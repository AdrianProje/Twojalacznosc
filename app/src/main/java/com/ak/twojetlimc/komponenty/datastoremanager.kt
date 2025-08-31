package com.ak.twojetlimc.komponenty

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ak.twojetlimc.planLekcji.Schedule
import com.ak.twojetlimc.planLekcji.deserializenewSchedule
import com.ak.twojetlimc.planLekcji.serializenewSchedule
import com.ak.twojetlimc.zastepstwa.Zastepstwo
import com.ak.twojetlimc.zastepstwa.deserializeZastepstwo
import com.ak.twojetlimc.zastepstwa.serializeZastepstwo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map


class Datastoremanager(private val context: Context) {
    companion object DataStoreKeys {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userSettings")
        val USER_DEFAULT_CHIP = intPreferencesKey("user_default_plan")

        val USER_PASS_OBBE = booleanPreferencesKey("user_pass_obbe")
        val USER_FAV_SCHEDULE_ONOFF = booleanPreferencesKey("user_fav_schedule_onoff")
        val PARANOIA = booleanPreferencesKey("paranoia")
        val USER_REFRESH = booleanPreferencesKey("user_refresh")
        val ONLINE_MODE = booleanPreferencesKey("online_mode")

        val PLAN_DATESTAMP = stringPreferencesKey("plan_data")
        val USER_FAV_SCHEDULE = stringPreferencesKey("user_fav_schedule")
        val SCHEDULE_PREFIX = stringPreferencesKey("schedule_")
        val ZASTEPSTWO_PREFIX = stringPreferencesKey("zastepstwo_")
    }

//    suspend fun editPreferences(action: suspend (MutablePreferences) -> Unit) {
//        context.dataStore.edit { preferences ->
//            action(preferences)
//        }
//    }

    val getUPObbe: Flow<Boolean?> =
        context.dataStore.data.map { preferences -> preferences[USER_PASS_OBBE] == true }

    suspend fun saveUPObbe(value: Boolean) {
        context.dataStore.edit { preferences -> preferences[USER_PASS_OBBE] = value }
    }

    val getParanoia: Flow<Boolean?> =
        context.dataStore.data.map { preferences -> preferences[PARANOIA] == true }

    suspend fun saveParanoia(value: Boolean) {
        context.dataStore.edit { preferences -> preferences[PARANOIA] = value }
    }

    val getUserRefresh: Flow<Boolean?> =
        context.dataStore.data.map { preferences -> preferences[USER_REFRESH] != false }

    suspend fun saveUserRefresh(value: Boolean) {
        context.dataStore.edit { preferences -> preferences[USER_REFRESH] = value }
    }

    val getDefaultPlan: Flow<Int?> =
        context.dataStore.data.map { preferences -> preferences[USER_DEFAULT_CHIP] ?: 0 }

    suspend fun saveDefaultPlan(value: Int) {
        context.dataStore.edit { preferences -> preferences[USER_DEFAULT_CHIP] = value }
    }

    val getFavSchedule: Flow<String?> =
        context.dataStore.data.map { preferences -> preferences[USER_FAV_SCHEDULE] ?: "" }

    val saveFavSchedule: suspend (String) -> Unit = { value ->
        context.dataStore.edit { preferences ->
            preferences[USER_FAV_SCHEDULE] = value
        }
    }

    val getFavScheduleOnOff: Flow<Boolean?> =
        context.dataStore.data.map { preferences -> preferences[USER_FAV_SCHEDULE_ONOFF] == true }

    suspend fun saveFavScheduleOnOff(value: Boolean) {
        context.dataStore.edit { preferences -> preferences[USER_FAV_SCHEDULE_ONOFF] = value }
    }

    val getPlanTimestamp: Flow<String?> =
        context.dataStore.data.map { preferences -> preferences[PLAN_DATESTAMP] ?: "" }

    suspend fun savePlanTimestamp(value: String) {
        context.dataStore.edit { preferences -> preferences[PLAN_DATESTAMP] = value }
    }

    val getOnlineMode: Flow<Boolean?> =
        context.dataStore.data.map { preferences -> preferences[ONLINE_MODE] != false }

    suspend fun saveOnlineMode(value: Boolean) {
        context.dataStore.edit { preferences -> preferences[ONLINE_MODE] = value }
    }

    suspend fun storenewSchedule(date: String, version: Int, listofschedules: List<Schedule>) {
        context.dataStore.edit { preferences ->
            val key = "$SCHEDULE_PREFIX$date$version"
            preferences[stringPreferencesKey(key)] = serializenewSchedule(listofschedules)
        }
    }

    suspend fun getnewSchedule(context: Context, date: String, version: Int): List<Schedule>? {
        return context.dataStore.data.firstOrNull()?.let { preferences ->
            val key = "$SCHEDULE_PREFIX$date$version"
            val jsonString = preferences[stringPreferencesKey(key)]
            if (jsonString != null) {
                deserializenewSchedule(jsonString) // Deserialize after retrieving
            } else {
                null
            }
        }
    }

    suspend fun getnewchosenClass(
        context: Context,
        date: String,
        html: String,
        version: Int
    ): Schedule {
        val wholeschedule = getnewSchedule(context, date, version)
        return wholeschedule!!.first { it.html == html }
    }


    suspend fun deleteDataFromStringPreferencesKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
        }
    }

    suspend fun compareTwoNewestSchedules(): Boolean? {
        val allPreferences = context.dataStore.data.firstOrNull() ?: return null

        // Filter keys that start with SCHEDULE_PREFIX and extract the date and version
        val scheduleKeysInfo = allPreferences.asMap().keys
            .filter { it.name.startsWith(SCHEDULE_PREFIX.name) }
            .mapNotNull { prefKey ->
                val keyName = prefKey.name
                val prefixRemoved = keyName.removePrefix(SCHEDULE_PREFIX.name)
                // Assuming the date is the first 10 characters and the rest is version
                if (prefixRemoved.length > 10) {
                    val date = prefixRemoved.substring(0, 10)
                    val versionString = prefixRemoved.substring(10)
                    // Ensure version part is a valid integer
                    try {
                        val version = versionString.toInt()
                        Triple(keyName, date, version)
                    } catch (e: NumberFormatException) {
                        null // Invalid version format
                    }
                } else {
                    null // Key format is not as expected
                }
            }

        // Sort by date descending, then by version descending to get the newest ones first
        val sortedSchedules = scheduleKeysInfo.sortedWith(
            compareByDescending<Triple<String, String, Int>> { it.second } // Sort by date (newest first)
                .thenByDescending { it.third } // Then by version (newest first)
        )

        return when (sortedSchedules.size) {
            0 -> true
            1 -> false
            else -> {
                // Get the two newest schedule keys
                val newestKeyName = sortedSchedules[0].first
                val secondNewestKeyName = sortedSchedules[1].first

                Log.d(
                    "DatastoreManager",
                    "Comparing schedules: $newestKeyName and $secondNewestKeyName"
                )


                // Retrieve the schedules using the full key name
                // Assuming your getnewSchedule can take the full key name or you adapt it
                // For this example, I'll directly access the preference value
                // You might need to adjust this part based on how you implemented getnewSchedule
                // or if you need to pass date and version separately

                val newestScheduleJson = allPreferences[stringPreferencesKey(newestKeyName)]
                val secondNewestScheduleJson =
                    allPreferences[stringPreferencesKey(secondNewestKeyName)]

                if (newestScheduleJson == null || secondNewestScheduleJson == null) {
                    Log.d(
                        "DatastoreManager",
                        "Could not retrieve one or both schedules for comparison."
                    )
                    return true // Should not happen if keys were found, but good for safety
                }

                // Deserialize the schedules
                // Ensure deserializenewSchedule is accessible here or pass context if needed
                val newestSchedule = deserializenewSchedule(newestScheduleJson)
                val secondNewestSchedule = deserializenewSchedule(secondNewestScheduleJson)

                // Compare the deserialized schedules
                // The comparison logic depends on how your Schedule class implements equals()
                // Or you can compare their serialized forms if that's sufficient,
                // but comparing the objects is generally more robust.
                val areEqual = newestSchedule == secondNewestSchedule

                Log.d("DatastoreManager", "Schedules are equal: $areEqual")
                Log.d("DatastoreManager", "Newest Schedule: $newestSchedule")
                Log.d("DatastoreManager", "Second Newest Schedule: $secondNewestSchedule")


                areEqual
            }
        }
    }

    suspend fun getAllScheduleNamesGroupedByDate(): Map<String, List<String>> {
        val allPreferences = context.dataStore.data.firstOrNull() ?: return emptyMap()
        val groupedSchedules = mutableMapOf<String, MutableList<String>>()

        val scheduleKeys = allPreferences.asMap().keys
            .filter { it.name.startsWith(SCHEDULE_PREFIX.name) }
            .map { it.name }

        for (keyName in scheduleKeys) {
            val prefixRemoved = keyName.removePrefix(SCHEDULE_PREFIX.name)
            if (prefixRemoved.length > 10) { // Ensure there's enough length for date and version
                val date = prefixRemoved.substring(0, 10) // YYYY-MM-DD
                val versionString = prefixRemoved.substring(10)

                try {
                    val version = versionString.toInt() // Or however you parse the version

                    // Fetch the list of schedules for this specific date and version
                    val schedulesList = getnewSchedule(context, date, version)

                    schedulesList?.forEach { schedule ->
                        // IMPORTANT: Replace 'schedule.name' with the actual property
                        // that holds the name in your Schedule object.
                        // If a single entry from getnewSchedule can have multiple "names"
                        // or if each Schedule object in the list is a distinct "schedule"
                        // you want to list, adjust accordingly.

                        // Example: Assuming Schedule class has a "name" property
                        val scheduleName =
                            schedule.imieinazwisko // Or schedule.title, schedule.className etc.

                        groupedSchedules.getOrPut(date) { mutableListOf() }.add(scheduleName)
                    }
                } catch (e: NumberFormatException) {
                    Log.e("DatastoreManager", "Could not parse version from key: $keyName", e)
                } catch (e: Exception) {
                    Log.e("DatastoreManager", "Error processing schedule key: $keyName", e)
                }
            } else {
                Log.w("DatastoreManager", "Skipping malformed schedule key: $keyName")
            }
        }
        // If you want to remove duplicate names for a given date:
        // return groupedSchedules.mapValues { it.value.distinct() }
        return groupedSchedules
    }

    suspend fun getZastepstwo(
        context: Context,
        lessonnumber: Int,
        klasa: String,
        dzien: String,
        version: Int
    ): Zastepstwo? {
        return context.dataStore.data.firstOrNull()?.let { preferences ->
            val key = "$ZASTEPSTWO_PREFIX$dzien$klasa$lessonnumber$version"
            val jsonString = preferences[stringPreferencesKey(key)]
            if (jsonString != null) {
                deserializeZastepstwo(jsonString) // Deserialize after retrieving
            } else {
                null
            }
        }
    }

    suspend fun storeZastepstwo(
        context: Context,
        lessonnumber: Int,
        klasa: String,
        dzien: String,
        version: Int,
        zastepstwo: Zastepstwo
    ) {
        context.dataStore.edit { preferences ->
            val key = "$ZASTEPSTWO_PREFIX$dzien$klasa$lessonnumber$version" // Corrected key
            preferences[stringPreferencesKey(key)] = serializeZastepstwo(zastepstwo)
        }
    }
}
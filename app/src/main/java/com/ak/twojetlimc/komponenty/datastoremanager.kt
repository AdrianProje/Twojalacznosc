package com.ak.twojetlimc.komponenty

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ak.twojetlimc.planLekcji.Schedule
import com.ak.twojetlimc.planLekcji.deserializeSchedule
import com.ak.twojetlimc.planLekcji.serializeSchedule
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

//        const val SCHEDULE_PREFIX = "schedule_"
//        const val ZASTEPSTWO_PREFIX = "zastepstwo_"
    }

    suspend fun editPreferences(action: suspend (MutablePreferences) -> Unit) {
        context.dataStore.edit { preferences ->
            action(preferences)
        }
    }

    val getUPObbe: Flow<Boolean?> =
        context.dataStore.data.map { preferences -> preferences[USER_PASS_OBBE] ?: false }

    suspend fun saveUPObbe(value: Boolean) {
        context.dataStore.edit { preferences -> preferences[USER_PASS_OBBE] = value }
    }

    val getParanoia: Flow<Boolean?> =
        context.dataStore.data.map { preferences -> preferences[PARANOIA] ?: false }

    suspend fun saveParanoia(value: Boolean) {
        context.dataStore.edit { preferences -> preferences[PARANOIA] = value }
    }

    val getUserRefresh: Flow<Boolean?> =
        context.dataStore.data.map { preferences -> preferences[USER_REFRESH] ?: false }

    suspend fun saveUserRefresh(value: Boolean) {
        context.dataStore.edit { preferences -> preferences[USER_REFRESH] = value }
    }

    val getDefaultPlan: Flow<Int?> =
        context.dataStore.data.map { preferences -> preferences[USER_DEFAULT_CHIP] ?: 1 }

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
        context.dataStore.data.map { preferences -> preferences[USER_FAV_SCHEDULE_ONOFF] ?: false }

    suspend fun saveFavScheduleOnOff(value: Boolean) {
        context.dataStore.edit { preferences -> preferences[USER_FAV_SCHEDULE_ONOFF] = value }
    }

    val getPlanTimestamp: Flow<String?> =
        context.dataStore.data.map { preferences -> preferences[PLAN_DATESTAMP] ?: "" }

    suspend fun savePlanTimestamp(value: String) {
        context.dataStore.edit { preferences -> preferences[PLAN_DATESTAMP] = value }
    }

    suspend fun deletePlanTimestamp(value: String) {
        context.dataStore.edit { preferences -> preferences.remove(PLAN_DATESTAMP) }
    }

    val getOnlineMode: Flow<Boolean?> =
        context.dataStore.data.map { preferences -> preferences[ONLINE_MODE] ?: true }

    suspend fun saveOnlineMode(value: Boolean) {
        context.dataStore.edit { preferences -> preferences[ONLINE_MODE] = value }
    }

    suspend fun getSchedule(context: Context, userId: String, version: Int): Schedule? {
        return context.dataStore.data.firstOrNull()?.let { preferences ->
            val key = "$SCHEDULE_PREFIX$userId$version"
            val jsonString = preferences[stringPreferencesKey(key)]
            if (jsonString != null) {
                deserializeSchedule(jsonString) // Deserialize after retrieving
            } else {
                null
            }
        }
    }

    suspend fun storeSchedule(context: Context, userId: String, version: Int, schedule: Schedule) {
        context.dataStore.edit { preferences ->
            val key = "$SCHEDULE_PREFIX$userId$version" // Corrected key
            preferences[stringPreferencesKey(key)] = serializeSchedule(schedule)
        }
    }

    suspend fun deleteDataFromStringPreferencesKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
        }
    }

    suspend fun getAllScheduleKeysGrouped(): Map<String, List<String>> {
        val allPreferences = context.dataStore.data.firstOrNull() ?: return emptyMap()

        return allPreferences.asMap().keys
            .filter { it.name.startsWith(SCHEDULE_PREFIX.toString()) }
            .map { it.name }
            .groupBy { key ->
                val parts = key.split("/")
                if (parts.isNotEmpty()) {
                    val datePart = parts[0].removePrefix("schedule_") // Remove "schedule_" prefix
                    datePart // Use the date part as the group key
                } else {
                    key // Fallback to the full key if it doesn't match the expected format
                }
            }
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
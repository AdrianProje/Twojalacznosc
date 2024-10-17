package com.ak.twojetlimc.komponenty

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ak.twojetlimc.PlanLekcji.Schedule
import com.ak.twojetlimc.PlanLekcji.deserializeSchedule
import com.ak.twojetlimc.PlanLekcji.serializeSchedule
import com.ak.twojetlimc.Zastepstwa.Zastepstwo
import com.ak.twojetlimc.Zastepstwa.deserializeZastepstwo
import com.ak.twojetlimc.Zastepstwa.serializeZastepstwo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class Datastoremanager(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userSettings")
        val USER_PASS_OBBE = booleanPreferencesKey("user_pass_obbe")
        val USER_DEFAULT_CHIP = intPreferencesKey("user_default_plan")
        val USER_FAV_SCHEDULE = stringPreferencesKey("user_fav_schedule")
        val USER_FAV_SCHEDULE_ONOFF = booleanPreferencesKey("user_fav_schedule_onoff")
        val PLAN_DATESTAMP = stringPreferencesKey("plan_data")

        const val SCHEDULE_PREFIX = "schedule_"
        const val ZASTEPSTWO_PREFIX = "zastepstwo_"
    }

    val getUPObbe: Flow<Boolean?> =
        context.dataStore.data.map { preferences -> preferences[USER_PASS_OBBE] ?: false }

    suspend fun saveUPObbe(value: Boolean) {
        context.dataStore.edit { preferences -> preferences[USER_PASS_OBBE] = value }
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
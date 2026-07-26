package com.example.busbashlog.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.busbashlog.model.LogEntry
import com.example.busbashlog.model.Vehicle
import com.example.busbashlog.network.BustimesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

private val Context.dataStore by preferencesDataStore("bash_log")

class LogRepository(private val context: Context) {
    private val api = BustimesApi.create()
    private val key = stringPreferencesKey("entries")
    private val json = Json { ignoreUnknownKeys = true }

    val entries: Flow<List<LogEntry>> = context.dataStore.data.map { prefs ->
        val raw = prefs[key] ?: "[]"
        try {
            json.decodeFromString<List<LogEntry>>(raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun lookup(query: String): Result<List<Vehicle>> = try {
        Result.success(api.searchVehicles(query.trim()).results)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun add(vehicle: Vehicle?, notes: String = "", lat: Double? = null, lon: Double? = null) {
        val entry = LogEntry(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            fleetCode = vehicle?.fleetCode ?: vehicle?.fleetNumber?.toString(),
            reg = vehicle?.reg,
            operatorName = vehicle?.operator?.name,
            typeName = vehicle?.vehicleType?.name,
            notes = notes,
            latitude = lat,
            longitude = lon
        )
        context.dataStore.edit { prefs ->
            val current = try {
                json.decodeFromString<List<LogEntry>>(prefs[key] ?: "[]")
            } catch (_: Exception) {
                emptyList()
            }
            prefs[key] = json.encodeToString(listOf(entry) + current)
        }
    }
}

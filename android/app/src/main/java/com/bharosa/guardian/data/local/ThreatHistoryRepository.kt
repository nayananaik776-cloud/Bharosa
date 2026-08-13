package com.bharosa.guardian.data.local

import android.content.Context
import android.content.SharedPreferences
import com.bharosa.guardian.model.ThreatLog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ThreatHistoryRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    private val logs = mutableListOf<ThreatLog>()

    init {
        loadLogs()
    }

    @Synchronized
    fun addLog(log: ThreatLog) {
        logs.add(0, log) // Add to top
        saveLogs()
    }

    @Synchronized
    fun getLogs(): List<ThreatLog> {
        return logs.toList()
    }

    @Synchronized
    fun clearLogs() {
        logs.clear()
        saveLogs()
    }

    private fun loadLogs() {
        val json = prefs.getString(KEY_LOGS, null)
        if (!json.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<List<ThreatLog>>() {}.type
                val saved: List<ThreatLog> = gson.fromJson(json, type) ?: emptyList()
                logs.clear()
                logs.addAll(saved)
            } catch (e: Exception) {
                logs.clear()
            }
        }
    }

    private fun saveLogs() {
        try {
            val json = gson.toJson(logs.take(50)) // Keep last 50
            prefs.edit().putString(KEY_LOGS, json).apply()
        } catch (e: Exception) {
            // Ignore write errors
        }
    }

    companion object {
        private const val PREFS_NAME = "bharosa_threat_history"
        private const val KEY_LOGS = "threat_logs_json"
    }
}

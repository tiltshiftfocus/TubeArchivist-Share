package me.imjerry.tubearchivistshare

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ActivityLogManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(
        "ActivityLogs",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()

    companion object {
        private const val KEY_LOGS = "logs"
        private const val MAX_LOGS = 100 // Keep last 100 entries
    }

    fun addLog(log: ActivityLog) {
        val logs = getLogs().toMutableList()
        logs.add(0, log) // Add to beginning (most recent first)
        
        // Keep only the most recent MAX_LOGS entries
        if (logs.size > MAX_LOGS) {
            logs.subList(MAX_LOGS, logs.size).clear()
        }
        
        saveLogs(logs)
    }

    fun updateLog(logId: Long, status: ActivityLog.Status, message: String = "") {
        val logs = getLogs().toMutableList()
        val index = logs.indexOfFirst { it.id == logId }
        
        if (index != -1) {
            val updatedLog = logs[index].copy(
                status = status,
                message = message
            )
            logs[index] = updatedLog
            saveLogs(logs)
        }
    }

    fun getLogs(): List<ActivityLog> {
        val json = sharedPreferences.getString(KEY_LOGS, null) ?: return emptyList()
        val type = object : TypeToken<List<ActivityLog>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearLogs() {
        sharedPreferences.edit().remove(KEY_LOGS).apply()
    }

    fun getLogById(logId: Long): ActivityLog? {
        return getLogs().firstOrNull { it.id == logId }
    }

    fun getSuccessCount(): Int {
        return getLogs().count { it.status == ActivityLog.Status.SUCCESS }
    }

    fun getFailedCount(): Int {
        return getLogs().count { it.status == ActivityLog.Status.FAILED }
    }

    fun getPendingCount(): Int {
        return getLogs().count { it.status == ActivityLog.Status.PENDING }
    }

    private fun saveLogs(logs: List<ActivityLog>) {
        val json = gson.toJson(logs)
        sharedPreferences.edit().putString(KEY_LOGS, json).apply()
    }
}

package me.imjerry.tubearchivistshare

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ActivityLog(
    val id: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis(),
    val youtubeUrl: String,
    val videoId: String,
    val status: Status,
    val message: String = ""
) {
    enum class Status {
        PENDING,
        SUCCESS,
        FAILED
    }

    fun getFormattedTimestamp(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun getStatusEmoji(): String {
        return when (status) {
            Status.PENDING -> "⏳"
            Status.SUCCESS -> "✓"
            Status.FAILED -> "✗"
        }
    }

    fun getStatusColor(): Int {
        return when (status) {
            Status.PENDING -> 0xFFFFA726.toInt() // Orange
            Status.SUCCESS -> 0xFF4CAF50.toInt() // Green
            Status.FAILED -> 0xFFF44336.toInt() // Red
        }
    }
}

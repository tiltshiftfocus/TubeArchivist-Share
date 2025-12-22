package me.imjerry.tubearchivistshare

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object TubeArchivistApi {
    private val executor = Executors.newSingleThreadExecutor()

    fun testConnection(
        serverUrl: String,
        apiToken: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        executor.execute {
            try {
                val url = URL("$serverUrl/api/ping/")
                val connection = url.openConnection() as HttpURLConnection
                
                connection.requestMethod = "GET"
                connection.setRequestProperty("Authorization", "Token $apiToken")
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    onSuccess()
                } else {
                    val errorStream = connection.errorStream
                    val errorMessage = if (errorStream != null) {
                        BufferedReader(InputStreamReader(errorStream)).use { it.readText() }
                    } else {
                        "HTTP $responseCode"
                    }
                    onError("Server returned: $errorMessage")
                }

                connection.disconnect()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    fun addToDownloadQueue(
        serverUrl: String,
        apiToken: String,
        youtubeUrl: String,
        autostart: Boolean = false,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        executor.execute {
            try {
                var urlString = "$serverUrl/api/download/"
                if (autostart) {
                    urlString = "$urlString?autostart=true"
                }
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                
                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", "Token $apiToken")
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                // Create JSON payload
                val jsonPayload = JSONObject().apply {
                    put("data", org.json.JSONArray().apply {
                        put(JSONObject().apply {
                            put("youtube_id", extractVideoId(youtubeUrl))
                            put("status", "pending")
                        })
                    })

                }

                // Write payload
                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(jsonPayload.toString())
                    writer.flush()
                }

                val responseCode = connection.responseCode

                when (responseCode) {
                    HttpURLConnection.HTTP_OK,
                    HttpURLConnection.HTTP_CREATED -> {
                        onSuccess()
                    }
                    else -> {
                        val errorStream = connection.errorStream
                        val errorMessage = if (errorStream != null) {
                            BufferedReader(InputStreamReader(errorStream)).use { it.readText() }
                        } else {
                            "HTTP $responseCode"
                        }
                        onError("Server returned: $errorMessage")
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    fun addMultipleToDownloadQueue(
        serverUrl: String,
        apiToken: String,
        youtubeUrls: List<String>,
        autostart: Boolean = false,
        logManager: ActivityLogManager,
        onProgress: (current: Int, total: Int) -> Unit,
        onComplete: (successCount: Int, failedCount: Int) -> Unit
    ) {
        executor.execute {
            var successCount = 0
            var failedCount = 0
            val total = youtubeUrls.size

            for ((index, youtubeUrl) in youtubeUrls.withIndex()) {
                val videoId = extractVideoId(youtubeUrl)
                
                // Create log entry
                val logEntry = ActivityLog(
                    youtubeUrl = youtubeUrl,
                    videoId = videoId,
                    status = ActivityLog.Status.PENDING,
                    message = "Bulk add: sending to queue..."
                )
                logManager.addLog(logEntry)

                try {
                    var urlString = "$serverUrl/api/download/"
                    if (autostart) {
                        urlString = "$urlString?autostart=true"
                    }
                    val url = URL(urlString)
                    val connection = url.openConnection() as HttpURLConnection
                    
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Authorization", "Token $apiToken")
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.doOutput = true
                    connection.connectTimeout = 15000
                    connection.readTimeout = 15000

                    val jsonPayload = JSONObject().apply {
                        put("data", org.json.JSONArray().apply {
                            put(JSONObject().apply {
                                put("youtube_id", videoId)
                                put("status", "pending")
                            })
                        })

                    }

                    OutputStreamWriter(connection.outputStream).use { writer ->
                        writer.write(jsonPayload.toString())
                        writer.flush()
                    }

                    val responseCode = connection.responseCode

                    when (responseCode) {
                        HttpURLConnection.HTTP_OK,
                        HttpURLConnection.HTTP_CREATED -> {
                            successCount++
                            logManager.updateLog(
                                logEntry.id,
                                ActivityLog.Status.SUCCESS,
                                "Successfully added to download queue (autostart: $autostart)"
                            )
                        }
                        else -> {
                            failedCount++
                            val errorStream = connection.errorStream
                            val errorMessage = if (errorStream != null) {
                                BufferedReader(InputStreamReader(errorStream)).use { it.readText() }
                            } else {
                                "HTTP $responseCode"
                            }
                            logManager.updateLog(
                                logEntry.id,
                                ActivityLog.Status.FAILED,
                                "Error: $errorMessage"
                            )
                        }
                    }

                    connection.disconnect()
                } catch (e: Exception) {
                    failedCount++
                    logManager.updateLog(
                        logEntry.id,
                        ActivityLog.Status.FAILED,
                        "Error: ${e.message ?: "Unknown error"}"
                    )
                }

                onProgress(index + 1, total)
            }

            onComplete(successCount, failedCount)
        }
    }

    private fun extractVideoId(url: String): String {
        // Handle different YouTube URL formats
        val patterns = listOf(
            Regex("""(?:youtube\.com/watch\?v=|youtu\.be/)([a-zA-Z0-9_-]{11})"""),
            Regex("""youtube\.com/embed/([a-zA-Z0-9_-]{11})"""),
            Regex("""youtube\.com/v/([a-zA-Z0-9_-]{11})""")
        )

        for (pattern in patterns) {
            val match = pattern.find(url)
            if (match != null) {
                return match.groupValues[1]
            }
        }

        // If no pattern matches, return the URL as-is (let server handle it)
        return url
    }
}

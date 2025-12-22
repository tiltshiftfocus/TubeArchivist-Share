package me.imjerry.tubearchivistshare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ShareHandlerActivity : AppCompatActivity() {
    private lateinit var preferences: AppPreferences
    private lateinit var logManager: ActivityLogManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = AppPreferences(this)
        logManager = ActivityLogManager(this)

        when (intent?.action) {
            Intent.ACTION_SEND -> {
                handleSendText(intent)
            }
            Intent.ACTION_VIEW -> {
                handleViewUrl(intent)
            }
            else -> {
                Toast.makeText(this, "Unsupported action", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let { sharedText ->
            val youtubeUrl = extractYouTubeUrl(sharedText)
            if (youtubeUrl != null) {
                sendToTubeArchivist(youtubeUrl)
            } else {
                Toast.makeText(this, "No YouTube URL found", Toast.LENGTH_SHORT).show()
                finish()
            }
        } ?: run {
            Toast.makeText(this, "No text shared", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun handleViewUrl(intent: Intent) {
        intent.data?.let { uri ->
            val youtubeUrl = uri.toString()
            if (isYouTubeUrl(youtubeUrl)) {
                sendToTubeArchivist(youtubeUrl)
            } else {
                Toast.makeText(this, "Not a YouTube URL", Toast.LENGTH_SHORT).show()
                finish()
            }
        } ?: run {
            Toast.makeText(this, "No URL provided", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun extractYouTubeUrl(text: String): String? {
        // Try to find YouTube URL in the text
        val urlPattern = Regex("""(https?://(?:www\.|m\.)?(?:youtube\.com|youtu\.be)/[^\s]+)""")
        val match = urlPattern.find(text)
        return match?.value?.let { url ->
            if (isYouTubeUrl(url)) normalizeYouTubeUrl(url) else null
        }
    }

    private fun isYouTubeUrl(url: String): Boolean {
        val uri = Uri.parse(url)
        val host = uri.host?.lowercase() ?: return false
        return host.contains("youtube.com") || host.contains("youtu.be")
    }

    private fun normalizeYouTubeUrl(url: String): String {
        val uri = Uri.parse(url)
        
        // Handle youtu.be short links
        if (uri.host?.contains("youtu.be") == true) {
            val videoId = uri.pathSegments.firstOrNull()
            return "https://www.youtube.com/watch?v=$videoId"
        }
        
        // Return as-is for regular YouTube URLs
        return url
    }

    private fun sendToTubeArchivist(youtubeUrl: String) {
        val serverUrl = preferences.getServerUrl()
        val apiToken = preferences.getApiToken()

        if (serverUrl.isEmpty() || apiToken.isEmpty()) {
            Toast.makeText(
                this,
                "Please configure TubeArchivist settings first",
                Toast.LENGTH_LONG
            ).show()
            
            // Open main activity to configure
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Extract video ID for logging
        val videoId = extractVideoIdFromUrl(youtubeUrl)
        
        // Create log entry
        val logEntry = ActivityLog(
            youtubeUrl = youtubeUrl,
            videoId = videoId,
            status = ActivityLog.Status.PENDING,
            message = "Sending to TubeArchivist..."
        )
        logManager.addLog(logEntry)

        Toast.makeText(this, "Adding to download queue...", Toast.LENGTH_SHORT).show()

        val autostart = preferences.getAutostart()

        TubeArchivistApi.addToDownloadQueue(
            serverUrl = serverUrl,
            apiToken = apiToken,
            youtubeUrl = youtubeUrl,
            autostart = autostart,
            onSuccess = {
                runOnUiThread {
                    logManager.updateLog(
                        logEntry.id,
                        ActivityLog.Status.SUCCESS,
                        "Successfully added to download queue (autostart: $autostart)"
                    )
                    Toast.makeText(
                        this,
                        "✓ Added to TubeArchivist queue",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            },
            onError = { error ->
                runOnUiThread {
                    logManager.updateLog(
                        logEntry.id,
                        ActivityLog.Status.FAILED,
                        "Error: $error"
                    )
                    Toast.makeText(
                        this,
                        "✗ Failed: $error",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        )
    }

    private fun extractVideoIdFromUrl(url: String): String {
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

        return "Unknown"
    }
}

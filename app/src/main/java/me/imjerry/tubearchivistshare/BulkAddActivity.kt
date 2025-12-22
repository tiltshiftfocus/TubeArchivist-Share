package me.imjerry.tubearchivistshare

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class BulkAddActivity : AppCompatActivity() {
    private lateinit var preferences: AppPreferences
    private lateinit var logManager: ActivityLogManager
    private lateinit var urlInput: EditText
    private lateinit var addButton: Button
    private lateinit var clearButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var countText: TextView

    private var isProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bulk_add)

        // Set up toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferences = AppPreferences(this)
        logManager = ActivityLogManager(this)

        urlInput = findViewById(R.id.urlInput)
        addButton = findViewById(R.id.addToQueueButton)
        clearButton = findViewById(R.id.clearInputButton)
        progressBar = findViewById(R.id.progressBar)
        statusText = findViewById(R.id.statusText)
        countText = findViewById(R.id.countText)

        urlInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                updateUrlCount()
            }
        })

        addButton.setOnClickListener {
            addVideosToQueue()
        }

        clearButton.setOnClickListener {
            urlInput.text.clear()
            statusText.text = ""
            updateUrlCount()
        }

        updateUrlCount()
    }

    private fun updateUrlCount() {
        val urls = extractUrls(urlInput.text.toString())
        countText.text = "${urls.size} video(s) detected"
    }

    private fun addVideosToQueue() {
        if (isProcessing) return

        val serverUrl = preferences.getServerUrl()
        val apiToken = preferences.getApiToken()

        if (serverUrl.isEmpty() || apiToken.isEmpty()) {
            Toast.makeText(
                this,
                "Please configure TubeArchivist settings first",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val inputText = urlInput.text.toString().trim()
        if (inputText.isEmpty()) {
            Toast.makeText(this, "Please enter at least one YouTube URL", Toast.LENGTH_SHORT).show()
            return
        }

        val urls = extractUrls(inputText)
        if (urls.isEmpty()) {
            Toast.makeText(this, "No valid YouTube URLs found", Toast.LENGTH_SHORT).show()
            return
        }

        isProcessing = true
        setUIProcessing(true)

        statusText.text = "Processing ${urls.size} video(s)..."

        val autostart = preferences.getAutostart()

        TubeArchivistApi.addMultipleToDownloadQueue(
            serverUrl = serverUrl,
            apiToken = apiToken,
            youtubeUrls = urls,
            autostart = autostart,
            logManager = logManager,
            onProgress = { current, total ->
                runOnUiThread {
                    progressBar.progress = (current.toFloat() / total * 100).toInt()
                    statusText.text = "Processing $current of $total..."
                }
            },
            onComplete = { successCount, failedCount ->
                runOnUiThread {
                    isProcessing = false
                    setUIProcessing(false)
                    
                    val message = buildString {
                        append("Completed!\n")
                        append("✓ Success: $successCount\n")
                        if (failedCount > 0) {
                            append("✗ Failed: $failedCount\n")
                        }
                        append("\nCheck Activity Log for details")
                    }
                    
                    statusText.text = message
                    
                    Toast.makeText(
                        this,
                        "Added $successCount video(s) to queue",
                        Toast.LENGTH_LONG
                    ).show()
                    
                    // Clear input on success
                    if (successCount > 0 && failedCount == 0) {
                        urlInput.text.clear()
                    }
                }
            }
        )
    }

    private fun extractUrls(text: String): List<String> {
        val urls = mutableListOf<String>()
        val lines = text.split("\n")
        
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue
            
            // Try to find YouTube URL in the line
            val urlPattern = Regex("""(https?://(?:www\.|m\.)?(?:youtube\.com|youtu\.be)/[^\s]+)""")
            val matches = urlPattern.findAll(trimmed)
            
            for (match in matches) {
                val url = match.value
                if (isYouTubeUrl(url)) {
                    urls.add(url)
                }
            }
            
            // If no URL pattern found but line looks like a YouTube URL
            if (matches.count() == 0 && isYouTubeUrl(trimmed)) {
                urls.add(trimmed)
            }
        }
        
        return urls.distinct() // Remove duplicates
    }

    private fun isYouTubeUrl(url: String): Boolean {
        return url.contains("youtube.com") || url.contains("youtu.be")
    }

    private fun setUIProcessing(processing: Boolean) {
        addButton.isEnabled = !processing
        clearButton.isEnabled = !processing
        urlInput.isEnabled = !processing
        progressBar.visibility = if (processing) View.VISIBLE else View.GONE

        if (processing) {
            progressBar.progress = 0
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

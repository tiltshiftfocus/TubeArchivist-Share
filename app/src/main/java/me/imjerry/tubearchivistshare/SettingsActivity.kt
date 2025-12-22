package me.imjerry.tubearchivistshare

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {
    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        preferences = AppPreferences(this)

        // Set up toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val serverUrlInput = findViewById<EditText>(R.id.serverUrlInput)
        val apiTokenInput = findViewById<EditText>(R.id.apiTokenInput)
        val autostartCheckbox = findViewById<CheckBox>(R.id.autostartCheckbox)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val testButton = findViewById<Button>(R.id.testConnectionButton)

        // Load existing settings
        serverUrlInput.setText(preferences.getServerUrl())
        apiTokenInput.setText(preferences.getApiToken())
        autostartCheckbox.isChecked = preferences.getAutostart()

        saveButton.setOnClickListener {
            val serverUrl = serverUrlInput.text.toString().trim().trimEnd('/')
            val apiToken = apiTokenInput.text.toString().trim()

            if (serverUrl.isEmpty()) {
                Toast.makeText(this, "Please enter a server URL", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (apiToken.isEmpty()) {
                Toast.makeText(this, "Please enter an API token", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!serverUrl.startsWith("http://") && !serverUrl.startsWith("https://")) {
                Toast.makeText(
                    this,
                    "Server URL must start with http:// or https://",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            preferences.setServerUrl(serverUrl)
            preferences.setApiToken(apiToken)
            preferences.setAutostart(autostartCheckbox.isChecked)

            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
            finish()
        }

        testButton.setOnClickListener {
            val serverUrl = serverUrlInput.text.toString().trim().trimEnd('/')
            val apiToken = apiTokenInput.text.toString().trim()

            if (serverUrl.isEmpty() || apiToken.isEmpty()) {
                Toast.makeText(this, "Please enter both server URL and API token", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Testing connection...", Toast.LENGTH_SHORT).show()

            TubeArchivistApi.testConnection(
                serverUrl = serverUrl,
                apiToken = apiToken,
                onSuccess = {
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "✓ Connection successful!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                onError = { error ->
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "✗ Connection failed: $error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

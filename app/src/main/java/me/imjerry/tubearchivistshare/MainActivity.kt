package me.imjerry.tubearchivistshare

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var preferences: AppPreferences
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = AppPreferences(this)

        // Set up toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up navigation drawer
        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val statusText = findViewById<TextView>(R.id.statusText)
        val testButton = findViewById<Button>(R.id.testButton)

        updateStatusText(statusText)

        testButton.setOnClickListener {
            testConnection(statusText)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        val statusText = findViewById<TextView>(R.id.statusText)
        updateStatusText(statusText)
    }

    private fun updateStatusText(statusText: TextView) {
        val serverUrl = preferences.getServerUrl()
        val apiToken = preferences.getApiToken()

        val status = when {
            serverUrl.isEmpty() && apiToken.isEmpty() -> "⚠️ Not configured\n\nPlease configure your TubeArchivist server settings"
            serverUrl.isEmpty() -> "⚠️ Server URL not set"
            apiToken.isEmpty() -> "⚠️ API Token not set"
            else -> "✓ Configured\n\nServer: $serverUrl\n\nReady to receive YouTube shares"
        }

        statusText.text = status
    }

    private fun testConnection(statusText: TextView) {
        val serverUrl = preferences.getServerUrl()
        val apiToken = preferences.getApiToken()

        if (serverUrl.isEmpty() || apiToken.isEmpty()) {
            statusText.text = "⚠️ Please configure settings first"
            return
        }

        statusText.text = "Testing connection..."

        TubeArchivistApi.testConnection(
            serverUrl = serverUrl,
            apiToken = apiToken,
            onSuccess = {
                runOnUiThread {
                    statusText.text = "✓ Connection successful!\n\nServer: $serverUrl"
                }
            },
            onError = { error ->
                runOnUiThread {
                    statusText.text = "✗ Connection failed\n\n$error"
                }
            }
        )
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_bulk_add -> {
                startActivity(Intent(this, BulkAddActivity::class.java))
            }
            R.id.nav_activity_log -> {
                startActivity(Intent(this, ActivityLogActivity::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

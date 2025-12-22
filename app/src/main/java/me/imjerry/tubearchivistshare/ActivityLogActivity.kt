package me.imjerry.tubearchivistshare

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class ActivityLogActivity : AppCompatActivity() {
    private lateinit var logManager: ActivityLogManager
    private lateinit var adapter: ActivityLogAdapter
    private lateinit var emptyView: TextView
    private lateinit var statsView: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        // Set up toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        logManager = ActivityLogManager(this)

        recyclerView = findViewById(R.id.logRecyclerView)
        emptyView = findViewById(R.id.emptyLogView)
        statsView = findViewById(R.id.statsView)
        val clearButton = findViewById<Button>(R.id.clearLogsButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ActivityLogAdapter(logManager.getLogs())
        recyclerView.adapter = adapter

        updateUI()

        clearButton.setOnClickListener {
            showClearConfirmation()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshLogs()
    }

    private fun refreshLogs() {
        adapter.updateLogs(logManager.getLogs())
        updateUI()
    }

    private fun updateUI() {
        val logs = logManager.getLogs()
        
        if (logs.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            statsView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            statsView.visibility = View.VISIBLE
            
            val successCount = logManager.getSuccessCount()
            val failedCount = logManager.getFailedCount()
            val pendingCount = logManager.getPendingCount()
            val totalCount = logs.size
            
            statsView.text = "Total: $totalCount  |  ✓ $successCount  |  ✗ $failedCount  |  ⏳ $pendingCount"
        }
    }

    private fun showClearConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Clear Activity Log")
            .setMessage("Are you sure you want to clear all log entries?")
            .setPositiveButton("Clear") { _, _ ->
                logManager.clearLogs()
                refreshLogs()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

class ActivityLogAdapter(private var logs: List<ActivityLog>) : 
    RecyclerView.Adapter<ActivityLogAdapter.LogViewHolder>() {

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val statusIcon: TextView = view.findViewById(R.id.statusIcon)
        val timestamp: TextView = view.findViewById(R.id.timestamp)
        val videoId: TextView = view.findViewById(R.id.videoId)
        val url: TextView = view.findViewById(R.id.url)
        val message: TextView = view.findViewById(R.id.message)
        val statusIndicator: View = view.findViewById(R.id.statusIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = logs[position]
        
        holder.statusIcon.text = log.getStatusEmoji()
        holder.timestamp.text = log.getFormattedTimestamp()
        holder.videoId.text = "Video ID: ${log.videoId}"
        holder.url.text = log.youtubeUrl
        holder.statusIndicator.setBackgroundColor(log.getStatusColor())
        
        if (log.message.isNotEmpty()) {
            holder.message.visibility = View.VISIBLE
            holder.message.text = log.message
        } else {
            holder.message.visibility = View.GONE
        }
    }

    override fun getItemCount() = logs.size

    fun updateLogs(newLogs: List<ActivityLog>) {
        logs = newLogs
        notifyDataSetChanged()
    }
}

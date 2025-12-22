package me.imjerry.tubearchivistshare

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "TubeArchivistPrefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_API_TOKEN = "api_token"
        private const val KEY_AUTOSTART = "autostart"
    }

    fun setServerUrl(url: String) {
        sharedPreferences.edit().putString(KEY_SERVER_URL, url).apply()
    }

    fun getServerUrl(): String {
        return sharedPreferences.getString(KEY_SERVER_URL, "") ?: ""
    }

    fun setApiToken(token: String) {
        sharedPreferences.edit().putString(KEY_API_TOKEN, token).apply()
    }

    fun getApiToken(): String {
        return sharedPreferences.getString(KEY_API_TOKEN, "") ?: ""
    }

    fun setAutostart(autostart: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_AUTOSTART, autostart).apply()
    }

    fun getAutostart(): Boolean {
        return sharedPreferences.getBoolean(KEY_AUTOSTART, false)
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}

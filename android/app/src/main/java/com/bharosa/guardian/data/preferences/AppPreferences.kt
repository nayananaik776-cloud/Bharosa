package com.bharosa.guardian.data.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var selectedLanguage: String
        get() = prefs.getString(KEY_LANGUAGE, "hi") ?: "hi"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    var backendUrl: String
        get() = prefs.getString(KEY_BACKEND_URL, DEFAULT_BACKEND_URL) ?: DEFAULT_BACKEND_URL
        set(value) = prefs.edit().putString(KEY_BACKEND_URL, value).apply()

    var isProtectionEnabled: Boolean
        get() = prefs.getBoolean(KEY_PROTECTION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_PROTECTION_ENABLED, value).apply()

    var scannedCount: Int
        get() = prefs.getInt(KEY_SCANNED_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_SCANNED_COUNT, value).apply()

    var threatsBlockedCount: Int
        get() = prefs.getInt(KEY_THREATS_BLOCKED, 0)
        set(value) = prefs.edit().putInt(KEY_THREATS_BLOCKED, value).apply()

    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_DONE, value).apply()

    fun incrementScanned() {
        scannedCount += 1
    }

    fun incrementBlocked() {
        threatsBlockedCount += 1
    }

    companion object {
        private const val PREFS_NAME = "bharosa_guardian_prefs"
        private const val KEY_LANGUAGE = "selected_language"
        private const val KEY_BACKEND_URL = "backend_url"
        private const val KEY_PROTECTION_ENABLED = "protection_enabled"
        private const val KEY_SCANNED_COUNT = "scanned_count"
        private const val KEY_THREATS_BLOCKED = "threats_blocked"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"

        const val DEFAULT_BACKEND_URL = "http://10.0.2.2:8000"
    }
}

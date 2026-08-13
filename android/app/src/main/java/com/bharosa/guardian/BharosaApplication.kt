package com.bharosa.guardian

import android.app.Application
import com.bharosa.guardian.data.local.ThreatHistoryRepository
import com.bharosa.guardian.data.preferences.AppPreferences
import com.bharosa.guardian.service.TtsManager

class BharosaApplication : Application() {

    lateinit var preferences: AppPreferences
        private set

    lateinit var ttsManager: TtsManager
        private set

    lateinit var threatRepository: ThreatHistoryRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferences = AppPreferences(this)
        ttsManager = TtsManager(this)
        threatRepository = ThreatHistoryRepository(this)
    }

    companion object {
        lateinit var instance: BharosaApplication
            private set
    }
}

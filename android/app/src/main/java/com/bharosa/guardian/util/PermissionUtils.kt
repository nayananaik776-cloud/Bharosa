package com.bharosa.guardian.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.bharosa.guardian.service.GuardianNotificationListener

object PermissionUtils {

    fun isNotificationListenerGranted(context: Context): Boolean {
        val packageName = context.packageName
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        ) ?: return false

        val names = flat.split(":")
        val componentName = ComponentName(context, GuardianNotificationListener::class.java).flattenToString()

        for (name in names) {
            if (name.equals(componentName, ignoreCase = true) || name.contains(packageName)) {
                return true
            }
        }
        return false
    }

    fun getNotificationListenerSettingsIntent(): Intent {
        return Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    }
}

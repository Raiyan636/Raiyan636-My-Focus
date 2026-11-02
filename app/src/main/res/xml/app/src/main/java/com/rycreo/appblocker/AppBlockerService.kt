package com.rycreo.appblocker

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class AppBlockerService : AccessibilityService() {
    private val blockedApps = listOf("com.facebook.katana", "com.instagram.android", "com.zhiliaoapp.musically")

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()
            if (packageName != null && blockedApps.contains(packageName)) {
                val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(homeIntent)
                Toast.makeText(this, "Rycreo Blocker: App Restricted!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onInterrupt() {}
}

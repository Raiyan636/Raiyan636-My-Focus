package com.rycreo.appblocker
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
class BootReceiver : BroadcastReceiver() {
 override fun onReceive(context: Context, intent: Intent?) {
  // Preserve an active focus session across reboot. The AccessibilityService
  // reconnects when Android restores the user's enabled accessibility services.
  // The original wall-clock deadline remains stored in SharedPreferences.
  if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
 }
}
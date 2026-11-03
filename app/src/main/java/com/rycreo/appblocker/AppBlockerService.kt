package com.rycreo.appblocker
import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
class AppBlockerService : AccessibilityService() {
 private var lastLaunch = 0L
 override fun onAccessibilityEvent(event: AccessibilityEvent?) {
  if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
  val opened = event.packageName?.toString() ?: return
  if (opened == packageName) return
  val p = getSharedPreferences("focus", MODE_PRIVATE)
  val until = p.getLong("until", 0L)
  if (until <= System.currentTimeMillis()) {
   if (until != 0L) p.edit().remove("until").remove("active").apply()
   return
  }
  val blocked = p.getStringSet("active", emptySet()) ?: emptySet()
  if (!blocked.contains(opened)) return
  val now = SystemClock.elapsedRealtime()
  if (now - lastLaunch < 1200L) return
  lastLaunch = now
  startActivity(Intent(this, BlockedActivity::class.java).apply {
   addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
   putExtra("blocked", opened)
  })
 }
 override fun onInterrupt() = Unit
}
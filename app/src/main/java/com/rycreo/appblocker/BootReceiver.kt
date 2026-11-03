package com.rycreo.appblocker
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
class BootReceiver : BroadcastReceiver() {
 override fun onReceive(context: Context, intent: Intent?) {
  if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
   context.getSharedPreferences("focus", Context.MODE_PRIVATE).edit()
    .remove("until").remove("active").apply()
  }
 }
}
package com.rycreo.appblocker
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
class BlockedActivity : Activity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val p = intent.getStringExtra("blocked") ?: ""
  val name = try { packageManager.getApplicationLabel(packageManager.getApplicationInfo(p, 0)).toString() } catch (_: Exception) { "This app" }
  val box = LinearLayout(this).apply { orientation=1; gravity=Gravity.CENTER; setPadding(30,30,30,30) }
  box.addView(TextView(this).apply { text="$name is blocked. Stay focused!"; textSize=24f; gravity=Gravity.CENTER })
  box.addView(Button(this).apply { text="Go to Home"; setOnClickListener { goHome() } })
  setContentView(box)
 }
 @Suppress("DEPRECATION") override fun onBackPressed() { goHome() }
 private fun goHome() { startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); finish() }
}
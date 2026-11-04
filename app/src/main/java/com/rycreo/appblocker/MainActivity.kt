package com.rycreo.appblocker
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.*
class MainActivity : Activity() {
 private val p by lazy { getSharedPreferences("focus", MODE_PRIVATE) }
 private lateinit var box: LinearLayout
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); show() }
 override fun onResume() { super.onResume(); if (::box.isInitialized) show() }
 private fun show() {
  box = LinearLayout(this).apply { orientation=1; setPadding(24,24,24,24) }
  setContentView(ScrollView(this).apply { addView(box) })
  addText("Rycreo Focus", 26f)
  val until=p.getLong("until",0L)
  val active=p.getStringSet("active",emptySet()) ?: emptySet()
  if (until>System.currentTimeMillis() && active.isNotEmpty()) {
   addText("BLOCKING ACTIVE",22f)
   addText("Remaining: ${((until-System.currentTimeMillis())/60000L).coerceAtLeast(1)} minutes",16f)
   addText("Your focus session cannot be stopped before the timer ends.",14f)
   return
  }
  if (until!=0L || active.isNotEmpty()) p.edit().remove("until").remove("active").apply()
  addText("Duration in minutes (1–900)",16f)
  val duration=EditText(this).apply { inputType=android.text.InputType.TYPE_CLASS_NUMBER; setText("60") }
  box.addView(duration,params())
  addText("Select apps to block",18f)
  val chosen=(p.getStringSet("selected",emptySet()) ?: emptySet()).toMutableSet()
  packageManager.getInstalledApplications(0).filter {
   it.packageName!=packageName && packageManager.getLaunchIntentForPackage(it.packageName)!=null
  }.sortedBy { packageManager.getApplicationLabel(it).toString().lowercase() }.forEach { app ->
   box.addView(CheckBox(this).apply {
    text=packageManager.getApplicationLabel(app); isChecked=chosen.contains(app.packageName)
    setOnCheckedChangeListener { _, checked ->
     if (checked) chosen.add(app.packageName) else chosen.remove(app.packageName)
     p.edit().putStringSet("selected",chosen.toSet()).apply()
    }
   },params())
  }
  addButton("Start blocking now") {
   val mins=duration.text.toString().toLongOrNull()
   if (mins==null || mins<1 || mins>900) { toast("Enter 1 to 900 minutes"); return@addButton }
   val apps=p.getStringSet("selected",emptySet()) ?: emptySet()
   if (apps.isEmpty()) { toast("Select at least one app"); return@addButton }
   if (!enabled()) { toast("Enable Rycreo Focus in Accessibility settings"); startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)); return@addButton }
   p.edit().putStringSet("active",apps.toSet()).putLong("until",System.currentTimeMillis()+mins*60000L).apply()
   show()
  }
  addText("This first build blocks whole apps. Reels and Shorts detection is not included yet.",13f)
 }
 private fun enabled(): Boolean {
  val s=Settings.Secure.getString(contentResolver,Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
  val target=ComponentName(this,AppBlockerService::class.java).flattenToString()
  return s.split(':').any { it.equals(target,true) }
 }
 private fun addText(s:String,n:Float) { box.addView(TextView(this).apply { text=s; textSize=n; setPadding(0,10,0,10) },params()) }
 private fun addButton(s:String,f:()->Unit) { box.addView(Button(this).apply { text=s; setOnClickListener { f() } },params()) }
 private fun params()=LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
 private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_LONG).show()
}
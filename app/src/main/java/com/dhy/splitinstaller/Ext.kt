package com.dhy.splitinstaller

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

fun Activity.startComponent(className: String) {
//    val intent = Intent().setClassName(packageName, className)
    val intent = Intent(this, Class.forName(className))
    startActivity(intent)
}

@SuppressLint("PrivateApi")
fun Context.clearApplicationUserData() {
    try {
        val observer = Class.forName("android.content.pm.IPackageDataObserver")
        val clearApp = ActivityManager::class.java.getMethod("clearApplicationUserData", String::class.java, observer)
        val am = getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        clearApp.invoke(am, packageName, null)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
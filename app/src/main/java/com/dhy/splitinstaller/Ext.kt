package com.dhy.splitinstaller

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Process
import androidx.appcompat.app.AppCompatActivity

fun Activity.startComponent(className: String) {
    val intent = Intent().setClassName(packageName, className)
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

internal fun Context.restartApp() {
    if (this is Activity) finish()

    val mainPid = Process.myPid()
    val am = getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
    am.runningAppProcesses?.forEach { processInfo ->
        if (processInfo.pid != mainPid) Process.killProcess(processInfo.pid)
    }

    val intent = packageManager.getLaunchIntentForPackage(packageName)!!
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(intent)

    Process.killProcess(mainPid)
}
package com.donalddu.splitinstaller

import android.content.pm.ApplicationInfo
import android.os.Build
import android.util.Log
import com.donalddu.splitinstaller.SplitInstaller.TAG
import java.io.File
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method


internal class HookGetApplicationInfo(private val origin: Any) : InvocationHandler {
    val splits: MutableSet<File> = mutableSetOf()
    private var splitNames: Array<String>? = null
    private var splitDirs: Array<String>? = null

    override fun invoke(proxy: Any?, method: Method, args: Array<Any>): Any? {
        //mPM.getPackageInfo(packageName, flags, mContext.getUserId())
        return if (method.name == "getApplicationInfo") {
            Log.i(TAG, "Hooking getApplicationInfo")
            val info = method.invoke(origin, *args) as ApplicationInfo
            loadSplits(info)
            info
        } else {
            method.invoke(origin, *args)
        }
    }

    private fun loadSplits(info: ApplicationInfo) {
        if (info.splitSourceDirs.contentEquals(splitDirs)) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            info.splitNames = splitNames
        }
        info.splitSourceDirs = splitDirs
        info.splitPublicSourceDirs = splitDirs
    }

    fun addSplits(splits: Set<File>) {
        if (this.splits.containsAll(splits)) return

        this.splits.addAll(splits)
        splitNames = this.splits.map { it.name }.toTypedArray()
        splitDirs = this.splits.map { it.absolutePath }.toTypedArray()
    }
}
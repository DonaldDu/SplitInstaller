package com.donalddu.splitinstaller

import android.content.pm.ApplicationInfo
import android.os.Build
import android.util.Log
import com.donalddu.splitinstaller.SplitInstaller.TAG
import java.io.File


internal class HookGetApplicationInfo(origin: Any, hostPackageName: String) : ApplicationInfoIH(origin, hostPackageName) {
    val splits: MutableSet<File> = mutableSetOf()
    private var splitNames: Array<String>? = null
    private var splitDirs: Array<String>? = null

    override fun loadSplits(info: ApplicationInfo) {
        if (info.splitSourceDirs.contentEquals(splitDirs)) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            info.splitNames = splitNames
        }
        info.splitSourceDirs = splitDirs
        info.splitPublicSourceDirs = splitDirs
    }

    fun addSplits(splits: Set<File>) {
        if (this.splits.containsAll(splits)) return
        Log.i(TAG, "Hooking getApplicationInfo")
        this.splits.addAll(splits)
        splitNames = this.splits.map { it.name }.toTypedArray()
        splitDirs = this.splits.map { it.absolutePath }.toTypedArray()
    }
}
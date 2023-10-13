package com.donalddu.splitinstaller

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import me.weishu.reflection.Reflection
import java.io.File
import java.lang.reflect.Proxy

@SuppressLint("PrivateApi")
object SplitInstaller {
    internal const val TAG = "SplitLoader"
    private var unseal = false

    /**
     * @param splits apk files to load append
     * @param splitOptDir null dir means disable DEX opt
     * */
    @JvmStatic
    fun load(context: Context, splits: Set<File>, splitOptDir: File? = null) {
        initRestrictionBypass(context)
        if (hookGetApplicationInfo.splits.containsAll(splits)) return

        hookGetApplicationInfo.addSplits(splits)
        if (splitOptDir != null) SplitTempClassLoader.install(context.packageName, splitOptDir)
        ReflectHelper.dispatchPackageBroadcast(context.packageName)
    }

    private fun initRestrictionBypass(context: Context) {
        if (unseal) return
        unseal = true
        if (Build.VERSION.SDK_INT >= 28) {
            try {
                Reflection.unseal(context)
            } catch (e: Exception) {
                Log.e("BypassProvider", "Unable to unseal hidden api access", e)
            }
        }
    }

    private val hookGetApplicationInfo: HookGetApplicationInfo by lazy {
        val sPackageManager = ReflectHelper.sPackageManager
        val hook = HookGetApplicationInfo(sPackageManager.value!!)
        val packageManagerClazz = Class.forName("android.content.pm.IPackageManager")
        val delegate = Proxy.newProxyInstance(javaClass.classLoader, arrayOf(packageManagerClazz), hook)
        sPackageManager.value = delegate
        hook
    }
}
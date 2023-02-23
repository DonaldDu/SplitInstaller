package com.donalddu.splitinstaller

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import com.dhy.easyreflect.field
import com.dhy.easyreflect.method
import org.chickenhook.restrictionbypass.Unseal
import java.io.File
import java.lang.reflect.Proxy

@SuppressLint("PrivateApi")
object SplitInstaller {
    internal const val TAG = "SplitLoader"

    init {
        initRestrictionBypass()
    }

    /**
     * @param splits apk files to load append
     * */
    @JvmStatic
    fun load(context: Context, splitOptDir: File, splits: Set<File>) {
        if (hookGetApplicationInfo.splits.containsAll(splits)) return

        hookGetApplicationInfo.addSplits(splits)
        SplitTempClassLoader.install(context, splitOptDir)
        dispatchPackageBroadcast(context)
    }

    private fun initRestrictionBypass() {
        if (Build.VERSION.SDK_INT >= 28) {
            try {
                Unseal.unseal()
            } catch (e: Exception) {
                Log.e("BypassProvider", "Unable to unseal hidden api access", e)
            }
        }
    }

    private fun dispatchPackageBroadcast(context: Context) {
        val mAppThread = Class.forName("android.app.ActivityThread")
            .field("mAppThread")
            .get(currentActivityThread)

        mAppThread.javaClass
            .method("dispatchPackageBroadcast", Int::class, Array<String>::class)
            .invoke(mAppThread, 3, arrayOf(context.packageName))
    }

    private val hookGetApplicationInfo: HookGetApplicationInfo by lazy {
        val sPackageManagerField = Class.forName("android.app.ActivityThread").field("sPackageManager")
        val origin = sPackageManagerField.get(currentActivityThread)!!
        val hook = HookGetApplicationInfo(origin)
        val packageManagerClazz = Class.forName("android.content.pm.IPackageManager")
        val delegate = Proxy.newProxyInstance(SplitInstaller::class.java.classLoader, arrayOf(packageManagerClazz), hook)
        sPackageManagerField.set(currentActivityThread, delegate)
        hook
    }

    private val currentActivityThread by lazy {
        Class.forName("android.app.ActivityThread")
            .method("currentActivityThread")
            .invoke(null)
    }
}
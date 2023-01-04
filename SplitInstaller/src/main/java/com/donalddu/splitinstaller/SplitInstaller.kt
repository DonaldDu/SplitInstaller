package com.donalddu.splitinstaller

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import org.chickenhook.restrictionbypass.Unseal
import java.io.File
import java.lang.reflect.Proxy

@SuppressLint("PrivateApi")
object SplitInstaller {
    internal const val TAG = "SplitLoader"

    /**
     * @param splits apk files to load append
     * */
    @JvmStatic
    fun load(context: Context, splitOptDir: File, splits: Set<File>) {
        initRestrictionBypass()
        if (hookGetApplicationInfo == null) {
            SplitInstalledDispatcher.addListener(DynamicProviderSwitchListener(context))
            hookGetApplicationInfo(context)
        }
        hookGetApplicationInfo!!.addSplits(splits)
        SplitTempClassLoader.install(context, splitOptDir)
        dispatchPackageBroadcast(context)
    }

    private var restrictionBypass = false
    private fun initRestrictionBypass() {
        if (restrictionBypass) return
        if (Build.VERSION.SDK_INT >= 28) {
            try {
                Unseal.unseal()
            } catch (e: Exception) {
                Log.e("BypassProvider", "Unable to unseal hidden api access", e)
            }
        }
        restrictionBypass = true
    }

    private fun dispatchPackageBroadcast(context: Context) {
        val mAppThread = Class.forName("android.app.ActivityThread")
            .field("mAppThread")
            .get(currentActivityThread)

        Class.forName("android.app.ActivityThread\$ApplicationThread")
            .method("dispatchPackageBroadcast", Int::class, Array<String>::class)
            .invoke(mAppThread, 3, arrayOf(context.packageName))
    }

    private var hookGetApplicationInfo: HookGetApplicationInfo? = null
    private fun hookGetApplicationInfo(context: Context) {
        val sPackageManagerField = Class.forName("android.app.ActivityThread")
            .field("sPackageManager")

        val origin = sPackageManagerField.get(currentActivityThread)!!
        hookGetApplicationInfo = HookGetApplicationInfo(origin)
        val packageManagerClazz = Class.forName("android.content.pm.IPackageManager")
        val delegate = Proxy.newProxyInstance(context.classLoader, arrayOf(packageManagerClazz), hookGetApplicationInfo!!)
        sPackageManagerField.set(currentActivityThread, delegate)
    }

    private val currentActivityThread by lazy {
        Class.forName("android.app.ActivityThread")
            .method("currentActivityThread")
            .invoke(null)
    }
}
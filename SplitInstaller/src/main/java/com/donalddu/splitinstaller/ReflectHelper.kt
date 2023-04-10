package com.donalddu.splitinstaller

import android.annotation.SuppressLint
import android.util.ArrayMap
import com.dhy.easyreflect.method
import java.lang.ref.WeakReference
import java.lang.reflect.Method

@SuppressLint("PrivateApi")
object ReflectHelper {
    private val currentActivityThread by lazy {
        Class.forName("android.app.ActivityThread")
            .method("currentActivityThread")
            .invoke(null)
    }
    private val mAppThread: Any get() = currentActivityThread.getFieldValue("mAppThread")!!
    internal val sPackageManager: FieldDelegate<Any> get() = FieldDelegate(currentActivityThread, "sPackageManager")
    private val mPackages: ArrayMap<*, *> get() = currentActivityThread.getFieldValue("mPackages") as ArrayMap<*, *>

    /**
     * black list api
     * */
    private val dispatchPackageBroadcastMethod: Method
        get():Method {
            return mAppThread.javaClass.method("dispatchPackageBroadcast", Int::class, Array<String>::class)
        }

    fun dispatchPackageBroadcast(packageName: String) {
        dispatchPackageBroadcastMethod.invoke(mAppThread, 3, arrayOf(packageName))
    }

    fun getLoadedApk(packageName: String): Any {
        val loadedApkRef = mPackages[packageName] as WeakReference<*>
        return loadedApkRef.get()!!
    }
}
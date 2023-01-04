package com.donalddu.splitinstaller

import android.annotation.SuppressLint
import android.content.Context
import android.util.ArrayMap
import android.util.Log
import com.donalddu.splitinstaller.SplitInstaller.TAG
import dalvik.system.BaseDexClassLoader
import dalvik.system.PathClassLoader
import java.io.File
import java.lang.ref.WeakReference

@SuppressLint("PrivateApi")
internal class SplitTempClassLoader(
    parent: ClassLoader,
    private val loadedApk: Any,
    private val optimizedDirectory: File
) : PathClassLoader("", parent) {
    @Suppress("PrivatePropertyName")

    private val pathListF = BaseDexClassLoader::class.field("pathList")
    private val pathList by lazy { pathListF.get(parent) }
    private val addDexPathDelegate by lazy {
        pathList.javaClass.method("addDexPath", String::class, File::class)
    }
    private val addNativePathDelegate by lazy {
        pathList.javaClass.method("addNativePath", Collection::class)
    }

    fun addDexPath(dexPath: String) {
        Log.i(TAG, "addDexPath $dexPath")
        addDexPathDelegate.invoke(pathList, dexPath, optimizedDirectory)
        loadedApkClassLoaderField.set(loadedApk, parent)
        Log.i(TAG, "restore to default loadedApkClassLoader")
        SplitInstalledDispatcher.onSplitInstalled()
    }

    fun addNativePath(libPaths: Collection<String>) {
        Log.i(TAG, "addNativePath ${libPaths.joinToString()}")
        addNativePathDelegate.invoke(pathList, libPaths)
    }

    companion object {
        /**
         * replace LoadedApk ClassLoader to hook addDexPath with optimizedDirectory
         * */
        fun install(context: Context, splitOptDir: File) {
            val loadedApk = getLoadedApk(context)
            val loadedApkClassLoader = loadedApkClassLoaderField.get(loadedApk) as ClassLoader
            if (loadedApkClassLoader !is SplitTempClassLoader) {
                val temp = SplitTempClassLoader(loadedApkClassLoader, loadedApk, splitOptDir)
                loadedApkClassLoaderField.set(loadedApk, temp)
            }
        }

        @SuppressLint("PrivateApi")
        private fun getLoadedApk(context: Context): Any {
            val mPackages = currentActivityThread.javaClass.field("mPackages").get(currentActivityThread) as ArrayMap<*, *>
            val loadedApkRef = mPackages[context.packageName] as WeakReference<*>
            return loadedApkRef.get()!!
        }

        private val currentActivityThread by lazy {
            Class.forName("android.app.ActivityThread")
                .method("currentActivityThread")
                .invoke(null)!!
        }

        private val loadedApkClassLoaderField by lazy {
            val loadedApkClazz = Class.forName("android.app.LoadedApk")
            try {
                loadedApkClazz.field("mDefaultClassLoader")
            } catch (e: Exception) {
                loadedApkClazz.field("mClassLoader")
            }
        }
    }
}
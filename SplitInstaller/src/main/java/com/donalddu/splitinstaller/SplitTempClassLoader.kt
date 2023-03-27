package com.donalddu.splitinstaller

import android.annotation.SuppressLint
import android.util.Log
import com.dhy.easyreflect.field
import com.dhy.easyreflect.method
import com.donalddu.splitinstaller.SplitInstaller.TAG
import dalvik.system.BaseDexClassLoader
import dalvik.system.PathClassLoader
import java.io.File

@SuppressLint("PrivateApi")
internal class SplitTempClassLoader(
    parent: ClassLoader,
    private val mDefaultClassLoader: FieldDelegate<ClassLoader>,
    private val optimizedDirectory: File
) : PathClassLoader("", parent) {
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
        mDefaultClassLoader.value = parent//restore to default classLoader
        Log.i(TAG, "restore to default classLoader")
        SplitInstalledDispatcher.onSplitInstalled()
    }

    fun addNativePath(libPaths: Collection<String>) {
        Log.i(TAG, "addNativePath ${libPaths.joinToString()}")
        addNativePathDelegate.invoke(pathList, libPaths)
    }

    companion object {
        private var temp: SplitTempClassLoader? = null

        /**
         * replace LoadedApk ClassLoader to hook addDexPath with optimizedDirectory
         * */
        fun install(packageName: String, splitOptDir: File) {
            val mDefaultClassLoader = getDefaultClassLoaderDelegate(packageName)
            val loadedApkClassLoader = mDefaultClassLoader.value!!
            if (loadedApkClassLoader !is SplitTempClassLoader) {
                temp = temp ?: SplitTempClassLoader(loadedApkClassLoader, mDefaultClassLoader, splitOptDir)
                mDefaultClassLoader.value = temp
            }
        }

        private fun getDefaultClassLoaderDelegate(packageName: String): FieldDelegate<ClassLoader> {
            return temp?.mDefaultClassLoader ?: FieldDelegate(ReflectHelper.getLoadedApk(packageName), "mDefaultClassLoader", "mClassLoader")
        }
    }
}
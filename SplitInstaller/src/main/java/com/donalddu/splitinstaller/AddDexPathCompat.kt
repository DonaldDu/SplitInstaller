package com.donalddu.splitinstaller

import android.os.Build
import com.dhy.easyreflect.field
import com.dhy.easyreflect.method
import java.io.File
import java.io.IOException

object AddDexPathCompat {
    @JvmStatic
    fun addDexPath(pathList: Any, files: ArrayList<File>, optimizedDirectory: File?) {
        val dexPathListClass = Class.forName("dalvik.system.DexPathList")
        if (Build.VERSION.SDK_INT >= 24) {//android-7.0
            //android-7.0 public void addDexPath(String dexPath, File optimizedDirectory)
            dexPathListClass.method("addDexPath", ArrayList::class, File::class)
                .invoke(pathList, files, optimizedDirectory)
        } else {
            val field = dexPathListClass.field("dexElements")
            val olds = field.get(pathList) as Array<*>
            val adds = makeDexElements(files, optimizedDirectory, ArrayList())
            field.set(pathList, mergeAndDistinctArray(adds, olds))
        }
    }

    //private static Element[] makeDexElements(ArrayList<File> files, File optimizedDirectory, ArrayList<IOException> suppressedExceptions)
    private fun makeDexElements(files: ArrayList<File>, optimizedDirectory: File?, suppressedExceptions: ArrayList<IOException>): Array<*> {
        val dexPathListClass = Class.forName("dalvik.system.DexPathList")
        val m = dexPathListClass.method("makeDexElements", ArrayList::class, File::class, ArrayList::class)
        return m.invoke(null, files, optimizedDirectory, suppressedExceptions) as Array<*>
    }

    /**
     * 合并 & 去重
     * */
    private fun mergeAndDistinctArray(first: Array<*>, second: Array<*>): Array<*> {
        val firstTemp = first.toSet()
        val secondTemp = second.toMutableSet().apply { removeAll(firstTemp) }
        val list = mutableListOf<Any?>()
        list.addAll(firstTemp)
        list.addAll(secondTemp)
        val results = list.toTypedArray()
        val newArray = java.lang.reflect.Array.newInstance(results.first()!!.javaClass, results.size)
        System.arraycopy(results, 0, newArray, 0, results.size)
        return newArray as Array<*>
    }
}
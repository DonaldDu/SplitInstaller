package com.dhy.soinstaller

import android.os.Build
import com.dhy.easyreflect.field
import com.dhy.easyreflect.method
import dalvik.system.BaseDexClassLoader
import java.io.File

//http://gityuan.com/2017/03/26/load_library/
object DexPathListCompat {

    /**
     * 添加实体目录，或zip中的目录( .so 文件须未压缩，否则不生效)。内部去重，多次调用无影响。
     * @param addToHead True：添加到最前面，可实现SO热修复功能；False：默认方式添加，兼容性及性能更好。
     * */
    @JvmStatic
    fun addNativePath(pathList: Any, libPaths: Collection<String>, addToHead: Boolean = false) {
        val dexPathListClass = Class.forName("dalvik.system.DexPathList")
        if (!addToHead && Build.VERSION.SDK_INT >= 28) {//android-9.0
            //addNativePath(Collection<String> libPaths)
            dexPathListClass.method("addNativePath", Collection::class).invoke(pathList, libPaths)
        } else if (Build.VERSION.SDK_INT >= 26) {//android-8.0
            val field = dexPathListClass.field("nativeLibraryPathElements")
            val olds = field.get(pathList) as Array<*>
            val adds = makePathElements(dexPathListClass, libPaths)
            val newValue = mergeAndDistinctArray(adds, olds)
            field.set(pathList, newValue)
        } else {//android-5.0
            val field = dexPathListClass.field("nativeLibraryDirectories")
            val value = field.get(pathList)
            val libs = libPaths.map { File(it) }
            when (value) {
                is Array<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val array = value as Array<File>
                    val ne = array.toMutableList()
                    ne.removeAll(libs)
                    ne.addAll(0, libs)
                    field.set(pathList, ne.toTypedArray())
                }
                is List<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val list = value as List<File>
                    val ne = list.toMutableList()
                    ne.removeAll(libs)
                    ne.addAll(0, libs)
                    field.set(pathList, ne)
                }
                else -> throw IllegalStateException("addNativePath unknown type")
            }
        }
    }

    private fun makePathElements(dexPathList: Class<*>, libPaths: Collection<String>): Array<*> {
        //private static NativeLibraryElement[] makePathElements(List<File> files)
        val m = dexPathList.method("makePathElements", List::class)
        return m.invoke(null, libPaths.toSet().map { File(it) }) as Array<*>
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

fun BaseDexClassLoader.addNativePathCompat(libPaths: Collection<String>, addToHead: Boolean = false) {
    val pathList = BaseDexClassLoader::class.field("pathList").get(this)!!
    DexPathListCompat.addNativePath(pathList, libPaths, addToHead)
}
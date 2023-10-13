package com.dhy.soinstaller

import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

@Suppress("DEPRECATION")
object NativeSplitInstaller {
    internal val zipDirRedirect: MutableMap<String, String> = mutableMapOf()

    /**
     * 在安装运行插件前，需先把so文件解压到指定目录
     * "/data/user/xxx/split.apk!/lib/x86_64" -> File(".../split.apk.so/lib/x86_64")
     * */
    @JvmStatic
    fun redirect(splitApk: File, unzipSoFolder: File) {
        val apkSoDir = File(unzipSoFolder, "${splitApk.name}.so")
        val versionFile = File(apkSoDir, "${Build.CPU_ABI}.version")
        val lastModified = if (versionFile.exists()) versionFile.readText().toLong() else null
        val soRoot = File(apkSoDir, Build.CPU_ABI)
        zipDirRedirect[splitApk.absolutePath] = soRoot.absolutePath
        if (lastModified == splitApk.lastModified()) return

        soRoot.deleteRecursively()//删除所有旧版本so文件
        versionFile.parentFile?.mkdirs()
        versionFile.writeText(splitApk.lastModified().toString())

        val lib = "lib/${Build.CPU_ABI}/"//需加后缀 ‘/’， 以区分 "lib/x86/", "lib/x86_64/"
        val zip = ZipFile(splitApk)
        for (e in zip.entries()) {
            if (e.name.startsWith(lib) && e.name.endsWith(".so")) {
                soRoot.mkdirs()//没有so文件时，不会创建文件夹，最后也不会加到so搜索列表，可优化查询速度
                zip.getInputStream(e).also {
                    val outputStream = FileOutputStream(File(soRoot, e.name.substringAfterLast('/')))
                    it.copyTo(outputStream)
                    outputStream.flush()
                    outputStream.close()
                }
            }
        }
        zip.close()
    }

    private fun getSoDir(splitApk: File): String? {
        val dir = zipDirRedirect[splitApk.absolutePath]
        if (dir != null && File(dir).exists()) return dir
        return null
    }

    @JvmStatic
    fun addNativePath(pathList: Any, splitApk: File) {
        val path = getSoDir(splitApk) ?: return
        DexPathListCompat.addNativePath(pathList, listOf(path))
    }

    fun addNativePath(pathList: Any, libPaths: Collection<String>) {
        val redirects = mutableListOf<String>()
        for (path in libPaths) {
            if (zipDirRedirect.containsKey(path)) {
                redirects.add(zipDirRedirect.getValue(path))
            } else {
                redirects.add(path)
            }
        }
        DexPathListCompat.addNativePath(pathList, redirects, true)
        Log.i("TAG", "addNativePath ${redirects.joinToString()}")
    }
}
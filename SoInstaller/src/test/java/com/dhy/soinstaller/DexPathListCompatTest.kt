package com.dhy.soinstaller

import dalvik.system.PathClassLoader
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.zip.ZipFile

//@Config(minSdk = 21, maxSdk = 27, manifest = Config.NONE)
@Config(minSdk = 21, manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
open class DexPathListCompatTest {
    private val soApk = File(System.getProperty("user.dir"), "src/androidTest/assets/so.apk")

    @Test
    fun addNativePath() {
        Assert.assertTrue(soApk.exists())
        val tmp = File(soApk.parent, "tmp")
        val folder = "lib/x86_64"
        if (!tmp.exists()) soApk.unzip(folder, tmp)
        val loader = PathClassLoader("", javaClass.classLoader)
        loader.addNativePathCompat(setOf("$tmp/$folder"))
        val name = loader.findLibrary("imagepipeline")
        tmp.deleteRecursively()
        Assert.assertTrue(name != null)
        System.gc()
    }

    @Test
    fun addNativePathToHead() {

        Assert.assertTrue(soApk.exists())
        val tmp = File(soApk.parent, "tmp")
        val folder = "lib/x86_64"
        if (!tmp.exists()) soApk.unzip(folder, tmp)
        val loader = PathClassLoader("", javaClass.classLoader)
        loader.addNativePathCompat(setOf("$tmp/$folder"), true)
        val name = loader.findLibrary("imagepipeline")
        tmp.deleteRecursively()
        Assert.assertTrue(name != null)
        System.gc()
    }

    private fun File.unzip(folder: String, toDir: File) {
        val zipFile = ZipFile(this)
        val head = if (folder.endsWith("/")) folder else "$folder/"
        for (e in zipFile.entries()) {
            if (e.name.startsWith(head) && e.name.endsWith(".so")) {
                zipFile.getInputStream(e).also {
                    val name = if (isWindowsSystem) {
                        e.name.replace("/lib", "/")
                            .replace(".so", ".dll")
                    } else e.name
                    val file = File(toDir, name)
                    file.parentFile?.mkdirs()
                    file.writeBytes(it.readBytes())
                }
            }
        }
        zipFile.close()
    }

    private val isWindowsSystem: Boolean = System.getProperty("os.name")!!.lowercase().contains("win")
}
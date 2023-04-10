package com.dhy.soinstaller

import android.content.Context
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dhy.easyreflect.field
import dalvik.system.BaseDexClassLoader
import dalvik.system.PathClassLoader
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.zip.ZipFile

@RunWith(AndroidJUnit4::class)
class DexPathListCompatTest {
    private val context: Context by lazy { InstrumentationRegistry.getInstrumentation().targetContext }
    private val soApk: File by lazy {
        val f = File(context.cacheDir, "so.apk")
        f.delete()
        context.assets.open(f.name).also {
            f.writeBytes(it.readBytes())
        }
        f
    }

    @Test
    fun addNativePathByPathList() {
        Assert.assertTrue(soApk.exists())
        val tmp = File(context.cacheDir, "unzipSo")
        val cpu = Build.CPU_ABI
        val folder = "lib/$cpu"
        soApk.unzip(folder, tmp)
        val loader = context.classLoader as PathClassLoader
        loader.addNativePathCompat(setOf("$tmp/$folder"))
        val name = loader.findLibrary("imagepipeline")
        tmp.deleteRecursively()
        Assert.assertTrue(name?.endsWith("/libimagepipeline.so") == true)
    }

    @Test
    fun nativeSplitInstallerListenerTest() {
        Assert.assertTrue(soApk.exists())
        val tmp = File(context.cacheDir, "unzipSo")
        NativeSplitInstaller.redirect(soApk, tmp)

        val loader = context.classLoader as PathClassLoader
        val pathList = BaseDexClassLoader::class.field("pathList").get(loader)!!
        NativeSplitInstaller.addNativePath(pathList, NativeSplitInstaller.zipDirRedirect.keys)

        val name = loader.findLibrary("imagepipeline")
        tmp.deleteRecursively()
        Assert.assertTrue(name?.endsWith("/libimagepipeline.so") == true)
    }

    private fun File.unzip(folder: String, toDir: File) {
        val zipFile = ZipFile(this)
        val head = if (folder.endsWith("/")) folder else "$folder/"
        for (e in zipFile.entries()) {
            if (e.name.startsWith(head) && e.name.endsWith(".so")) {
                zipFile.getInputStream(e).also {
                    val file = File(toDir, e.name)
                    file.parentFile?.mkdirs()
                    file.writeBytes(it.readBytes())
                }
            }
        }
        zipFile.close()
    }
}
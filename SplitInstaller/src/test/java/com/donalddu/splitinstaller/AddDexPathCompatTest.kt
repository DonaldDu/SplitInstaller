package com.donalddu.splitinstaller

import com.dhy.easyreflect.field
import dalvik.system.BaseDexClassLoader
import dalvik.system.PathClassLoader
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

//@Config(minSdk = 21, maxSdk = 27, manifest = Config.NONE)
@Config(minSdk = 21,maxSdk = 22, manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class AddDexPathCompatTest {
    @Test
    fun addDexPath() {
        val projectRoot = File(System.getProperty("user.dir")!!).parent
        val apk = File(projectRoot, "app/src/main/assets/plugin-debug.apk")
        Assert.assertTrue(apk.exists())

        val loader = PathClassLoader("", javaClass.classLoader)
        val pathList = BaseDexClassLoader::class.field("pathList").get(loader)!!
        AddDexPathCompat.addDexPath(pathList, arrayListOf(apk), null)
        loader.loadClass("com.facebook.drawee.BuildConfig")
//        loader.loadClass("com.facebook.drawee.view.SimpleDraweeView")
//        loader.loadClass("com.dhy.plugin.PluginActivity")
    }
}
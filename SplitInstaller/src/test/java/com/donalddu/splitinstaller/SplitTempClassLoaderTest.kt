package com.donalddu.splitinstaller

import com.dhy.easyreflect.field
import com.dhy.easyreflect.method
import dalvik.system.BaseDexClassLoader
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@Config(minSdk = 28, manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class SplitTempClassLoaderTest {

    /**
     * Android7.0(24)
     * sdk api level [24,33+]
     * */
    @Test
    fun addDexPath() {
        BaseDexClassLoader::class.method("addDexPath", String::class)
    }

    /**
     * Android9.0(28)
     * sdk api level [28(9.0),33+]
     * */
    @Test
    fun addNativePath() {
        BaseDexClassLoader::class.method("addNativePath", Collection::class)
    }

    /**
     * Android7.0(24)
     * sdk api level [24,33+]
     * */
    @Test
    fun pathListAddDexPath() {
        val pathListF = BaseDexClassLoader::class.field("pathList")
        pathListF.type.method("addDexPath", String::class, File::class)
    }

    /**
     * Android9.0(28)
     * sdk api level [28(9.0),33+]
     * */
    @Test
    fun pathListAddNativePath() {
        val pathListF = BaseDexClassLoader::class.field("pathList")
        pathListF.type.method("addNativePath", Collection::class)
    }
}
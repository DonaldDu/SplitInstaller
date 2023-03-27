package com.donalddu.splitinstaller

import android.app.Application
import com.dhy.easyreflect.field
import com.dhy.easyreflect.method
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * 分成两块测试，不然内存溢出。 [21,28], [29,++]
 * */
@Config(minSdk = 21, maxSdk = 28, manifest = Config.NONE, application = Application::class)
@RunWith(RobolectricTestRunner::class)
open class SplitInstallerTest {

    @Test
    fun dispatchPackageBroadcast() {
        Class.forName("android.app.ActivityThread")
            .field("mAppThread")

        Class.forName("android.app.ActivityThread\$ApplicationThread")
            .method("dispatchPackageBroadcast", Int::class, Array<String>::class)
    }

    @Test
    fun hookGetApplicationInfo() {
        val sPackageManagerField = Class.forName("android.app.ActivityThread")
            .field("sPackageManager")
        Assert.assertEquals("android.content.pm.IPackageManager", sPackageManagerField.type.name)
    }

    @Test
    fun currentActivityThread() {
        Class.forName("android.app.ActivityThread")
            .method("currentActivityThread")
    }

    @After
    fun tearDown() {
        System.gc()
    }
}
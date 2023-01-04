package com.donalddu.splitinstaller

import android.app.Application
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(minSdk = 21, manifest = Config.NONE, application = Application::class)
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
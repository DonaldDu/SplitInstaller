package com.donalddu.splitinstaller

import android.app.Application
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(minSdk = 29, manifest = Config.NONE, application = Application::class)
@RunWith(RobolectricTestRunner::class)
class SplitInstallerTest2 : SplitInstallerTest()
package com.dhy.splitinstaller

import android.app.Application
import android.content.Context
import com.donalddu.splitinstaller.NativeSplitInstallerListener
import com.donalddu.splitinstaller.SplitInstallerDispatcher

class App : Application() {
    private val dynamicProviderSwitch by lazy { DynamicProviderSwitchListener(this) }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitInstallerDispatcher.addListener(dynamicProviderSwitch)
        SplitInstallerDispatcher.addListener(NativeSplitInstallerListener())
    }
}
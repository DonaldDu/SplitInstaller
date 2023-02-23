package com.dhy.splitinstaller

import android.app.Application
import android.content.Context
import com.donalddu.splitinstaller.SplitInstalledDispatcher

class App : Application() {
    private val dynamicProviderSwitch by lazy { DynamicProviderSwitchListener(this) }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitInstalledDispatcher.addListener(dynamicProviderSwitch)
    }
}
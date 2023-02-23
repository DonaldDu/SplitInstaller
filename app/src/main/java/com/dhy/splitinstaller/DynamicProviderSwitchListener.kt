package com.dhy.splitinstaller

import android.content.Context
import com.donald.dps.lib.DynamicProviderSwitch
import com.donalddu.splitinstaller.SplitInstalledListener

class DynamicProviderSwitchListener(private val context: Context) : SplitInstalledListener {
    private val providerSwitch by lazy {
        DynamicProviderSwitch(context, false, compatProvider = false)
    }

    override fun onSplitInstalled() {
        providerSwitch.startDynamicProviders()
    }
}
package com.dhy.splitinstaller

import android.content.Context
import com.donald.dps.lib.DynamicProviderSwitch
import com.donalddu.splitinstaller.SplitInstallerListener

class DynamicProviderSwitchListener(private val context: Context) : SplitInstallerListener {
    private val providerSwitch by lazy {
        DynamicProviderSwitch(context, false, compatProvider = false)
    }

    override fun onSplitInstalled() {
        providerSwitch.startDynamicProviders()
    }
}
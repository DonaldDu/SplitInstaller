package com.donalddu.splitinstaller

import android.content.Context
import com.donald.dps.lib.DynamicProviderSwitch

object SplitInstalledDispatcher {
    private val listeners: MutableSet<SplitInstalledListener> = mutableSetOf()
    internal fun onSplitInstalled() {
        //safe forEach: add or remove will not cause errors, not work either
        ArrayList(listeners).forEach {
            it.onSplitInstalled()
        }
    }

    fun addListener(listener: SplitInstalledListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: SplitInstalledListener) {
        listeners.remove(listener)
    }
}

interface SplitInstalledListener {
    fun onSplitInstalled()
}

class DynamicProviderSwitchListener(private val context: Context) : SplitInstalledListener {
    private val providerSwitch by lazy {
        DynamicProviderSwitch(context, false, compatProvider = false)
    }

    override fun onSplitInstalled() {
        providerSwitch.startDynamicProviders()
    }
}
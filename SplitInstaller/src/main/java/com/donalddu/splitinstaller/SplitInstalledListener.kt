package com.donalddu.splitinstaller

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


package com.donalddu.splitinstaller

object SplitInstallerDispatcher {
    private val listeners: MutableSet<SplitInstallerListener> = mutableSetOf()
    internal fun onSplitInstalled() {
        //safe forEach: add or remove will not cause errors, not work either
        ArrayList(listeners).forEach {
            it.onSplitInstalled()
        }
    }

    internal fun onAddNativePath(pathList: Any, libPaths: Collection<String>) {
        ArrayList(listeners).forEach {
            it.onAddNativePath(pathList, libPaths)
        }
    }

    fun addListener(listener: SplitInstallerListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: SplitInstallerListener) {
        listeners.remove(listener)
    }
}

interface SplitInstallerListener {
    fun onAddNativePath(pathList: Any, libPaths: Collection<String>){}
    fun onSplitInstalled(){}
}


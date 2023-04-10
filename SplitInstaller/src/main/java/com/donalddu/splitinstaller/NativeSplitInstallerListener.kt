package com.donalddu.splitinstaller

import com.dhy.soinstaller.NativeSplitInstaller
import java.io.File

class NativeSplitInstallerListener : SplitInstallerListener {
    companion object {

        /**
         * 在安装运行插件前，需求先把so文件解压到指定目录
         * "/data/user/xxx/so.apk!/lib/x86_64" -> File("/data/user/xxx/cache/so.apk/lib/x86_64")
         * */
        @JvmStatic
        fun redirect(splitApk: File, unzipSoFolder: File) {
            NativeSplitInstaller.redirect(splitApk, unzipSoFolder)
        }
    }

    override fun onAddNativePath(pathList: Any, libPaths: Collection<String>) {
        NativeSplitInstaller.addNativePath(pathList, libPaths)
    }
}
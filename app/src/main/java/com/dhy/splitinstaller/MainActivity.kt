package com.dhy.splitinstaller

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dhy.splitinstaller.databinding.ActivityMainBinding
import com.donalddu.splitinstaller.NativeSplitInstallerListener
import com.donalddu.splitinstaller.SplitInstaller
import com.donalddu.splitinstaller.SplitInstallerDispatcher
import com.donalddu.splitinstaller.SplitInstallerListener
import dalvik.system.PathClassLoader
import java.io.File

class MainActivity : AppCompatActivity(), SplitInstallerListener {
    private val TAG = "Main"
    private val context get() = this
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        SplitInstallerDispatcher.addListener(this)
        binding.apply {
            btLoadPluginAssets.setOnClickListener {
                findLibrary()
                val msg = try {
                    assets.open("a.txt").use { txt -> txt.readBytes().decodeToString() }
                } catch (e: Exception) {
                    e.toString()
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                Log.i(TAG, "asset $msg")
                DeviceStatus.it.assetOK = msg == "123"
                autoTest()
            }

            btInstallPlugin.setOnClickListener {
                NativeSplitInstallerListener.redirect(pluginApk, File(filesDir, "SplitSO"))
                SplitInstaller.load(context, setOf(pluginApk), cacheDir)
                btShowPluginPage.isEnabled = true
            }

            btClearApplicationUserData.setOnClickListener {
                clearApplicationUserData()
            }
            btShowPluginPage.isEnabled = false
            btShowPluginPage.setOnClickListener {
                try {
                    startComponent("com.dhy.plugin.PluginActivity")
                    DeviceStatus.it.pluginPage = true
                } catch (e: Exception) {
                    DeviceStatus.it.pluginPage = false
                    e.printStackTrace()
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                }
                autoTest()
            }
        }
        binding.btInstallPlugin.post {
            autoTest()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SplitInstallerDispatcher.removeListener(this)
    }

    private fun findLibrary() {
        val loader = classLoader as PathClassLoader
        val name = loader.findLibrary("imagepipeline")
        Log.i(TAG, "findLibrary: $name")
        Toast.makeText(context, "findLibrary: $name", Toast.LENGTH_SHORT).show()
        DeviceStatus.it.soOK = name != null
    }

    override fun onSplitInstalled() {
        binding.btInstallPlugin.postDelayed({
            autoTest()
        }, 3000)
    }

    private fun autoTest() {
        val autoTest = getString(R.string.autoTest) == "1"
        val hasToken = getString(R.string.X_LC_ID).isNotEmpty()
        if (autoTest && hasToken) {
            if (DeviceStatus.it.allReady()) {
                Reporter.upload(this)
            } else {
                if (!binding.btShowPluginPage.isEnabled) {
                    binding.btInstallPlugin.performClick()
                } else if (DeviceStatus.it.assetOK == null) {
                    binding.btLoadPluginAssets.performClick()
                } else if (DeviceStatus.it.pluginPage == null) {
                    binding.btShowPluginPage.performClick()
                }
            }
        }
    }

    private val pluginApk by lazy {
        val name = "plugin-debug.apk"
        val file = File(cacheDir, name)
        if (!file.exists()) {
            assets.open(name).use {
                file.writeBytes(it.readBytes())
            }
        }
        file
    }
}
package com.dhy.splitinstaller

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dhy.splitinstaller.databinding.ActivityMainBinding
import com.donalddu.splitinstaller.NativeSplitInstallerListener
import com.donalddu.splitinstaller.SplitInstaller
import dalvik.system.PathClassLoader
import java.io.File

class MainActivity : AppCompatActivity() {
    private val context get() = this
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            btLoadPluginAssets.setOnClickListener {
                findLibrary()
                val msg = try {
                    assets.open("a.txt").use { txt -> txt.readBytes().decodeToString() }
                } catch (e: Exception) {
                    e.toString()
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }

            btInstallPlugin.setOnClickListener {
                NativeSplitInstallerListener.redirect(pluginApk, File(filesDir, "SplitSO"))
                SplitInstaller.load(context, setOf(pluginApk), cacheDir)
            }

            btClearApplicationUserData.setOnClickListener {
                clearApplicationUserData()
            }

            btShowPluginPage.setOnClickListener {
                try {
                    startComponent("com.dhy.plugin.PluginActivity")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun findLibrary() {
        val loader = classLoader as PathClassLoader
        val name = loader.findLibrary("imagepipeline")
        println("findLibrary: $name")
        Toast.makeText(context, "findLibrary: $name", Toast.LENGTH_SHORT).show()
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
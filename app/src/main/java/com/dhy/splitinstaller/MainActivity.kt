package com.dhy.splitinstaller

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dhy.splitinstaller.databinding.ActivityMainBinding
import com.donalddu.splitinstaller.SplitInstaller
import java.io.File

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            btLoadPluginAssets.setOnClickListener {
                val msg = try {
                    assets.open("a.txt").use { txt -> txt.readBytes().decodeToString() }
                } catch (e: Exception) {
                    e.toString()
                }
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            }

            btInstallPlugin.setOnClickListener {
                SplitInstaller.load(this@MainActivity, cacheDir, setOf(pluginApk))
            }

            btClearApplicationUserData.setOnClickListener {
                clearApplicationUserData()
            }

            btShowPluginPage.setOnClickListener {
                try {
                    startComponent("com.dhy.plugin.PluginActivity")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_SHORT).show()
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
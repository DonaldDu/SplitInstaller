package com.dhy.plugin

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView

class PluginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_plugin)
        findViewById<View>(R.id.loadImage).setOnClickListener {
            val image: SimpleDraweeView = findViewById(R.id.image)
            image.setImageURI("https://t7.baidu.com/it/u=1819248061,230866778&fm=193&f=GIF")
        }
    }
}
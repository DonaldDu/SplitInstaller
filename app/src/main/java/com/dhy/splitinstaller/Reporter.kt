package com.dhy.splitinstaller

import android.annotation.SuppressLint
import android.content.Context
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


object Reporter {
    @SuppressLint("CheckResult")
    fun upload(context: Context) {
        val pref = context.getSharedPreferences("test", Context.MODE_PRIVATE)
        val keyReported = "reported"
        val reported = pref.getBoolean(keyReported, false)
        if (reported) return

        val id = context.getString(R.string.X_LC_ID)
        val key = context.getString(R.string.X_LC_KEY)
        api.insertLeanCloud(id, key, DeviceStatus.it)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                if (it.error != null) println("${it.code}: ${it.error}")
                else {
                    pref.edit().putBoolean(keyReported, true).apply()
                    println("insertOK")
                }
            }, {
                println("insertLeanCloud error")
                it.printStackTrace()
            })
    }

    private interface Api {
        @POST("https://api.leancloud.cn/1.1/classes/SplitInstallerTest")
        fun insertLeanCloud(
            @Header("X-LC-Id") lcId: String, @Header("X-LC-Key") lcKey: String, @Body data: DeviceStatus
        ): Observable<DeviceStatusResponse>
    }

    private val httpClient by lazy { OkHttpClient.Builder().build() }
    private val retrofit: Retrofit
        get() {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl("https://api.leancloud.cn")
                .client(httpClient)
                .build()
        }

    private val api by lazy { retrofit.create(Api::class.java) }
}
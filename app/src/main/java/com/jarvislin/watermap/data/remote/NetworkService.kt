package com.jarvislin.watermap.data.remote

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import com.jarvislin.watermap.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkService {
    private val retrofit: Retrofit

    companion object {
        //        const val API_DEV_URL = "https://master.apis.dev.openstreetmap.org/api/" 會抓不到資料，暫時用正式 API
        const val API_DEV_URL = "https://api.openstreetmap.org/api/"
        const val API_PRO_URL = "https://api.openstreetmap.org/api/"
        const val API_VERSION = "0.6/"
        const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss zzz"
    }

    init {
        retrofit = createRetrofit()
    }

    private fun createRetrofit(url: String = this.baseUrl()): Retrofit {
        val gson = GsonBuilder().setDateFormat(DATE_FORMAT).create()
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(url)
                .client(createClient())
                .build()
    }

    private fun createClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(if (isDebug()) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE))

        if (isDebug()) {
            builder.addNetworkInterceptor(StethoInterceptor())
        }

        return builder.build()
    }

    private fun isDebug(): Boolean = BuildConfig.DEBUG

    private fun baseUrl() = if (BuildConfig.DEBUG) "$API_DEV_URL$API_VERSION" else "$API_PRO_URL$API_VERSION"


    fun <T> create(clazz: Class<T>): T = retrofit.create(clazz)
}
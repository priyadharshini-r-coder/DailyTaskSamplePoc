package com.mvp.omnicure.kotlinactivity.retrofit

import android.content.Context
import android.util.Log
import com.example.dailytasksamplepoc.kotlinomnicure.OmnicureApp
import com.example.dailytasksamplepoc.kotlinomnicure.interfaces.ProviderEndpoints
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.gson.GsonBuilder
import com.mvp.omnicure.kotlinactivity.interceptor.DecryptionInterceptor
import com.mvp.omnicure.kotlinactivity.interceptor.EncryptionInterceptor

import com.example.dailytasksamplepoc.kotlinomnicure.interfaces.ApiEndpoints
import com.mvp.omnicure.kotlinactivity.interfaces.HospitalEndpoints
import com.mvp.omnicure.kotlinactivity.interfaces.PatientEndpoints

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
    private val BASE_URL = BuildConfigConstants.getBackendRootUrl()
    private val TAG = "ApiClient"
    private val AUTHORIZATION = "Authorization"
    private val UID = "uid"
    private var retrofit: Retrofit? = null
    private var loggingInterceptor: HttpLoggingInterceptor? = null
    var context: Context? = null


    private fun ApiClient() {}

    fun getApi(encrypt: Boolean, decrypt: Boolean): ApiEndpoints? {
        getLoggingInterceptor()
        val builder: OkHttpClient.Builder = getOkHttpBuilder()
        loggingInterceptor?.let { builder.addNetworkInterceptor(it) }
        if (encrypt) {
            builder.addInterceptor(EncryptionInterceptor(OmnicureApp.getAppContext()))
        }
        if (decrypt) {
            builder.addInterceptor(DecryptionInterceptor(OmnicureApp.getAppContext()))
        }
        addInterceptors(builder)
        getRetrofit(builder)
        return retrofit!!.create(ApiEndpoints::class.java)
    }

    fun getApiUserEndpoints(encrypt: Boolean, decrypt: Boolean): com.mvp.omnicure.kotlinactivity.interfaces.UserEndpoints? {
        getLoggingInterceptor()
        val builder: OkHttpClient.Builder = getOkHttpBuilder()

        loggingInterceptor?.let {
            builder.addNetworkInterceptor(it).connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(12, TimeUnit.SECONDS)
                .writeTimeout(12, TimeUnit.SECONDS)
        }

        if (encrypt) {
            builder.addInterceptor(EncryptionInterceptor(OmnicureApp.getAppContext()))
        }
        if (decrypt) {
            builder.addInterceptor(DecryptionInterceptor(OmnicureApp.getAppContext()))
        }
        getRetrofit(builder)
        return retrofit!!.create(com.mvp.omnicure.kotlinactivity.interfaces.UserEndpoints::class.java)
    }


    fun getApiPatientEndpoints(encrypt: Boolean, decrypt: Boolean): com.mvp.omnicure.kotlinactivity.interfaces.PatientEndpoints? {
        getLoggingInterceptor()
        val builder: OkHttpClient.Builder = getOkHttpBuilder()
        loggingInterceptor?.let { builder.addNetworkInterceptor(it) }
        if (encrypt) {
            builder.addInterceptor(EncryptionInterceptor(OmnicureApp().getAppContext()))
        }
        if (decrypt) {
            builder.addInterceptor(DecryptionInterceptor(OmnicureApp().getAppContext()))
        }
        addInterceptors(builder)
        getRetrofit(builder)
        return retrofit!!.create(PatientEndpoints::class.java)
    }

    fun getApiProviderEndpoints(encrypt: Boolean, decrypt: Boolean): ProviderEndpoints? {
        getLoggingInterceptor()
        val builder: OkHttpClient.Builder = getOkHttpBuilder()
        loggingInterceptor?.let { builder.addNetworkInterceptor(it) }
        if (encrypt) {
            builder.addInterceptor(EncryptionInterceptor(OmnicureApp().getAppContext()))
        }
        if (decrypt) {
            builder.addInterceptor(DecryptionInterceptor(OmnicureApp().getAppContext()))
        }
        addInterceptors(builder)
        getRetrofit(builder)
        return retrofit!!.create(ProviderEndpoints::class.java)
    }

    fun getApiHospital(encrypt: Boolean, decrypt: Boolean): com.mvp.omnicure.kotlinactivity.interfaces.HospitalEndpoints? {
        getLoggingInterceptor()
        val builder: OkHttpClient.Builder = getOkHttpBuilder()
        loggingInterceptor?.let { builder.addNetworkInterceptor(it) }
        if (encrypt) {
            builder.addInterceptor(EncryptionInterceptor(OmnicureApp().getAppContext()))
        }
        if (decrypt) {
            builder.addInterceptor(DecryptionInterceptor(OmnicureApp().getAppContext()))
        }
        addInterceptors(builder)
        getRetrofit(builder)
        return retrofit!!.create(HospitalEndpoints::class.java)
    }

    private fun getOkHttpBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .callTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true)
    }

    private fun getLoggingInterceptor() {
        if (loggingInterceptor == null) {
            loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor!!.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }

    private fun addInterceptors(builder: OkHttpClient.Builder) {
        builder.addInterceptor(Interceptor { chain: Interceptor.Chain ->
            val request = chain.request()
            chain.proceed(
                request.newBuilder()
                    .addHeader(
                        AUTHORIZATION,
                        PrefUtility().getHeaderIdToken(OmnicureApp().getAppContext())
                    )
                    .addHeader(UID, PrefUtility().getFireBaseUid(OmnicureApp().getAppContext()))
                    .build()
            )
        })
    }

    private fun getRetrofit(builder: OkHttpClient.Builder) {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        retrofit = Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(builder.build())
            .build()
    }

    private fun getBaseUrl(): String? {
        val url = BASE_URL
        Log.d(TAG, "getBaseUrl: $url")
        return url
    }
}
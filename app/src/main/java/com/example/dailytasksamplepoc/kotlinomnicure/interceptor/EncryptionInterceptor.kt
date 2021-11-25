package com.mvp.omnicure.kotlinactivity.interceptor

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.dailytasksamplepoc.kotlinomnicure.utils.AESUtils
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.gson.Gson
import com.google.gson.JsonObject

import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.lang.Exception

class EncryptionInterceptor {
    private val TAG = "EncryptionInterceptor"
    private var context: Context? = null

    fun EncryptionInterceptor(context: Context?) {
        this.context = context
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(IOException::class)
    fun intercept(chain: Interceptor.Chain): Response {


        var request: Request = chain.request()

        val body = request.body

        val mediaType: MediaType = parse.parse("text/plain; charset=utf-8")
        val `object` = JsonObject()
        val buffer = Buffer()
        if (body != null) {
            Log.e(TAG, "Encryptintercept: body-->$body")
            body.writeTo(buffer)
            val requestString = buffer.readUtf8()
            Log.e(TAG, "Encryptintercept: requestString-->$requestString")
            var aesKey: String? = null
            try {
                aesKey = context?.let {
                    PrefUtility().getStringInPref(
                        it,
                        Constants.AES_API_KEY,
                        ""
                    )
                }
                Log.e(TAG, "Encryptintercept: aesKey-->$aesKey")
            } catch (e: Exception) {
                Log.e("Exception", e.toString())
            }

            val encryptedData = aesKey?.let { AESUtils().encryptData(requestString, it) }

            if (encryptedData != null) {
                Log.e("encryptedData-->", encryptedData)
            }
            `object`.addProperty("encryptedValue", encryptedData)
            Log.d(
                TAG,
                "url Endpoint req " + request.url.toString()
                    .substring(request.url.toString().lastIndexOf('/') + 1).replace("}", "")
            )
            Log.d(TAG, "intercept: reqStr $requestString")
            Log.d(TAG, "intercept: encryptedString $encryptedData")
            val decryptedData = encryptedData?.let {
                if (aesKey != null) {
                    AESUtils().decryptData(it, aesKey)
                }
            }

            Log.d(TAG, "intercept: decryptedString $decryptedData")
            Log.d(TAG, "intercept: final " + Gson().toJson(`object`))
            val requestBody: RequestBody = RequestBody.create(Gson().toJson(`object`), mediaType)
            request = request.newBuilder()
                .header(
                    "Content-Type",
                    body.contentType().toString()
                )
                .header("Content-Length", body.contentLength().toString())
                .method(request.method, requestBody)
                .build()
            Log.d(TAG, "intercept: req $request")
        }
        buffer.close()
        return chain.proceed(request)
    }
}
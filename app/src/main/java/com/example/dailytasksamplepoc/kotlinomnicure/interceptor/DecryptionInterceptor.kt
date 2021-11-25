package com.mvp.omnicure.kotlinactivity.interceptor

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.dailytasksamplepoc.kotlinomnicure.utils.AESUtils
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import omnicurekotlin.example.com.userEndpoints.model.CommonResponse
import java.io.IOException
import java.lang.Exception

class DecryptionInterceptor : Interceptor {
    private val TAG = "DecryptionInterceptor"
    private var context: Context? = null

    fun DecryptionInterceptor(context: Context?) {
        this.context = context
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        Log.e(TAG, "intercept: SentResponseTime-->" + response.sentRequestAtMillis)
        Log.e(TAG, "intercept: ReceivedResponseTime-->" + response.receivedResponseAtMillis)
        Log.e(
            TAG,
            "Final response time : " + (response.receivedResponseAtMillis - response.sentRequestAtMillis) + " ms"
        )
        if (response.isSuccessful) {
            val encrypted = response.body!!.string()
            val newResponse: Response.Builder = response.newBuilder()
            val gson = Gson()
            val commonResponse: CommonResponse =
                gson.fromJson(encrypted, CommonResponse::class.java)
            if (!TextUtils.isEmpty(commonResponse.getEncryptedValue())) {
                var aesKey: String? = null
                try {
                    aesKey = PrefUtility().getStringInPref(
                        context,
                        Constants.AES_API_KEY,
                        ""
                    )
                    Log.e(TAG, "Decryptintercept: aesKey-->$aesKey")
                } catch (e: Exception) {
                    Log.e("Exception", e.toString())
                }
                //                String decrypted = AESUtils.decryptData(commonResponse.getEncryptedValue(), "E78F5DF838969053C89DB80649AD3A200C93F0108CA2ED7C04499A956FEFD63A");
                val decrypted: String =
                    AESUtils.decryptData(commonResponse.getEncryptedValue(), aesKey)
                //                String decrypted = AESUtilsCBC.decryptData(commonResponse.getEncryptedValue(), aesKey);
                Log.d(
                    TAG,
                    "url Endpoint " + response.toString()
                        .substring(response.toString().lastIndexOf('/') + 1).replace("}", "")
                )
                Log.d(TAG, " encrypted " + commonResponse.getEncryptedValue())
                Log.d(TAG, " decrypted $decrypted")
                newResponse.body(ResponseBody.create(decrypted, parse.parse("application/json")))
                return newResponse.build()
            }
        }
        return response
    }
}
package com.example.dailytasksamplepoc.kotlinomnicure.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.healthcareEndPoints.Model.CommonResponseProviderNotification
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.healthcareEndPoints.Model.ProviderNotificationResponse
import com.example.dailytasksamplepoc.kotlinomnicure.requestbodys.CommonIdRequestBody
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.requestbodys.GetProviderListRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.SendMessageRequestBody
import com.mvp.omnicure.kotlinactivity.retrofit.ApiClient
import omnicurekotlin.example.com.hospitalEndpoints.model.HospitalListResponse
import omnicurekotlin.example.com.providerEndpoints.model.CommonResponse
import omnicurekotlin.example.com.providerEndpoints.model.ProviderListResponse
import omnicurekotlin.example.com.userEndpoints.model.VersionInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.HashMap

class HomeViewModel : ViewModel() {
   private val TAG = "HomeViewModel"

    private var providerListObservable: MutableLiveData<ProviderListResponse>? = null
    private var hospitalListObservable: MutableLiveData<HospitalListResponse>? = null
    private val providerNotificationListObservable: MutableLiveData<ProviderNotificationResponse>? =
        null
    private val updateProviderObservable: MutableLiveData<CommonResponse>? = null
    private val handOffAcceptObservable: MutableLiveData<CommonResponse>? = null
    private val addOrUpdateObservable: MutableLiveData<CommonResponseProviderNotification>? = null

    private val passwordObservable: MutableLiveData<CommonResponse>? =
        null
    private val acceptInviteObservable: MutableLiveData<CommonResponse>? =
        null
    private var startCallObservable: MutableLiveData<CommonResponse>? =
        null
    private val resetAcuityObservable: MutableLiveData<CommonResponse>? =
        null
    private val providerObservable: MutableLiveData<CommonResponse>? =
        null
    private var hospitalObservable: MutableLiveData<CommonResponse>? =
        null
    private val versionInfoObservable: MutableLiveData<VersionInfoResponse>? = null



    fun startCall(callerId: Long?, token: String?, receiverId: Long?, patientId: Long?, callType: String?): LiveData<CommonResponse?>? {
        startCallObservable = MutableLiveData<CommonResponse>()
        if (callerId != null) {
            if (token != null) {
                if (receiverId != null) {
                    if (patientId != null) {
                        if (callType != null) {
                            startCallAPI(callerId, token, receiverId, patientId, callType)
                        }
                    }
                }
            }
        }
        return startCallObservable
    }
    fun getHospitalById(hospitalId: String?): LiveData<CommonResponse?>? {
        hospitalObservable = MutableLiveData()
        if (hospitalId != null) {
            getHospitalByIdApi(hospitalId)
        }
        return hospitalObservable
    }

    private fun startCallAPI(callerId: Long, token: String, receiverId: Long, patientId: Long, callType: String) {
        val errMsg = ""
        val url = "providerEndpoints/v1/sendMessage"
        ApiClient().getApiProviderEndpoints(true, true)?.sendMessage(
            url, SendMessageRequestBody(
                callType,
                "$callerId-$receiverId",
                receiverId,
                token,
                callerId))?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
                    if (response.isSuccessful()) {
                        Log.d("VerifyTags", "onResponse: " + response.code())
                        val commonResponse: CommonResponse? =
                            response.body()
                        if (startCallObservable == null) {
                            startCallObservable =
                                MutableLiveData<CommonResponse>()
                        }
                        startCallObservable!!.setValue(commonResponse)
                    } else {
                        Log.d("Verifytags", "onResponse: " + response.code())
                        Handler(Looper.getMainLooper()).post {
                            val commonResponse: CommonResponse =
                                CommonResponse()
                            commonResponse.setErrorMessage(Constants.API_ERROR)
                            if (startCallObservable == null) {
                                startCallObservable =
                                    MutableLiveData<CommonResponse>()
                            }
                            startCallObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                    Log.e("loginTags", "onFailure: $t")
                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: CommonResponse =
                            CommonResponse()
                        commonResponse.setErrorMessage(Constants.API_ERROR)
                        if (startCallObservable == null) {
                            startCallObservable =
                                MutableLiveData<CommonResponse>()
                        }
                        startCallObservable!!.setValue(commonResponse)
                    }
                }
            })
        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse: CommonResponse =
                    CommonResponse()
                commonResponse.setErrorMessage(errMsg)
                if (startCallObservable == null) {
                    startCallObservable =
                        MutableLiveData<CommonResponse>()
                }
                startCallObservable!!.setValue(commonResponse)
            }
        }
    }
    fun getHospitalList(userid: Long): LiveData<HospitalListResponse?>? {
        hospitalListObservable = MutableLiveData()

        getHospitalApiRetro(userid)
        return hospitalListObservable
    }
    private fun getHospitalApiRetro(userid: Long) {
        val errMsg = arrayOfNulls<String>(1)

        //sending body through data class
        val requestBody = CommonIdRequestBody(userid)


        ApiClient().getApiHospital(true, true)?.hospitallistresponse(requestBody)
            ?.enqueue(object : Callback<HospitalListResponse?> {
                override fun onResponse(
                    call: Call<HospitalListResponse?>,
                    response: Response<HospitalListResponse?>
                ) {
                    Log.d(TAG, "onResponse: hospitallistresponse " + response.code())
                    if (response.isSuccessful()) {
                        if (hospitalListObservable == null) {
                            hospitalListObservable = MutableLiveData<HospitalListResponse>()
                        }
                        hospitalListObservable!!.setValue(response.body())
                    }
                }

                override fun onFailure(call: Call<HospitalListResponse?>, t: Throwable) {
                    Log.e(TAG, "onFailure: hospitallistresponse $t")
                    if (t is SocketTimeoutException) errMsg[0] =
                        Constants.APIErrorType.SocketTimeoutException.toString()
                    if (t is Exception) errMsg[0] = Constants.API_ERROR
                    val response = HospitalListResponse()
                    response.setErrorMessage(errMsg[0])
                    if (hospitalListObservable == null) {
                        hospitalListObservable = MutableLiveData<HospitalListResponse>()
                    }
                    hospitalListObservable!!.setValue(response)
                }
            })
        if (!TextUtils.isEmpty(errMsg[0])) {
            val response = HospitalListResponse()
            response.setErrorMessage(errMsg[0])
            if (hospitalListObservable == null) {
                hospitalListObservable = MutableLiveData<HospitalListResponse>()
            }
            hospitalListObservable!!.setValue(response)
        }
    }
    fun getProviderList(
        providerId: Long?,
        token: String?,
        role: String?
    ): LiveData<ProviderListResponse?>? {
        providerListObservable = MutableLiveData<ProviderListResponse>()
        if (providerId != null) {
            if (token != null) {
                if (role != null) {
                    getProviders(providerId, token, role)
                }
            }
        }
        return providerListObservable
    }

    private fun getProviders(providerId: Long, token: String, role: String) {
        val errMsg = ""

        val url = "providerEndpoints/v1/getRemoteProviderRemoteDirectory"
        ApiClient().getApiProviderEndpoints(true, true)
            ?.getProviderList(url, GetProviderListRequestBody(role, token, providerId))
            ?.enqueue(object : Callback<ProviderListResponse?> {
                override fun onResponse(
                    call: Call<ProviderListResponse?>,
                    response: Response<ProviderListResponse?>
                ) {
                    if (response.isSuccessful()) {
  ;
                        val commonResponse: ProviderListResponse? = response.body()
                        if (providerListObservable == null) {
                            providerListObservable = MutableLiveData<ProviderListResponse>()
                        }
                        providerListObservable!!.setValue(commonResponse)
                    } else {

                        Handler(Looper.getMainLooper()).post {
                            val commonResponse:ProviderListResponse =
                                ProviderListResponse()
                            commonResponse.setErrorMessage(Constants.API_ERROR)
                            if (providerListObservable == null) {
                                providerListObservable = MutableLiveData<ProviderListResponse>()
                            }
                            providerListObservable!!.setValue(commonResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ProviderListResponse?>,
                    t: Throwable
                ) {

                    Handler(Looper.getMainLooper()).post {
                        val commonResponse: ProviderListResponse =
                            ProviderListResponse()
                        commonResponse.setErrorMessage(Constants.API_ERROR)
                        if (providerListObservable == null) {
                            providerListObservable = MutableLiveData<ProviderListResponse>()
                        }
                        providerListObservable!!.setValue(commonResponse)
                    }
                }
            })


        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse: ProviderListResponse =
                    ProviderListResponse()
                commonResponse.setErrorMessage(errMsg)
                if (providerListObservable == null) {
                    providerListObservable = MutableLiveData<ProviderListResponse>()
                }
                providerListObservable!!.setValue(commonResponse)
            }
        }
    }

    private fun getHospitalByIdApi(hospitalId: String) {
        val errMsg = ""
        val bodyValues = HashMap<String, String>()
        bodyValues["hospitalId"] = hospitalId
        ApiClient().getApiHospital(true, true)?.getHospitalById(bodyValues)
            ?.enqueue(object : Callback<CommonResponse?> {
                override fun onResponse(
                    call: Call<CommonResponse?>,
                    response: Response<CommonResponse?>
                ) {
                    if (response.isSuccessful()) {

                        val commonResponse: CommonResponse? =
                            response.body()
                        if (hospitalObservable == null) {
                            hospitalObservable = MutableLiveData()
                        }
                        hospitalObservable!!.setValue(commonResponse)
                    } else {

                    }
                }

                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {

                }
            })
        if (!TextUtils.isEmpty(errMsg)) {
            Handler(Looper.getMainLooper()).post {
                val commonResponse: CommonResponse =
                    CommonResponse()
                commonResponse.setErrorMessage(errMsg)
                if (hospitalObservable == null) {
                    hospitalObservable =
                        MutableLiveData()
                }
                hospitalObservable!!.setValue(commonResponse)
            }
        }
    }
}
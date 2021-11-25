package com.example.dailytasksamplepoc.kotlinomnicure.interfaces


import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.healthcareEndPoints.Model.AddNotificationDataRequest
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.healthcareEndPoints.Model.CommonResponseProviderNotification
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.healthcareEndPoints.Model.ProviderNotificationResponse
import com.mvp.omnicure.kotlinactivity.requestbodys.LoginDetailsRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.LogoutRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.ProviderNotificationDetailsRequestBody
import com.mvp.omnicure.kotlinactivity.requestbodys.UpdateFcmkKeyRequestBody
import omnicurekotlin.example.com.appointmentEndpoints.model.Appointment
import omnicurekotlin.example.com.patientsEndpoints.model.CommonResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.HashMap

interface ApiEndpoints {

    @POST(" ")
     fun doLogin(@Body body: LoginDetailsRequestBody?): Call<CommonResponse?>?

    // loginWithPassword
    @POST("loginEndpoints/v1/loginWithPassword")
     fun loginWithPassword(@Body bodyValues: HashMap<String?, String?>?): Call<CommonResponse?>?

    @POST("healthcareEndpoints/v1/addOrUpdateProviderNotification")
     fun addOrUpdateNotificationSettings(@Body addNotificationDataRequest: AddNotificationDataRequest?): Call<CommonResponseProviderNotification?>?


    @POST("loginEndpoints/v1/updateFcmkKey")
   fun commonresponse(@Body body: UpdateFcmkKeyRequestBody?): Call<CommonResponse?>?

    @POST("appointmentEndpoints/v1/addAppointment/{token}")
    fun addAppointment(@Path("token") token: String?, @Body appointment: Appointment?): Call<CommonResponse?>?


    @POST("healthcareEndpoints/v1/getProviderNotification")
   fun getProviderNotificationDetailsApi(@Body providerId: HashMap<String?, String?>?): Call<ProviderNotificationResponse?>?

    //changed "userId" key to "id"
    @POST("healthcareEndpoints/v1/getProviderNotification")
     fun getProviderNotificationDetailsApi(@Body body: ProviderNotificationDetailsRequestBody?): Call<ProviderNotificationResponse?>?
    @POST("loginEndpoints/v1/logout")
     fun doLogout(@Body body: LogoutRequestBody?): Call<CommonResponse?>?
}
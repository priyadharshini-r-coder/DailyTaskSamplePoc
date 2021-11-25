package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.healthcareEndPoints.Model

import java.io.Serializable

//public class AddNotificationDataRequest  extends com.google.api.client.json.GenericJson {
class AddNotificationDataRequest : Serializable {

    var id: String? = null
        set(id) {
            field = id
            field = id
        }

    var providerId: Long? = null
        set(providerId) {

            field = providerId
        }
        get() = field


    var mobileAcuity: Boolean? = null

    var webAcuity: Boolean? = null


    var econsult: Boolean? = null


    var census: Boolean? = null


    var handoff: Boolean? = null

    var newpatient: Boolean? = null
}
package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.providerEndpoints.model

import java.io.Serializable


class SendChatMessageInputRequestModel : Serializable {

    var id: Long? = null
        get() = field
        set(id) {
            field = id
        }

    //com.google.api.client.util.Key
    var senderId: Int? = null

    //com.google.api.client.util.Key
    var message: String? = null

    //com.google.api.client.util.Key
    var type: String? = null

    //com.google.api.client.util.Key
    var subType: String? = null

    //com.google.api.client.util.Key
    var patientId: Long? = null

    //com.google.api.client.util.Key
    var senderName: String? = null

    //com.google.api.client.util.Key
    var urgent: Boolean? = null

    //com.google.api.client.util.Key
    var important: Boolean? = null

    //com.google.api.client.util.Key
    var token: String? = null

    //com.google.api.client.util.Key
    var title: String? = null

    //com.google.api.client.util.Key
    var time: Long? = null
}
/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2018-10-08 17:45:39 UTC)
 * on 2020-07-20 at 06:48:21 UTC
 * Modify at your own risk.
 */
package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.loginEndpoints.model

import java.io.Serializable


class CommonResponse : Serializable {

    var activeList: List<Patient>? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var dischargedCount: Int? = null
        private set

    //com.google.api.client.util.Key
    var idToken: String? = null

    //com.google.api.client.util.Key
    var refreshToken: String? = null
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var dischargedList: List<Patient>? = null
        private set

    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var errorId: Int? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var errorMessage: String? = null
        private set

    //com.google.api.client.util.Key
    var feedbackForm: String? = null

    //com.google.api.client.util.Key
    var tutorial_url: String? = null
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    //    @com.google.api.client.json.JsonString
    var id: Long? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var pendingList: List<Patient>? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var provider: Provider? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var providerList: List<Provider>? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var room: Room? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var status: Boolean? = null
        private set

    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var email: String? = null

    //com.google.api.client.util.Key
    var encryptionKey: String? = null

    //com.google.api.client.util.Key
    var aesEncryptionKey: String? = null

    //com.google.api.client.util.Key
    var agoraAppId: String? = null

    //com.google.api.client.util.Key
    var agoraAppCertificate: String? = null
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var unit: Unit? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var ward: Ward? = null
        private set

    /**
     * @param activeList activeList or `null` for none
     */
    fun setActiveList(activeList: List<Patient>?): CommonResponse {
        this.activeList = activeList
        return this
    }

    /**
     * @param dischargedCount dischargedCount or `null` for none
     */
    fun setDischargedCount(dischargedCount: Int?): CommonResponse {
        this.dischargedCount = dischargedCount
        return this
    }

    /**
     * @param dischargedList dischargedList or `null` for none
     */
    fun setDischargedList(dischargedList: List<Patient>?): CommonResponse {
        this.dischargedList = dischargedList
        return this
    }

    /**
     * @param errorId errorId or `null` for none
     */
    fun setErrorId(errorId: Int?): CommonResponse {
        this.errorId = errorId
        return this
    }

    /**
     * @param errorMessage errorMessage or `null` for none
     */
    fun setErrorMessage(errorMessage: String?): CommonResponse {
        this.errorMessage = errorMessage
        return this
    }

    /**
     * @param id id or `null` for none
     */
    fun setId(id: Long?): CommonResponse {
        this.id = id
        return this
    }

    /**
     * @param pendingList pendingList or `null` for none
     */
    fun setPendingList(pendingList: List<Patient>?): CommonResponse {
        this.pendingList = pendingList
        return this
    }

    /**
     * @param provider provider or `null` for none
     */
    fun setProvider(provider: Provider?): CommonResponse {
        this.provider = provider
        return this
    }

    /**
     * @param providerList providerList or `null` for none
     */
    fun setProviderList(providerList: List<Provider>?): CommonResponse {
        this.providerList = providerList
        return this
    }

    /**
     * @param room room or `null` for none
     */
    fun setRoom(room: Room?): CommonResponse {
        this.room = room
        return this
    }

    /**
     * @param status status or `null` for none
     */
    fun setStatus(status: Boolean?): CommonResponse {
        this.status = status
        return this
    }

    /**
     * @param unit unit or `null` for none
     */
    fun setUnit(unit: Unit?): CommonResponse {
        this.unit = unit
        return this
    }

    /**
     * @param ward ward or `null` for none
     */
    fun setWard(ward: Ward?): CommonResponse {
        this.ward = ward
        return this
    } //    @Override
    //    public CommonResponse set(String fieldName, Object value) {
    //        return (CommonResponse) super.set(fieldName, value);
    //    }
    //    @Override
    //    public CommonResponse clone() {
    //        return (CommonResponse) super.clone();
    //    }
}
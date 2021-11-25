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

import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.loginEndpoints.model.LoginRequest
import java.io.Serializable


class LoginRequest : Serializable {
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var countryCode: String? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var email: String? = null
        private set

    /**
     * @return value or `null` for none
     */
    //com.google.api.client.util.Key
    var phoneNumber: String? = null
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var otp: String? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var password: String? = null
        private set

    //com.google.api.client.util.Key
    var token: String? = null
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //com.google.api.client.util.Key
    var phone: String? = null
        private set

    //com.google.api.client.util.Key
    var overrride: Boolean? = null

    //com.google.api.client.util.Key
    var loginStatus: Boolean? = null

    /**
     * @param countryCode countryCode or `null` for none
     */
    fun setCountryCode(countryCode: String?): LoginRequest {
        this.countryCode = countryCode
        return this
    }

    /**
     * @param email email or `null` for none
     */
    fun setEmail(email: String?): LoginRequest {
        this.email = email
        return this
    }

    /**
     * @param otp otp or `null` for none
     */
    fun setOtp(otp: String?): LoginRequest {
        this.otp = otp
        return this
    }

    /**
     * @param password password or `null` for none
     */
    fun setPassword(password: String?): LoginRequest {
        this.password = password
        return this
    }

    /**
     * @param phone phone or `null` for none
     */
    fun setPhone(phone: String?): LoginRequest {
        this.phone = phone
        return this
    } //  @Override
    //  public LoginRequest set(String fieldName, Object value) {
    //    return (LoginRequest) super.set(fieldName, value);
    //  }
    //
    //  @Override
    //  public LoginRequest clone() {
    //    return (LoginRequest) super.clone();
    //  }
}
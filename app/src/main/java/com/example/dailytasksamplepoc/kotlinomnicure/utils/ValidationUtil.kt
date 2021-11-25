package com.mvp.omnicure.kotlinactivity.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityLocalCareProviderSignUpFirstBinding
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.R
import com.mvp.omnicure.databinding.ActivityAddPatientVitalsBinding
import com.mvp.omnicure.databinding.ActivityLocalCareProviderSignUpFirstBinding
import com.mvp.omnicure.databinding.ActivityLocalCareProviderSignUpSecondBinding
import com.mvp.omnicure.utils.Constants

import java.util.ArrayList
import java.util.regex.Pattern

class ValidationUtil {
    fun isValidate(viewDataBinding: ViewDataBinding): String? {
        if (viewDataBinding is ActivityAddPatientVitalsBinding) {
            val binding: ActivityAddPatientVitalsBinding = viewDataBinding as ActivityAddPatientVitalsBinding
            return isPatientVitalsValid(binding)
        }
        else if (viewDataBinding is ActivityLocalCareProviderSignUpFirstBinding) {
            return isRegistrationFirstFieldsValid(viewDataBinding)
        } else if (viewDataBinding is ActivityLocalCareProviderSignUpSecondBinding) {
            return isRegistrationLocalSecondFieldsValid(viewDataBinding)
        }

        return ""
    }

    fun passwordVisibility(editText: EditText, imageView: ImageView) {
        if (editText.transformationMethod == null) {
            editText.transformationMethod = PasswordTransformationMethod()
            editText.typeface = Typeface.DEFAULT
            imageView.setImageResource(R.drawable.ic_visibility)
            editText.setSelection(editText.text.length)
        } else {
            editText.transformationMethod = null
            editText.typeface = Typeface.DEFAULT
            imageView.setImageResource(R.drawable.ic_visibility_off)
            editText.setSelection(editText.text.length)
        }
    }





    private fun isRegistrationLocalSecondFieldsValid(binding: ActivityLocalCareProviderSignUpSecondBinding): String? {
        val context = binding.root.context
        var error = ""
        if (binding.lcpProviderTypeSpinner == null || binding.lcpProviderTypeSpinner.selectedItemPosition === 0) {
            error = context.getString(R.string.select_provider_type)
        } else if (binding.selectHospital == null || TextUtils.isEmpty(binding.selectHospital.text.toString().trim())) {
            error = context.getString(R.string.sel_hospital)
        } else if (binding.checkbox == null || !binding.checkbox.isChecked) {
            error = context.getString(R.string.accept_user_agreement)
        }
        return error
    }


    private fun isRegistrationFirstFieldsValid(binding: ActivityLocalCareProviderSignUpFirstBinding): String? {
        val context = binding.root.context
        val specialCharPattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val UpperCasePattern = Pattern.compile("[A-Z ]")
        val lowerCasePattern = Pattern.compile("[a-z ]")
        val digitCasePattern = Pattern.compile("[0-9 ]")
        var error = ""
        if (binding.idFirstName == null || TextUtils.isEmpty(binding.idFirstName.text.toString().trim())) {
            error = "error"
            binding.idFirstName.setErrorMessage(context.getString(R.string.first_name_empty_try_again))
        }
        if (binding.idLastName == null || TextUtils.isEmpty(binding.idLastName.text.toString().trim())) {
            error = "error"
            binding.idLastName.setErrorMessage(context.getString(R.string.last_name_empty_try_again))
        }
        if (binding.idEmailId == null || TextUtils.isEmpty(binding.idEmailId.text.toString().trim())) {
            error = "error"
            binding.idEmailId.setErrorMessage(context.getString(R.string.invalid_emailid_try_again))
        } else if (!UtilityMethods().isValidEmail(binding.idEmailId.text.toString())) {
            error = "error"
            binding.idEmailId.setErrorMessage(context.getString(R.string.invalid_emailid_try_again))
        }
        if (binding.idPhoneNumber == null || TextUtils.isEmpty(binding.idPhoneNumber.text.toString().trim())) {
            error = "error"
            binding.idPhoneNumber.setErrorMessage(context.getString(R.string.phone_number_empty))
            binding.phoneLayout.background = context.resources.getDrawable(R.drawable.error_edittext_bg)
            binding.seperator.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
        } else if (binding.idPhoneNumber.text.toString().startsWith("0")) {
            error = "error"
            binding.idPhoneNumber.setErrorMessage(context.getString(R.string.invalid_phone_no))
            binding.phoneLayout.background = context.resources.getDrawable(R.drawable.error_edittext_bg)
            binding.seperator.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
        } else if (binding.idPhoneNumber.text.toString().replace("-", "").matches(Constants.PHONE_PATTERN)
            && binding.idPhoneNumber.text.toString().replace("-", "").length < 10) {
            error = "error"
            binding.idPhoneNumber.setErrorMessage(context.getString(R.string.invalid_phone_no))
            binding.phoneLayout.background = context.resources.getDrawable(R.drawable.error_edittext_bg)
            binding.seperator.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
        }
        var passwordError = false
        if (binding.idPassword == null || TextUtils.isEmpty(binding.idPassword.text.toString())) {
            error = "error"
            passwordError = true
            binding.idPassword.setErrorMessage(context.getString(R.string.invalid_password_try_again))
        } else if (binding.idPassword.text.toString().length < 8) {
            error = "error"
            passwordError = true
            binding.idPassword.setErrorMessage(context.getString(R.string.invalid_password_try_again))
        } else if (!specialCharPattern.matcher(binding.idPassword.text.toString()).find()) {
            error = "error"
            passwordError = true
            binding.idPassword.setErrorMessage(context.getString(R.string.invalid_password_try_again))
        } else if (!UpperCasePattern.matcher(binding.idPassword.text.toString()).find()) {
            error = "error"
            passwordError = true
            binding.idPassword.setErrorMessage(context.getString(R.string.invalid_password_try_again))
        } else if (!lowerCasePattern.matcher(binding.idPassword.text.toString()).find()) {
            error = "error"
            passwordError = true
            binding.idPassword.setErrorMessage(context.getString(R.string.invalid_password_try_again))
        } else if (!digitCasePattern.matcher(binding.idPassword.text.toString()).find()) {
            error = "error"
            passwordError = true
            binding.idPassword.setErrorMessage(context.getString(R.string.invalid_password))
        }
       if (passwordError) {
            binding.passwordLayout.background = context.resources.getDrawable(R.drawable.error_edittext_bg)
            binding.passwordInfo.visibility = View.VISIBLE
            binding.passwordVerified.visibility = View.GONE
        }
       return error
    }

    fun isPassSpecialCharValid(str: String?): Boolean {
        val specialCharPattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val UpperCasePattern = Pattern.compile("[A-Z ]")
        val lowerCasePattern = Pattern.compile("[a-z ]")
        val digitCasePattern = Pattern.compile("[0-9 ]")
        return specialCharPattern.matcher(str).find()
    }

    fun isPassLowerCaseValid(str: String?): Boolean {
        val lowerCasePattern = Pattern.compile("[a-z ]")
        return lowerCasePattern.matcher(str).find()
    }

    fun isPassUpperCaseValid(str: String?): Boolean {
        val UpperCasePattern = Pattern.compile("[A-Z ]")
        return UpperCasePattern.matcher(str).find()
    }

    fun isPassNumberValid(str: String?): Boolean {
        val digitCasePattern = Pattern.compile("[0-9 ]")
        return digitCasePattern.matcher(str).find()
    }

    fun isPassCharsLengthValid(str: String): Boolean {
        return str.length >= 8
    }

    private fun containsPartOf(pass: String, username: String): Boolean {
        val requiredMin = 3
        var i = 0
        while (i + requiredMin < username.length) {
            if (pass.contains(username.substring(i, i + requiredMin))) {
                return true
            }
            i++
        }
        return false
    }


    fun checkPassword(str: String, binding: ActivityLocalCareProviderSignUpFirstBinding,
                      strFirstName: String, strLastName: String, strEmail: String): String? {
        var str = str
        var strFirstName = strFirstName
        var strLastName = strLastName
        var strEmail = strEmail
        val context = binding.root.context
        val specialCharPattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val UpperCasePattern = Pattern.compile("[A-Z ]")
        val lowerCasePattern = Pattern.compile("[a-z ]")
        val digitCasePattern = Pattern.compile("[0-9 ]")
        if (str.length < 8) {
            return context.getString(R.string.invalid_password_try_again)
        } else if (!specialCharPattern.matcher(str).find()) {
            return context.getString(R.string.invalid_password_try_again)
        } else if (!UpperCasePattern.matcher(str).find()) {
            return context.getString(R.string.invalid_password_try_again)
        } else if (!lowerCasePattern.matcher(str).find()) {
            return context.getString(R.string.invalid_password_try_again)
        } else if (!digitCasePattern.matcher(str).find()) {
            return context.getString(R.string.invalid_password_try_again)
        }
        if (!TextUtils.isEmpty(strFirstName) || !TextUtils.isEmpty(strLastName)) {
            strFirstName = strFirstName.toLowerCase()
            strLastName = strLastName.toLowerCase()
            str = str.toLowerCase()
            val fnameBool = strFirstName.trim { it <= ' ' }.length >= 3 && (str.startsWith(strFirstName) || str.matches(strFirstName) || str.contains(strFirstName))
            val lnameBool = strLastName.trim { it <= ' ' }.length >= 3 && (str.startsWith(strLastName) || str.matches(strLastName) || str.contains(strLastName))
            if (fnameBool || lnameBool) {
                return context.getString(R.string.invalid_password_try_again)
            }
        }
        if (!TextUtils.isEmpty(strEmail)) {
            strEmail = strEmail.toLowerCase()
            str = str.toLowerCase()
            val split = strEmail.split("@".toRegex()).toTypedArray()
            if (split.size > 0) {
                strEmail = split[0]
                if (str.startsWith(strEmail) || str.matches(strEmail) || str.contains(strEmail)) {
                    return context.getString(R.string.invalid_password_try_again)
                }
            }
        }
        return if (ValidationUtil().isRegularPassword(str)) {
            context.getString(R.string.invalid_password_try_again)
        } else null
    }



    fun checkPhoneNo(binding: ActivityLocalCareProviderSignUpFirstBinding): String? {
        val context = binding.root.context
        if (binding.idPhoneNumber == null || TextUtils.isEmpty(binding.idPhoneNumber.text.toString().trim())) {
            return context.getString(R.string.invalid_phone_no_try_again)
        } else if (binding.idPhoneNumber.text.toString().startsWith("0")) {
            return context.getString(R.string.invalid_phone_no_try_again)
        } else if (binding.idPhoneNumber.text.toString().replace("-", "").matches(Constants.PHONE_PATTERN)
            && binding.idPhoneNumber.text.toString().replace("-", "").length < 10) {
            return context.getString(R.string.invalid_phone_no_try_again)
        }
        return null
    }

    fun checkPhoneNo(str: String?): Boolean? {
        if (str == null || TextUtils.isEmpty(str.trim { it <= ' ' })) {
            return false
        } else if (str.startsWith("0")) {
            return false
        } else if (str.replace("-", "").matches(Constants.PHONE_PATTERN)
            && str.replace("-", "").length < 10) {
            return false
        }
        return true
    }

    fun checkPassword(str: String): Boolean? {
        val specialCharPattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val UpperCasePattern = Pattern.compile("[A-Z ]")
        val lowerCasePattern = Pattern.compile("[a-z ]")
        val digitCasePattern = Pattern.compile("[0-9 ]")
        if (str.length < 8) {
            return false
        } else if (!specialCharPattern.matcher(str).find()) {
            return false
        } else if (!UpperCasePattern.matcher(str).find()) {
            return false
        } else if (!lowerCasePattern.matcher(str).find()) {
            return false
        } else if (!digitCasePattern.matcher(str).find()) {
            return false
        }
        return true
    }

    fun checkPasswordValid(str: String, strFirstName: String, strLastName: String, strEmail: String): Boolean? {
        var str = str
        var strFirstName = strFirstName
        var strLastName = strLastName
        var strEmail = strEmail
        val specialCharPattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val UpperCasePattern = Pattern.compile("[A-Z ]")
        val lowerCasePattern = Pattern.compile("[a-z ]")
        val digitCasePattern = Pattern.compile("[0-9 ]")
        if (str.length < 8) {
            return false
        } else if (!specialCharPattern.matcher(str).find()) {
            return false
        } else if (!UpperCasePattern.matcher(str).find()) {
            return false
        } else if (!lowerCasePattern.matcher(str).find()) {
            return false
        } else if (!digitCasePattern.matcher(str).find()) {
            return false
        }
        if (!TextUtils.isEmpty(strFirstName) || !TextUtils.isEmpty(strLastName)) {
            strFirstName = strFirstName.toLowerCase()
            strLastName = strLastName.toLowerCase()
            str = str.toLowerCase()
            if (str.startsWith(strFirstName) || str.matches(strFirstName) || str.contains(strFirstName) ||
                str.startsWith(strLastName) || str.matches(strLastName) || str.contains(strLastName)) {
                return false
            }
        }
        if (!TextUtils.isEmpty(strEmail)) {
            strEmail = strEmail.toLowerCase()
            str = str.toLowerCase()
            val split = strEmail.split("@".toRegex()).toTypedArray()
            if (split.size > 0) {
                strEmail = split[0]
                if (str.startsWith(strEmail) || str.matches(strEmail) || str.contains(strEmail)) {
                    return false
                }
            }
        }
        return !isRegularPassword(str)
    }

    fun checkEdittext(editText: EditText?): Boolean? {
        return !(editText == null || TextUtils.isEmpty(editText.text.toString().trim { it <= ' ' }))
    }

    fun checkTextView(textView: TextView?): Boolean? {
        return !(textView == null || TextUtils.isEmpty(textView.text.toString().trim { it <= ' ' }))
    }

    fun checkNpi(editText: EditText?): Boolean? {
        if (editText == null || TextUtils.isEmpty(editText.text.toString().trim { it <= ' ' })) {
            return false
        } else if (editText.text.toString().length != 10) {
            return false
        }
        return true
    }

    fun checkEmail(str: String?): Boolean? {
        if (str == null || TextUtils.isEmpty(str.trim { it <= ' ' })) {
            return false
        } else if (!UtilityMethods().isValidEmail(str.toString())) {
            return false
        }
        return true
    }

    fun checkEmail(binding: ActivityLocalCareProviderSignUpFirstBinding): String? {
        val context = binding.root.context
        if (binding.idEmailId == null || TextUtils.isEmpty(binding.idEmailId.text.toString().trim())) {
            return context.getString(R.string.invalid_emailid_try_again)
        } else if (!UtilityMethods().isValidEmail(binding.idEmailId.text.toString())) {
            return context.getString(R.string.invalid_emailid_try_again)
        }
        return null
    }

    private fun isPatientVitalsValid(binding: ActivityAddPatientVitalsBinding): String? {
        val context: Context = binding.getRoot().getContext()
        if (binding.radioGrpOxygen.getCheckedRadioButtonId() === -1) {
            return context.getString(R.string.select_oxygen)
        } else if (binding.radioGrpAvpu1.getCheckedRadioButtonId() === -1 && binding.radioGrpAvpu2.getCheckedRadioButtonId() === -1) {
            return context.getString(R.string.select_AVPU)
        } else if (binding.covidGrp.getCheckedRadioButtonId() === -1) {
            return context.getString(R.string.select_covid)
        } else if (binding.idNotes == null || binding.idNotes.getText().toString().toLowerCase().equals("chief complaint:")) {
            return context.getString(R.string.chief_complaint_empty_addpatient)
        }
        return ""
    }


    fun showPasswordValidationPopup(context: Context?, str: String) {
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.password_validation_dialog)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val lowerIcon = dialog.findViewById<ImageView>(R.id.lower_icon)
        val upperIcon = dialog.findViewById<ImageView>(R.id.upper_icon)
        val numberIcon = dialog.findViewById<ImageView>(R.id.number_icon)
        val symbolIcon = dialog.findViewById<ImageView>(R.id.symbol_icon)
        val charsIcon = dialog.findViewById<ImageView>(R.id.char_icon)
        if (isPassCharsLengthValid(str)) {
            charsIcon.visibility = View.VISIBLE
        } else {
            charsIcon.visibility = View.GONE
        }
        if (isPassLowerCaseValid(str)) {
            lowerIcon.visibility = View.VISIBLE
        } else {
            lowerIcon.visibility = View.GONE
        }
        if (isPassUpperCaseValid(str)) {
            upperIcon.visibility = View.VISIBLE
        } else {
            upperIcon.visibility = View.GONE
        }
        if (isPassNumberValid(str)) {
            numberIcon.visibility = View.VISIBLE
        } else {
            numberIcon.visibility = View.GONE
        }
        if (isPassSpecialCharValid(str)) {
            symbolIcon.visibility = View.VISIBLE
        } else {
            symbolIcon.visibility = View.GONE
        }
        dialog.show()
    }


    fun showPasswordValidationDialog(context: Context?, str: String, strFirstName: String,
                                     strLastName: String, strEmail: String) {
        var str = str
        var strFirstName = strFirstName
        var strLastName = strLastName
        var strEmail = strEmail
        val dialog = Dialog(context!!, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.password_validation_dialog)
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val lowerIcon = dialog.findViewById<ImageView>(R.id.lower_icon)
        val upperIcon = dialog.findViewById<ImageView>(R.id.upper_icon)
        val numberIcon = dialog.findViewById<ImageView>(R.id.number_icon)
        val symbolIcon = dialog.findViewById<ImageView>(R.id.symbol_icon)
        val charsIcon = dialog.findViewById<ImageView>(R.id.char_icon)
        val first_last_icon = dialog.findViewById<ImageView>(R.id.first_last_icon)
        val emailIcon = dialog.findViewById<ImageView>(R.id.email_icon)
        val commonIcon = dialog.findViewById<ImageView>(R.id.common_icon)
        if (isPassCharsLengthValid(str)) {
            charsIcon.visibility = View.VISIBLE
        } else {
            charsIcon.visibility = View.GONE
        }
        if (isPassLowerCaseValid(str)) {
            lowerIcon.visibility = View.VISIBLE
        } else {
            lowerIcon.visibility = View.GONE
        }
        if (isPassUpperCaseValid(str)) {
            upperIcon.visibility = View.VISIBLE
        } else {
            upperIcon.visibility = View.GONE
        }
        if (isPassNumberValid(str)) {
            numberIcon.visibility = View.VISIBLE
        } else {
            numberIcon.visibility = View.GONE
        }
        if (isPassSpecialCharValid(str)) {
            symbolIcon.visibility = View.VISIBLE
        } else {
            symbolIcon.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(strFirstName) || !TextUtils.isEmpty(strLastName)) {
            strFirstName = strFirstName.toLowerCase()
            strLastName = strLastName.toLowerCase()
            str = str.toLowerCase()
            if (str.startsWith(strFirstName) || str.matches(strFirstName) || str.contains(strFirstName) ||
                str.startsWith(strLastName) || str.matches(strLastName) || str.contains(strLastName)) {
                first_last_icon.visibility = View.GONE
            } else {
                first_last_icon.visibility = View.VISIBLE
            }
        }
        if (!TextUtils.isEmpty(strEmail)) {
            strEmail = strEmail.toLowerCase()
            str = str.toLowerCase()
            val split = strEmail.split("@".toRegex()).toTypedArray()
            if (split.size > 0) {
                strEmail = split[0]
                if (str.startsWith(strEmail) || str.matches(strEmail) || str.contains(strEmail)) {
                    emailIcon.visibility = View.GONE
                } else {
                    emailIcon.visibility = View.VISIBLE
                }
            }
        }
        if (isRegularPassword(str)) {
            commonIcon.visibility = View.GONE
        } else {
            commonIcon.visibility = View.VISIBLE
        }
        dialog.show()
    }



    fun isValidPhone(str: String): Boolean {
        var s = str
        if (str.startsWith(Constants.US_COUNTRY_CODE + "-")) {
            s = str.replace(Constants.US_COUNTRY_CODE + "-", "")
        } else if (str.startsWith(Constants.US_COUNTRY_CODE)) {
            s = str.replace(Constants.US_COUNTRY_CODE, "")
        }
        if (TextUtils.isDigitsOnly(s) && s.length == 10) {
            return true
        }
        return if (s.length == 12 && s.replace("-", "").length == 10 && TextUtils.isDigitsOnly(s.replace("-", ""))) {
            true
        } else false
    }

    private fun isRegularPassword(password: String): Boolean {
        var password = password
        password = password.toLowerCase()
        val commonPasswordArray = getCommonPasswordArray()
        for (i in commonPasswordArray.indices) {
            val seqStr = commonPasswordArray[i].toLowerCase()
            if (password.startsWith(seqStr)) {
                return true
            }
            if (password.endsWith(seqStr)) {
                return true
            }
            if (password.contains(seqStr)) {
                return true
            }
        }
        return false
    }

    fun getCommonPasswordArray(): List<String> {
        var sequenceWordList = ArrayList<String>()
        sequenceWordList = ArrayList()
        sequenceWordList.add("qwerty")
        sequenceWordList.add("asdfgh")
        sequenceWordList.add("zxcvbn")
        sequenceWordList.add("password")
        sequenceWordList.add("admin")
        sequenceWordList.add("test")
        return sequenceWordList
    }


    private fun <T> Comparable<T>.matches(regex: T): Boolean {
        return true
    }
}
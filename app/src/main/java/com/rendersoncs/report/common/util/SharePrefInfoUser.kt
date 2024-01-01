package com.rendersoncs.report.common.util

import android.content.SharedPreferences
import com.rendersoncs.report.common.constants.ReportConstants
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class SharePrefInfoUser @Inject constructor(private var sharedPreferences: SharedPreferences){
    fun savePhotoSharePref(photo: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(ReportConstants.FIREBASE.FIRE_PHOTO, photo)
        editor.apply()
    }

    fun saveUserSharePref(user: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(ReportConstants.FIREBASE.FIRE_NAME, user)
        editor.apply()
    }

    fun saveEmailSharePref(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString(ReportConstants.FIREBASE.FIRE_EMAIL, email)
        editor.apply()
    }

    fun getUserSharePref(
        profileName: MutableStateFlow<String>,
        profileView: MutableStateFlow<String>,
        profileEmail: MutableStateFlow<String>
    ) {
        val name = sharedPreferences.getString(ReportConstants.FIREBASE.FIRE_NAME, "")
        val photo = sharedPreferences.getString(ReportConstants.FIREBASE.FIRE_PHOTO, "")
        val email = sharedPreferences.getString(ReportConstants.FIREBASE.FIRE_EMAIL, "")
        profileName.value = name.toString()
        profileView.value = photo.toString()
        profileEmail.value = email.toString()
    }

    fun getUser(): String {
        return sharedPreferences.getString(ReportConstants.FIREBASE.FIRE_NAME, "").toString()
    }

    fun deleteSharePref() {
        val editor = sharedPreferences.edit().clear()
        editor.apply()
    }

    fun getKey(key: String): Boolean {
        return sharedPreferences.contains(key)
    }
}
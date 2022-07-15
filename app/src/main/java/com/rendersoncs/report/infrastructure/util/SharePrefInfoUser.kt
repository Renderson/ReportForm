package com.rendersoncs.report.infrastructure.util

import android.content.SharedPreferences
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import kotlinx.coroutines.flow.MutableStateFlow

class SharePrefInfoUser {
    fun savePhotoSharePref(sharedPreferences: SharedPreferences, photo: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(ReportConstants.FIREBASE.FIRE_PHOTO, photo)
        editor.apply()
    }

    fun saveUserSharePref(sharedPreferences: SharedPreferences, user: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(ReportConstants.FIREBASE.FIRE_NAME, user)
        editor.apply()
    }

    fun saveEmailSharePref(sharedPreferences: SharedPreferences, email: String) {
        val editor = sharedPreferences.edit()
        editor.putString(ReportConstants.FIREBASE.FIRE_EMAIL, email)
        editor.apply()
    }

    fun getUserSharePref(sharedPreferences: SharedPreferences,
                         profileName: MutableStateFlow<String>,
                         profileView: MutableStateFlow<String>,
                         profileEmail: MutableStateFlow<String>) {
        val name = sharedPreferences.getString(ReportConstants.FIREBASE.FIRE_NAME, "")
        val photo = sharedPreferences.getString(ReportConstants.FIREBASE.FIRE_PHOTO, "")
        val email = sharedPreferences.getString(ReportConstants.FIREBASE.FIRE_EMAIL, "")
        profileName.value = name.toString()
        profileView.value = photo.toString()
        profileEmail.value = email.toString()
    }

    fun getUser(pref: SharedPreferences): String {
        return pref.getString(ReportConstants.FIREBASE.FIRE_NAME, "").toString()
    }

    fun deleteSharePref(sharedPreferences: SharedPreferences) {
        val editor = sharedPreferences.edit().clear()
        editor.apply()
    }
}
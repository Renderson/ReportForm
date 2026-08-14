package com.rendersoncs.report.common.util

import android.content.SharedPreferences
import com.rendersoncs.report.common.constants.ReportConstants
import javax.inject.Inject
import androidx.core.content.edit

class SharePrefInfoUser @Inject constructor(private var sharedPreferences: SharedPreferences){
    fun savePhotoSharePref(photo: String?) {
        sharedPreferences.edit {
            putString(ReportConstants.FIREBASE.FIRE_PHOTO, photo)
        }
    }

    fun saveUserSharePref(user: String?) {
        sharedPreferences.edit {
            putString(ReportConstants.FIREBASE.FIRE_NAME, user)
        }
    }

    fun saveEmailSharePref(email: String) {
        sharedPreferences.edit {
            putString(ReportConstants.FIREBASE.FIRE_EMAIL, email)
        }
    }

    fun getUser(): String {
        return sharedPreferences.getString(ReportConstants.FIREBASE.FIRE_NAME, "").toString()
    }

    fun getEmail(): String {
        return sharedPreferences.getString(ReportConstants.FIREBASE.FIRE_EMAIL, "").orEmpty()
    }

    fun getPhoto(): String {
        return sharedPreferences.getString(ReportConstants.FIREBASE.FIRE_PHOTO, "").orEmpty()
    }

    fun deleteSharePref() {
        val editor = sharedPreferences.edit().clear()
        editor.apply()
    }

    fun getKey(key: String): Boolean {
        return sharedPreferences.contains(key)
    }
}
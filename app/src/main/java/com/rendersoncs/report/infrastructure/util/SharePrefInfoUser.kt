package com.rendersoncs.report.infrastructure.util

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.rendersoncs.report.infrastructure.constants.ReportConstants

class SharePrefInfoUser {
    fun saveUserSharePref(context: Context, user: String?) {
        val sharedPreferences = context.getSharedPreferences(ReportConstants.FIREBASE.FIRE_USERS,
                Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(ReportConstants.FIREBASE.FIRE_NAME, user)
        editor.apply()
    }

    fun savePhotoSharePref(context: Context, photo: String?) {
        val sharedPreferences = context.getSharedPreferences(ReportConstants.FIREBASE.FIRE_USERS,
                Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(ReportConstants.FIREBASE.FIRE_PHOTO, photo)
        editor.apply()
    }

    fun getUserSharePref(context: Context, profileName: TextView, profileView: ImageView?) {
        val sharedPreferences = context.getSharedPreferences(ReportConstants.FIREBASE.FIRE_USERS,
                Context.MODE_PRIVATE)
        val name = sharedPreferences.getString(ReportConstants.FIREBASE.FIRE_NAME, "")
        val photo = sharedPreferences.getString(ReportConstants.FIREBASE.FIRE_PHOTO, "")
        profileName.text = name
        Glide.with(context).load(photo).into(profileView!!)
    }

    fun clearSharePref(context: Context) {
        val sharedPreferences = context.getSharedPreferences(ReportConstants.FIREBASE.FIRE_USERS,
                Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit().clear()
        editor.apply()
    }
}
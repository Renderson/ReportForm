package com.rendersoncs.report.infrastructure.util

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.rendersoncs.report.infrastructure.constants.ReportConstants

class GetInfoUserFireBase {
    private val sharePref = SharePrefInfoUser()
    fun getInfoUserFireBase(context: Context,
                            user: FirebaseUser,
                            databaseReference: DatabaseReference,
                            profileName: TextView, profileEmail: TextView,
                            profileView: ImageView?) {

        val pref = context.getSharedPreferences(ReportConstants.FIREBASE.FIRE_USERS, Context.MODE_PRIVATE)

        if (pref.contains(ReportConstants.FIREBASE.FIRE_NAME)
                && pref.contains(ReportConstants.FIREBASE.FIRE_PHOTO)) {
            sharePref.getUserSharePref(context, profileName, profileView)
        } else {
            for (profile in user.providerData) {
                val name = profile.displayName
                val photoUri = profile.photoUrl

                if (name == null || name.isEmpty()) {
                    databaseReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val nameCurrentUser = dataSnapshot.child(ReportConstants.FIREBASE.FIRE_USERS)
                                    .child(user.uid)
                                    .child(ReportConstants.FIREBASE.FIRE_CREDENTIAL)
                                    .child(ReportConstants.FIREBASE.FIRE_NAME)
                                    .value as String?
                            profileName.text = nameCurrentUser
                            sharePref.saveUserSharePref(context, nameCurrentUser)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }
                if (photoUri == null) {
                    databaseReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val currentPhoto = dataSnapshot.child(ReportConstants.FIREBASE.FIRE_USERS)
                                    .child(user.uid)
                                    .child(ReportConstants.FIREBASE.FIRE_PHOTO)
                                    .value as String?
                            Glide.with(context).load(currentPhoto).into(profileView!!)
                            sharePref.savePhotoSharePref(context, currentPhoto)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }
                profileName.text = name
            }
            profileName.text = user.displayName
            profileEmail.text = user.email
            Glide.with(context).load(user.photoUrl).into(profileView!!)
        }
    }
}
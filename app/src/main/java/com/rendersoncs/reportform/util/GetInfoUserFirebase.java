package com.rendersoncs.reportform.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class GetInfoUserFirebase {

    public void getInfoUserFirebase
            (Context context,
             FirebaseUser user,
             DatabaseReference databaseReference,
             TextView profileName, TextView profileEmail,
             ImageView profileView) {

        if (user != null) {

            for (UserInfo profile : user.getProviderData()) {
                String name = profile.getDisplayName();
                Uri photoUri = profile.getPhotoUrl();

                if (name != null) {

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String nameCurrentUser = (String) dataSnapshot.child("users").child(user.getUid()).child("credential").child("name").getValue();
                            profileName.setText(nameCurrentUser);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                if (photoUri == null) {
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String currentPhoto = (String) dataSnapshot.child("users").child(user.getUid()).child("photoUrl").getValue();

                            Glide.with(context).load(currentPhoto).into(profileView);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                profileName.setText(name);
            }
            profileName.setText(user.getDisplayName());
            profileEmail.setText(user.getEmail());
            Glide.with(context).load(user.getPhotoUrl()).into(profileView);
        }
    }

}

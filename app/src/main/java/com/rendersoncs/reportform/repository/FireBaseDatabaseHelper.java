package com.rendersoncs.reportform.repository;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rendersoncs.reportform.itens.Repo;

import java.util.ArrayList;
import java.util.List;

public class FireBaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private List<Repo> repository = new ArrayList<>();

    public interface DataStatus{
        void DataIsLoaded(List<Repo> repository, List<String> keys);
        void DataIsInsert();
        void DataIsUpdate();
        void DataIsDelete();
    }

    public FireBaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("Data");
    }

    public void readReport(final DataStatus dataStatus) {
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                repository.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Repo repo = keyNode.getValue(Repo.class);
                    repository.add(repo);
                }
                dataStatus.DataIsLoaded(repository, keys);
                Log.i("log", "Item: " + repository + " repository");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

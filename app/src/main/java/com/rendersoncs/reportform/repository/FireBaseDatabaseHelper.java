package com.rendersoncs.reportform.repository;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rendersoncs.reportform.itens.ReportItems;

import java.util.ArrayList;
import java.util.List;

public class FireBaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private List<ReportItems> repository = new ArrayList<>();

    public interface DataStatus{
        void DataIsLoaded(List<ReportItems> repository, List<String> keys);
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
                    ReportItems repo = keyNode.getValue(ReportItems.class);
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

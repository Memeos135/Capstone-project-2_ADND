package com.example.macros;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ProfileFragmentTwo extends Fragment {

    public ProfileFragmentTwo(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_activity_fragment_two, container, false);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            setupRecycler(view);
        }

        return view;
    }

    public void setupRecycler(View view){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupRecordList(recyclerView);
    }

    public void setupRecordList(RecyclerView recyclerView){
        ArrayList<ProfileRecordSetter> recordList = new ArrayList<>();

        readRecordsFirebase(recordList, recyclerView);

    }

    public void readRecordsFirebase(final ArrayList<ProfileRecordSetter> recordList, final RecyclerView recyclerView){

        final int year = Calendar.getInstance().get(Calendar.YEAR);
        final int month = Calendar.getInstance().get(Calendar.MONTH)+1;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentMacrosProgress")
                .child(String.valueOf(year)).child(String.valueOf(month));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                        ProfileRecordSetter profileRecordSetter = dataSnapshot1.getValue(ProfileRecordSetter.class);
                        profileRecordSetter.setDate(dataSnapshot1.getKey() + "/" + month + "/" + year);
                        recordList.add(profileRecordSetter);

                    }

                    ProfileRecyclerAdapter profileRecyclerAdapter = new ProfileRecyclerAdapter(getContext(), recordList);
                    recyclerView.setAdapter(profileRecyclerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

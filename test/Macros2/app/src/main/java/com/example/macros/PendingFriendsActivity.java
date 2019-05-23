package com.example.macros;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PendingFriendsActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_activity);

        context = this;

        setupRecycler();
    }

    public void setupRecycler(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        setupRecyclerData(recyclerView);
    }

    public void setupRecyclerData(final RecyclerView recyclerView){
        final ArrayList<String> pendingList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("pendingInvites");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        pendingList.add(dataSnapshot1.getValue().toString());
                    }
                    readUserRequestInfo(pendingList, recyclerView);
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });
    }

    public void readUserRequestInfo(final ArrayList<String> pendingList, final RecyclerView recyclerView){
        final ArrayList<PendingInfo> infoList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        for(int i = 0; i < pendingList.size(); i++){
            final int finalI = i;
            databaseReference.child(pendingList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.getKey().equals("userCreds")) {
                                String name = dataSnapshot1.child("name").getValue().toString();
                                String email = dataSnapshot1.child("email").getValue().toString();
                                String imageURL = dataSnapshot1.child("profile_picture").getValue().toString();

                                PendingInfo pendingInfo = new PendingInfo(name, email, imageURL, pendingList.get(finalI));
                                infoList.add(pendingInfo);
                            }
                        }
                        // init adapter
                        PendingAdapter pendingAdapter = new PendingAdapter(context, infoList);
                        recyclerView.setAdapter(pendingAdapter);
                    }
                }

                @Override
                public void onCancelled( DatabaseError databaseError) {

                }
            });
        }
    }

    public void backImageHandler(View view){
        onBackPressed();
    }
}

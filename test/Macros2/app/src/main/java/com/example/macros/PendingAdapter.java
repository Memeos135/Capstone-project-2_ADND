package com.example.macros;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.macros.ProfileActivity.flag;

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.MyViewHolder> {

    private ArrayList<PendingInfo> pendingInfo;
    private LayoutInflater mInflater;
    private boolean flag = false;

    public PendingAdapter(Context context, ArrayList<PendingInfo> pendingInfo){
        this.pendingInfo = pendingInfo;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.pending_activity_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {
        myViewHolder.email.setText(pendingInfo.get(i).getEmail());
        myViewHolder.name.setText(pendingInfo.get(i).getName());

        Picasso.get().load(pendingInfo.get(i).getImageURL()).centerCrop().fit().into(myViewHolder.photo);

        myViewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriend(i);
            }
        });
    }

    public void addFriend(final int position) {
        if (!flag) {
            flag = true;
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("friends");

            databaseReference.push().setValue(pendingInfo.get(position).getuID()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        removePending(position);
                    }
                }
            });
        }
    }

    public void removePending(final int position){
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("pendingInvites");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                if(dataSnapshot1.getValue().toString().equals(pendingInfo.get(position).getuID())){
                                    databaseReference.child(dataSnapshot1.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            addFriendToRequestee(position);
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void addFriendToRequestee(final int position){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(pendingInfo.get(position).getuID())
                .child("friends");

        databaseReference.push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    removeRequest(position);
                }
            }
        });
    }

    public void removeRequest(final int position){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(pendingInfo.get(position).getuID())
                .child("friendRequests");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if(dataSnapshot1.getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            databaseReference.child(dataSnapshot1.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    pendingInfo.remove(position);
                                    notifyDataSetChanged();
                                }
                            });
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return pendingInfo.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView email;
        TextView name;
        ImageView photo;
        Button button;

        public MyViewHolder( View itemView) {
            super(itemView);

            button = itemView.findViewById(R.id.acceptButton);
            photo = itemView.findViewById(R.id.send_image);
            name = itemView.findViewById(R.id.friend_name);
            email = itemView.findViewById(R.id.email_val);
        }
    }
}

package com.example.macros;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class FriendListRecyclerAdapter extends RecyclerView.Adapter<FriendListRecyclerAdapter.MyViewHolder> {


    private LayoutInflater mInflater;
    private ArrayList<FriendInfo> friendList;
    private boolean flag = false;

    public FriendListRecyclerAdapter(Context context,ArrayList<FriendInfo> friendList){
        this.mInflater = LayoutInflater.from(context);
        this.friendList = friendList;
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.friend_list_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int i) {
        myViewHolder.name.setText(friendList.get(i).getName());

        Picasso.get().load(friendList.get(i).getImage()).centerCrop().fit().into(myViewHolder.photo);

        myViewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInflater.getContext().startActivity(new Intent(mInflater.getContext(), UserProfileActivity.class)
                .putExtra("uID", friendList.get(i).getId()));
            }
        });

        myViewHolder.dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!flag) {
                    myViewHolder.spinner.setVisibility(View.VISIBLE);
                    myViewHolder.dropDown.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
                    flag = true;
                }else{
                    myViewHolder.spinner.setVisibility(View.GONE);
                    myViewHolder.dropDown.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
                    flag = false;
                }
            }
        });

        myViewHolder.remove_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myViewHolder.spinner.setVisibility(View.GONE);
                removeFromFirebase(i);
            }
        });
    }

    public void removeFromFirebase(final int position){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("friends");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    if(counter == position){

                        databaseReference.child(dataSnapshot1.getKey()).removeValue();
                        friendList.remove(position);
                        notifyDataSetChanged();
                        break;

                    }else{

                        counter++;

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
        return friendList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView photo;
        ImageView dropDown;
        LinearLayout spinner;
        TextView remove_friend;

        public MyViewHolder( View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.friend_name);
            photo = itemView.findViewById(R.id.send_image);
            dropDown = itemView.findViewById(R.id.drop_down_list);
            spinner = itemView.findViewById(R.id.spinner);
            remove_friend = itemView.findViewById(R.id.remove_friend);
        }
    }
}

package com.example.macros;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        // picasso friend image

        myViewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInflater.getContext().startActivity(new Intent(mInflater.getContext(), UserProfileActivity.class));
            }
        });

        myViewHolder.dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!flag) {
                    myViewHolder.spinner.setVisibility(View.VISIBLE);
                    flag = true;
                }else{
                    myViewHolder.spinner.setVisibility(View.GONE);
                    flag = false;
                }
            }
        });

        myViewHolder.remove_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myViewHolder.spinner.setVisibility(View.GONE);
                friendList.remove(i);
                notifyDataSetChanged();
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
            photo = itemView.findViewById(R.id.friend_photo);
            dropDown = itemView.findViewById(R.id.drop_down_list);
            spinner = itemView.findViewById(R.id.spinner);
            remove_friend = itemView.findViewById(R.id.remove_friend);
        }
    }
}

package com.example.macros;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendsRecyclerAdapter extends RecyclerView.Adapter<FriendsRecyclerAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<Friend> friendList;

    public FriendsRecyclerAdapter(Context context, ArrayList<Friend> friendList){
        this.mInflater = LayoutInflater.from(context);
        this.friendList = friendList;
    }
    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.chat_activity_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder( MyViewHolder myViewHolder, int i) {
        myViewHolder.last_seen.setText(friendList.get(i).getLast_seen());
        myViewHolder.last_message.setText(friendList.get(i).getLast_message());
        myViewHolder.name.setText(friendList.get(i).getName());
        // set user photo

        myViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInflater.getContext().startActivity(new Intent(mInflater.getContext(), SpecificChatActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView last_message;
        TextView last_seen;
        ImageView photo;
        ConstraintLayout container;

        public MyViewHolder( View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.friend_name);
            photo = itemView.findViewById(R.id.send_image);
            last_message = itemView.findViewById(R.id.last_msg_value);
            last_seen = itemView.findViewById(R.id.last_seen);
            container = itemView.findViewById(R.id.container);
        }
    }
}

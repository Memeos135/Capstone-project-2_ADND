package com.example.macros;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<FriendMessage> msgList;

    public MessageRecyclerAdapter(Context context, ArrayList<FriendMessage> msgList){
        this.mInflater = LayoutInflater.from(context);
        this.msgList = msgList;
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.specific_chat_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, int i) {
        // set message and initiate listeners
        myViewHolder.msg_time.setText(msgList.get(i).getTime());
        myViewHolder.msg.setText(msgList.get(i).getMsg());

        checkSource(myViewHolder, i);

        // set msg keyboard listener
    }

    public void checkSource(MyViewHolder myViewHolder, int i){
        if(msgList.get(i).getSource().startsWith("self")){
            myViewHolder.container.setBackgroundColor(myViewHolder.container.getResources().getColor(R.color.darker_light_blue));

            myViewHolder.container.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ((FrameLayout.LayoutParams) myViewHolder.container.getLayoutParams()).gravity = Gravity.START;

        }else{

            myViewHolder.container.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ((FrameLayout.LayoutParams) myViewHolder.container.getLayoutParams()).gravity = Gravity.END;
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView msg_time;
        TextView msg;
        ConstraintLayout container;
        public MyViewHolder( View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            msg_time = itemView.findViewById(R.id.msg_time);
            msg = itemView.findViewById(R.id.chat_msg);
        }
    }
}

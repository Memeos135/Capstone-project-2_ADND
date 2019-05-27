package com.example.macros;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<SearchNode> searchList;

    public SearchRecyclerAdapter(Context context, ArrayList<SearchNode> searchList){
        this.mInflater = LayoutInflater.from(context);
        this.searchList = searchList;
    }
    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.search_result_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {
        myViewHolder.username.setText(searchList.get(i).getName());

        myViewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mInflater.getContext().startActivity(new Intent(mInflater.getContext(), UserProfileActivity.class)
                    .putExtra("uID", searchList.get(i).getUID()), ActivityOptions.makeSceneTransitionAnimation((Activity) mInflater.getContext()).toBundle());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username;

        public MyViewHolder( View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
        }
    }
}

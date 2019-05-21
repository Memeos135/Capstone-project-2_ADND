package com.example.macros;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<SearchResult> searchList;

    public SearchRecyclerAdapter(Context context, ArrayList<SearchResult> searchList){
        this.mInflater = LayoutInflater.from(context);
        this.searchList = searchList;
    }
    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.search_result_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder( MyViewHolder myViewHolder, int i) {
        myViewHolder.username.setText(searchList.get(i).getName());

        myViewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInflater.getContext().startActivity(new Intent(mInflater.getContext(), UserProfileActivity.class));
            }
        });

        myViewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mInflater.getContext(), "AddButton", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        Button add;

        public MyViewHolder( View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
            add = itemView.findViewById(R.id.add_button);
        }
    }
}

package com.example.macros;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.MyViewHolder> {

    private LayoutInflater mInflated;
    private ArrayList<ProfileRecordSetter> recordList;

    public ProfileRecyclerAdapter(Context context, ArrayList<ProfileRecordSetter> recordList){
        this.recordList = recordList;
        this.mInflated = LayoutInflater.from(context);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflated.inflate(R.layout.profile_record_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
//        myViewHolder.check.setVisibility(View.GONE);
        myViewHolder.fats.setText(recordList.get(i).getFat());
        myViewHolder.carbs.setText(recordList.get(i).getCarbs());
        myViewHolder.protein.setText(recordList.get(i).getProtein());
        myViewHolder.date.setText(recordList.get(i).getDate());

        ProfileActivity.flag = false;
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView protein;
        TextView fats;
        TextView carbs;
        TextView date;
        ImageView check;

        public MyViewHolder(View itemView) {
            super(itemView);
            protein = itemView.findViewById(R.id.protein_value);
            carbs = itemView.findViewById(R.id.carbs_value);
            fats = itemView.findViewById(R.id.fat_value);
            date = itemView.findViewById(R.id.date);
            check = itemView.findViewById(R.id.goal_check);
        }
    }
}

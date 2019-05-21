package com.example.macros;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<NotesSetter> noteList;

    NotesRecyclerAdapter(Context context, ArrayList<NotesSetter> notesList){
        this.noteList = notesList;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.main_activity_recycler_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {

        myViewHolder.note.setText(noteList.get(i).getNote());
        myViewHolder.day.setText(noteList.get(i).getDay());
        myViewHolder.month.setText(noteList.get(i).getMonth());

        myViewHolder.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteList.remove(i);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView month;
        TextView day;
        TextView note;
        ImageView cross;

        public MyViewHolder(View itemView) {
            super(itemView);
            month = itemView.findViewById(R.id.note_month);
            day = itemView.findViewById(R.id.note_day);
            note = itemView.findViewById(R.id.note_content);
            cross = itemView.findViewById(R.id.cross);
        }
    }
}

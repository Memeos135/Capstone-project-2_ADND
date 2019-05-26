package com.example.macros;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        View view = mInflater.inflate(R.layout.activity_main_recycler_card, viewGroup, false);
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
                removeNoteFirebase(i, noteList.get(i).getDay(), getMonthNumber(noteList.get(i).getMonth()), noteList.get(i).getYear());
            }
        });
    }

    public void removeNoteFirebase(final int position, String day, String month, String year){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("currentMacrosProgress").child(year).child(month).child(day).child("notes");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    if(counter == position){

                        databaseReference.child(dataSnapshot1.getKey()).removeValue();
                        noteList.remove(position);
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

    public String getMonthNumber(String month){
        if(month.equals("JAN")){
            return String.valueOf(1);
        }else if(month.equals("FEB")){
            return String.valueOf(2);
        }else if(month.equals("MARCH")){
            return String.valueOf(3);
        }else if(month.equals("APRIL")){
            return String.valueOf(4);
        }else if(month.equals("MAY")){
            return String.valueOf(5);
        }else if(month.equals("JUNE")){
            return String.valueOf(6);
        }else if(month.equals("JULY")){
            return String.valueOf(7);
        }else if(month.equals("AUG")){
            return String.valueOf(8);
        }else if(month.equals("SEP")){
            return String.valueOf(9);
        }else if(month.equals("OCT")){
            return String.valueOf(10);
        }else if(month.equals("NOV")){
            return String.valueOf(11);
        }else{
            return String.valueOf(12);
        }
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

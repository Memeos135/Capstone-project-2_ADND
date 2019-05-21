package com.example.macros;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class MacrosNotesAdapter extends RecyclerView.Adapter<MacrosNotesAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<NotesSetter> noteList;

    public MacrosNotesAdapter(Context context, ArrayList<NotesSetter> noteList){
        this.mInflater = LayoutInflater.from(context);
        this.noteList = noteList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.macro_notes_card, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {
        myViewHolder.note.setText(noteList.get(i).getNote());

        myViewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // remove note from firebase
                removeNoteFirebase(i, noteList.get(i).getDay(), noteList.get(i).getMonth(), noteList.get(i).getYear());
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

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView note;
        ImageView remove;
        public MyViewHolder(View itemView) {
            super(itemView);
            note = itemView.findViewById(R.id.note);
            remove = itemView.findViewById(R.id.cross);
        }
    }
}

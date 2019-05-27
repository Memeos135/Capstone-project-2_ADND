package com.example.macros;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivityFragment extends Fragment{

    ArrayList<NotesSetter> notesList;
    NotesRecyclerAdapter notesRecyclerAdapter;

    public MainActivityFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_fragment, container, false);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            ((RelativeLayout) view.findViewById(R.id.loadingPanel)).setVisibility(View.VISIBLE);
            setupRecycler(view);
        }

        CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                // i > year
                // i1 > month
                // i2 > day
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(new Intent(getContext(), MacrosActivity.class)
                    .putExtra("day", i2)
                    .putExtra("month", i1)
                    .putExtra("year", i), ActivityOptions.makeSceneTransitionAnimation((Activity) getContext()).toBundle());
                }
            }
        });

        return view;
    }

    public void setupRecycler(View view){

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupRecyclerData(recyclerView);
    }

    public void setupRecyclerData(RecyclerView recyclerView){
        notesList = new ArrayList<>();
        readNotes(notesList, recyclerView);
    }

    public void readNotes(final ArrayList<NotesSetter> notesList, final RecyclerView recyclerView){
        final int month = Calendar.getInstance().get(Calendar.MONTH);
        final int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        final int year = Calendar.getInstance().get(Calendar.YEAR);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("currentMacrosProgress").child(String.valueOf(year)).child(String.valueOf(month+1)).child(String.valueOf(day)).child("notes");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        notesList.add(new NotesSetter(getMonth(month), String.valueOf(day), dataSnapshot1.getValue().toString(), String.valueOf(year)));
                    }
                    notesRecyclerAdapter = new NotesRecyclerAdapter(getContext(), notesList);
                    recyclerView.setAdapter(notesRecyclerAdapter);
                    ((RelativeLayout) getView().findViewById(R.id.loadingPanel)).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getMonth(int value){
        String[] months = {"JAN", "FEB", "MAR", "APRIL", "MAY", "JUNE", "JULY", "AUG", "SEPT", "OCT", "NOV", "DEC"};

        return months[value];
    }

    @Override
    public void onResume() {
        super.onResume();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            setupRecycler(getView());
        }
    }
}
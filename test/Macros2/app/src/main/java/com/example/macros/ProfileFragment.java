package com.example.macros;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ProfileFragment extends Fragment {

    public ProfileFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_activity_fragment, container, false);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            setupProgressBars(view);
        }
        return view;
    }

    public void setupProgressBars(final View view){
        final ProgressBar progressProtein = view.findViewById(R.id.protein_progress);
        final ProgressBar progressCarbs = view.findViewById(R.id.carbs_progress);
        final ProgressBar progressFats = view.findViewById(R.id.fat_progress);

        final TextView proteinRemain = view.findViewById(R.id.protein_remain);
        final TextView carbsRemain = view.findViewById(R.id.carbs_remain);
        final TextView fatsRemain = view.findViewById(R.id.fat_remain);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userGoalMacros");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int counter = 0;

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (counter == 0) {

                            progressCarbs.setMax(Integer.parseInt(dataSnapshot1.getValue().toString()));
                            carbsRemain.setText(" / " +dataSnapshot1.getValue().toString());

                        } else if (counter == 1) {

                            progressFats.setMax(Integer.parseInt(dataSnapshot1.getValue().toString()));
                            fatsRemain.setText(" / " +dataSnapshot1.getValue().toString());

                        } else {

                            progressProtein.setMax(Integer.parseInt(dataSnapshot1.getValue().toString()));
                            proteinRemain.setText(" / " +dataSnapshot1.getValue().toString());

                        }
                        counter++;
                    }

                    getCurrentProgress(progressCarbs.getMax(), progressFats.getMax(), progressProtein.getMax(), progressProtein, progressCarbs, progressFats, view);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getCurrentProgress(final int carbMax, final int fatMax, final int proteinMax, final ProgressBar protein, final ProgressBar carbs, final ProgressBar fat, final View view){

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH)+1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        final TextView proteinText = view.findViewById(R.id.protein_current);
        final TextView carbsText = view.findViewById(R.id.carbs_current);
        final TextView fatText = view.findViewById(R.id.fat_current);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("currentMacrosProgress").child(String.valueOf(year)).child(String.valueOf(month)).child(String.valueOf(day));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (!dataSnapshot1.getValue().toString().equals("notes")) {

                            if (dataSnapshot1.getKey().equals("carbs")) {

                                carbs.setProgress(Integer.parseInt(dataSnapshot1.getValue().toString()));
                                carbsText.setText(dataSnapshot1.getValue().toString());

                            } else if (dataSnapshot1.getKey().equals("protein")) {

                                protein.setProgress(Integer.parseInt(dataSnapshot1.getValue().toString()));
                                proteinText.setText(dataSnapshot1.getValue().toString());

                            } else if (dataSnapshot1.getKey().equals("fat")){

                                fat.setProgress(Integer.parseInt(dataSnapshot1.getValue().toString()));
                                fatText.setText(dataSnapshot1.getValue().toString());

                            }
                        }
                    }

                    calculateCalories(carbMax, fatMax, proteinMax, proteinText.getText().toString(), carbsText.getText().toString(), fatText.getText().toString(), view);

                }else{
//                    Toast.makeText(getContext(), "No records found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void calculateCalories(int carbMax, int fatMax, int proteinMax, String currentProtein, String currentCarbs, String currentFat, View view){

        int current_fours = (Integer.parseInt(currentProtein) + Integer.parseInt(currentCarbs))*4;
        int current_nines = Integer.parseInt(currentFat)*9;

        int current_total = current_fours + current_nines;

        int fours = (carbMax + proteinMax)*4;
        int nines = fatMax*9;

        int total = fours + nines;

        TextView cal = view.findViewById(R.id.calorie_value);
        cal.setText(String.valueOf(total));

        TextView cal_percent = view.findViewById(R.id.calorie_percent);

        double percent = ((double)current_total / (double)total)*100;

        cal_percent.setText((int) percent+"%");

        ProgressBar cal_progress = view.findViewById(R.id.progressBar);
        cal_progress.setProgress((int)percent);

        ProfileActivity.flag = false;

    }
}

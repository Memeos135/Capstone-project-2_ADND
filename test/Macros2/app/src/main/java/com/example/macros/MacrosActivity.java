package com.example.macros;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MacrosActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<NotesSetter> noteList;
    MacrosNotesAdapter macrosNotesAdapter;
    Context context;
    String year;
    String month;
    String day;

    String calorie_max;
    String calorie_percents;

    String protein_current;
    String protein_percents;
    String protein_max;
    String protein_remains;

    String carbs_current;
    String carbs_percents;
    String carbs_max;
    String carbs_remains;

    String fat_current;
    String fat_percents;
    String fat_max;
    String fat_remains;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.macros_activity);

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                noteList = new ArrayList<>();

                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.logged_drawer);

                setupDate();
                setupNotesRecycler();
                setupMacroInputListener();
                setupTargetTexts();
            }
        }
    }

    public void setupCalories(String protein, String carb, String fat, int protein_max, int carb_max, int fat_max){
        int max_calories = ((protein_max + carb_max)*4) + (fat_max*9);

        int fours = (Integer.parseInt(protein) + Integer.parseInt(carb))*4;
        int nines = Integer.parseInt(fat)*9;
        int current_calories = fours + nines;

        double percentage = ((double)current_calories / (double)max_calories)*100;

        TextView calorie_val = findViewById(R.id.calorie_value);
        TextView calorie_percent = findViewById(R.id.calorie_percent);

        calorie_val.setText(String.valueOf(max_calories));
        calorie_percent.setText((int)percentage+"%");

        ProgressBar cal_prog = findViewById(R.id.progressBar);
        cal_prog.setProgress((int)percentage);

        calorie_max = String.valueOf(max_calories);
        calorie_percents = String.valueOf((int)percentage);

    }

    public void setupProgressTexts(){
        final ProgressBar protein = findViewById(R.id.protein_progress);
        final ProgressBar carbs = findViewById(R.id.carbs_progress);
        final ProgressBar fat = findViewById(R.id.fat_progress);

        final TextView protein_label = findViewById(R.id.protein_remain);
        final TextView carbs_label = findViewById(R.id.carbs_remain);
        final TextView fat_label = findViewById(R.id.fat_remain);

        final TextView protein_percent = findViewById(R.id.protein_percent);
        final TextView carbs_percent = findViewById(R.id.carbs_percent);
        final TextView fat_percent = findViewById(R.id.fat_percent);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("currentMacrosProgress").child(year).child(month).child(day);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("protein").exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (!dataSnapshot1.getKey().equals("notes")) {

                            if (dataSnapshot1.getKey().equals("carbs")) {

                                carbs.setProgress(Integer.parseInt(dataSnapshot1.getValue().toString()));
                                carbs_label.setText(dataSnapshot1.getValue().toString());

                                double percentage = (Double.parseDouble(dataSnapshot1.getValue().toString()) / carbs.getMax()) * 100;
                                carbs_percent.setText((int) percentage + "%");

                                carbs_current = dataSnapshot1.getValue().toString();
                                carbs_percents = String.valueOf((int)percentage);

                            } else if (dataSnapshot1.getKey().equals("protein")) {

                                protein.setProgress(Integer.parseInt(dataSnapshot1.getValue().toString()));
                                protein_label.setText(dataSnapshot1.getValue().toString());

                                double percentage = (Double.parseDouble(dataSnapshot1.getValue().toString()) / protein.getMax()) * 100;
                                protein_percent.setText((int) percentage + "%");

                                protein_current = dataSnapshot1.getValue().toString();
                                protein_percents = String.valueOf((int)percentage);

                            } else {

                                fat.setProgress(Integer.parseInt(dataSnapshot1.getValue().toString()));
                                fat_label.setText(dataSnapshot1.getValue().toString());

                                double percentage = (Double.parseDouble(dataSnapshot1.getValue().toString()) / fat.getMax()) * 100;
                                fat_percent.setText((int) percentage + "%");

                                fat_current = dataSnapshot1.getValue().toString();
                                fat_percents = String.valueOf((int)percentage);

                            }
                        }
                    }
                    updateRemaining(protein_label.getText().toString(), carbs_label.getText().toString(), fat_label.getText().toString()
                            , protein, carbs, fat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateRemaining(String protein, String carbs, String fat, ProgressBar proteinProg, ProgressBar carbProg, ProgressBar fatProg){
        TextView protein_remain = findViewById(R.id.protein_current);
        TextView carbs_remain = findViewById(R.id.carbs_current);
        TextView fat_remain = findViewById(R.id.fat_current);

        int protein_sub = proteinProg.getMax() - Integer.parseInt(protein);
        int carb_sub = carbProg.getMax() - Integer.parseInt(carbs);
        int fat_sub = fatProg.getMax() - Integer.parseInt(fat);

        protein_remain.setText(String.valueOf(protein_sub));
        carbs_remain.setText(String.valueOf(carb_sub));
        fat_remain.setText(String.valueOf(fat_sub));

        protein_remains = String.valueOf(protein_sub);
        carbs_remains = String.valueOf(carb_sub);
        fat_remains = String.valueOf(fat_sub);

        setupCalories(protein, carbs, fat, proteinProg.getMax(), carbProg.getMax(), fatProg.getMax());
    }

    public void setupTargetTexts(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userGoalMacros");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    int counter = 0;

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        if (counter == 0) {

                            TextView carbs_target = findViewById(R.id.carbs_target);
                            carbs_target.setText(dataSnapshot1.getValue().toString());
                            setupProgressBarLimits("carbs", dataSnapshot1.getValue().toString());

                            carbs_max = dataSnapshot1.getValue().toString();

                        } else if (counter == 1) {

                            TextView fat_target = findViewById(R.id.fat_target);
                            fat_target.setText(dataSnapshot1.getValue().toString());
                            setupProgressBarLimits("fat", dataSnapshot1.getValue().toString());

                            fat_max = dataSnapshot1.getValue().toString();

                        } else {

                            TextView protein_target = findViewById(R.id.protein_target);
                            protein_target.setText(dataSnapshot1.getValue().toString());
                            setupProgressBarLimits("protein", dataSnapshot1.getValue().toString());

                            protein_max = dataSnapshot1.getValue().toString();
                        }
                        counter++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setupProgressBarLimits(String category, String value){

        if(category.equals("protein")){

            ProgressBar protein = findViewById(R.id.protein_progress);
            protein.setMax(Integer.parseInt(value));

        }else if(category.equals("carbs")){

            ProgressBar carbs = findViewById(R.id.carbs_progress);
            carbs.setMax(Integer.parseInt(value));

        }else{

            ProgressBar fat = findViewById(R.id.fat_progress);
            fat.setMax(Integer.parseInt(value));

        }

        setupProgressTexts();
    }

    public void setupDate(){
        TextView date = findViewById(R.id.date);

        year = String.valueOf(getIntent().getIntExtra("year", 0));
        month = String.valueOf((getIntent().getIntExtra("month", 0) + 1));
        day = String.valueOf(getIntent().getIntExtra("day", 0));

        date.setText(day + "/" + month + "/" + year);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {

            startActivity(new Intent(this, ProfileActivity.class));

        } else if (id == R.id.signup) {
            // Testing default activity enter/exit animation
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                startActivity(new Intent(this, SignupActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//            }

            startActivity(new Intent(this, SignupActivity.class));

        } else if (id == R.id.signin) {

            startActivity(new Intent(this, LoginActivity.class));

        } else if (id == R.id.pending) {

            Toast.makeText(this, "Pending", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.signout) {

            FirebaseAuth.getInstance().signOut();
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);

            resetNav();

        } else if (id == R.id.home) {

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void fabHandler(View view){

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.notes_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.create();
        }
        dialog.show();

        setupDialogListener(dialog);
    }

    public void setupDialogListener(Dialog dialog){

        final EditText note = dialog.findViewById(R.id.msg_text);

        note.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    if(!note.getText().toString().equals("")){
                        if(noteList.size() > 0) {
                            if (!noteList.get(noteList.size() - 1).getNote().equals(note.getText().toString())) {
                                // upload to firebase
                                storeNotesFirebase(note.getText().toString(), note);
                                noteList.add(new NotesSetter(month, day, note.getText().toString(), year));
                                return true;
                            }
                        }else{
                            // upload to firebase
                            storeNotesFirebase(note.getText().toString(), note);
                            noteList.add(new NotesSetter(month, day, note.getText().toString(), year));
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    public void storeNotesFirebase(final String note, final EditText view){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("currentMacrosProgress").child(year).child(month).child(day).child("notes");

        databaseReference.push().setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    macrosNotesAdapter.notifyDataSetChanged();
                    view.setText("");
                }else{
                    Toast.makeText(context, "Failed to save note", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void setupNotesRecycler(){
        RecyclerView recyclerView = findViewById(R.id.notesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        setupNotesAdapter(recyclerView);

    }

    public void setupNotesAdapter(RecyclerView recyclerView){
        //read notes from firebase
        readNotesFirebase(noteList, recyclerView);
    }

    public void readNotesFirebase(final ArrayList<NotesSetter> noteList, final RecyclerView recyclerView){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("currentMacrosProgress").child(year).child(month).child(day).child("notes");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        noteList.add(new NotesSetter(month, day, dataSnapshot1.getValue().toString(), year));
                    }
                    macrosNotesAdapter = new MacrosNotesAdapter(context, noteList);
                    recyclerView.setAdapter(macrosNotesAdapter);
                }else{
                    macrosNotesAdapter = new MacrosNotesAdapter(context, noteList);
                    recyclerView.setAdapter(macrosNotesAdapter);
                    Toast.makeText(MacrosActivity.this, "No notes have been found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void backHandler(View view){
        onBackPressed();
    }

    public void targetHandler(View view){
        TextView target = (TextView) view;

        if(target.getTag().equals("carb_target")){

            showTargetDialog("carbs");

        }else if(target.getTag().equals("protein_target")){

            showTargetDialog("protein");

        }else{

            showTargetDialog("fats");

        }
    }

    public void showTargetDialog(final String category){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.target_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.create();
        }
        dialog.show();

        TextView cat_label = dialog.findViewById(R.id.category);
        cat_label.setText(category.toUpperCase());

        final EditText input = dialog.findViewById(R.id.cat_input);

        input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    // edit in firebase
                    editGoalMacros(category, input.getText().toString(), input);
                    return true;
                }
                return false;
            }
        });
    }

    public void editGoalMacros(final String category, final String val, final EditText input){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userGoalMacros").child(category);

        databaseReference.setValue(val).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    input.setText("");

                    if(category.equals("carbs")){
                        setupTargetTexts();
                    }else if(category.equals("protein")){
                        setupTargetTexts();
                    }else{
                        setupTargetTexts();
                    }
                }else{
                    Toast.makeText(context, "Target edit has failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void setupMacroInputListener(){
        final EditText protein_input = findViewById(R.id.protein_prog_input);
        final EditText carbs_input = findViewById(R.id.carb_prog_input);
        final EditText fat_input = findViewById(R.id.fat_prog_input);

        protein_input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && !protein_input.getText().toString().equals("")) {
                    storeCurrentProgress("protein", protein_input);
                    setupTargetTexts();
                    return true;
                }
                return false;
            }
        });

        carbs_input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && !carbs_input.getText().toString().equals("")) {
                    storeCurrentProgress("carbs", carbs_input);
                    setupTargetTexts();
                    return true;
                }
                return false;
            }
        });

        fat_input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && !fat_input.getText().toString().equals("")) {
                    storeCurrentProgress("fat", fat_input);
                    setupTargetTexts();
                    return true;
                }
                return false;
            }
        });
    }

    public void storeCurrentProgress(String category, final EditText value){

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {

            year = String.valueOf(getIntent().getIntExtra("year", 0));
            month = String.valueOf((getIntent().getIntExtra("month", 0) + 1));
            day = String.valueOf(getIntent().getIntExtra("day", 0));

            getPreviousValue(category, value);

        }else{
            Toast.makeText(context, "Please login to store your macros", Toast.LENGTH_SHORT).show();
        }
    }

    public void getPreviousValue(final String category, final EditText value){
        final int[] val = {0};

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentMacrosProgress")
                .child(year).child(month).child(day).child(category);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    val[0] = Integer.parseInt(dataSnapshot.getValue().toString());
                    int total = val[0] + Integer.parseInt(value.getText().toString());

                    databaseReference.setValue(String.valueOf(total)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                value.setText("");
                                setupTargetTexts();
                            } else {
                                Toast.makeText(context, "Failed to add macro", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{

                    if(category.equals("protein")){
                        DumpClass dumpClass = new DumpClass(value.getText().toString(), "0", "0", "");
                        callDefault(dumpClass, value);
                    }else if(category.equals("carbs")){
                        DumpClass dumpClass = new DumpClass("0", value.getText().toString(), "0", "");
                        callDefault(dumpClass, value);
                    }else{
                        DumpClass dumpClass = new DumpClass("0", "0", value.getText().toString(), "");
                        callDefault(dumpClass, value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void callDefault(DumpClass dumpClass, final EditText value){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentMacrosProgress")
                .child(year).child(month).child(day);

        databaseReference.setValue(dumpClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    value.setText("");
                } else {
                    Toast.makeText(context, "Failed to add macro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.logged_drawer);
        }else{
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("protein_current", protein_current);
        outState.putString("protein_max", protein_max);
        outState.putString("protein_percent", protein_percents);
        outState.putString("protein_remain", protein_remains);

        outState.putString("carbs_current", carbs_current);
        outState.putString("carbs_max", carbs_max);
        outState.putString("carbs_percent", carbs_percents);
        outState.putString("carbs_remain", carbs_remains);

        outState.putString("fat_current", fat_current);
        outState.putString("fat_max", fat_max);
        outState.putString("fat_percent", fat_percents);
        outState.putString("fat_remain", fat_remains);

        outState.putString("calorie_max", calorie_max);
        outState.putString("calorie_percent", calorie_percents);

        outState.putString("year", String.valueOf(getIntent().getIntExtra("year",0)));
        outState.putString("month", String.valueOf((getIntent().getIntExtra("month",0)+1)));
        outState.putString("day", String.valueOf(getIntent().getIntExtra("day", 0)));

        outState.putParcelableArrayList("noteList", noteList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        year = savedInstanceState.getString("year");
        month = savedInstanceState.getString("month");
        day = savedInstanceState.getString("day");

        carbs_current = savedInstanceState.getString("carbs_current");
        carbs_max = savedInstanceState.getString("carbs_max");
        carbs_percents = savedInstanceState.getString("carbs_percent");
        carbs_remains = savedInstanceState.getString("carbs_remain");

        protein_current = savedInstanceState.getString("protein_current");
        protein_max = savedInstanceState.getString("protein_max");
        protein_percents = savedInstanceState.getString("protein_percent");
        protein_remains = savedInstanceState.getString("protein_remain");

        fat_current = savedInstanceState.getString("fat_current");
        fat_max = savedInstanceState.getString("fat_max");
        fat_percents = savedInstanceState.getString("fat_percent");
        fat_remains = savedInstanceState.getString("fat_remain");

        calorie_max = savedInstanceState.getString("calorie_max");
        calorie_percents = savedInstanceState.getString("calorie_percent");

        noteList = savedInstanceState.getParcelableArrayList("noteList");
        RecyclerView recyclerView = findViewById(R.id.notesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        macrosNotesAdapter = new MacrosNotesAdapter(context, noteList);
        recyclerView.setAdapter(macrosNotesAdapter);

        TextView dates = findViewById(R.id.date);
        dates.setText(day+"/"+month+"/"+year);

        TextView calorie_val = findViewById(R.id.calorie_value);
        TextView calorie_percent = findViewById(R.id.calorie_percent);
        calorie_val.setText(calorie_max);
        calorie_percent.setText(calorie_percents+"%");

        TextView protein_currents = findViewById(R.id.protein_remain);
        TextView protein_perc = findViewById(R.id.protein_percent);
        TextView protein_maxs = findViewById(R.id.protein_target);
        TextView protein_rem = findViewById(R.id.protein_current);
        protein_currents.setText(protein_current);
        protein_perc.setText(protein_percents+"%");
        protein_maxs.setText(protein_max);
        protein_rem.setText(protein_remains);

        TextView carbs_currents = findViewById(R.id.carbs_remain);
        TextView carbs_perc = findViewById(R.id.carbs_percent);
        TextView carbs_maxs = findViewById(R.id.carbs_target);
        TextView carbs_rem = findViewById(R.id.carbs_current);
        carbs_currents.setText(carbs_current);
        carbs_perc.setText(carbs_percents+"%");
        carbs_maxs.setText(carbs_max);
        carbs_rem.setText(carbs_remains);

        TextView fat_currents = findViewById(R.id.fat_remain);
        TextView fat_perc = findViewById(R.id.fat_percent);
        TextView fat_maxs = findViewById(R.id.fat_target);
        TextView fat_rem = findViewById(R.id.fat_current);
        fat_currents.setText(fat_current);
        fat_perc.setText(fat_percents+"%");
        fat_maxs.setText(fat_max);
        fat_rem.setText(fat_remains);

        setupMacroInputListener();

    }

    public void resetNav(){
        TextView name = findViewById(R.id.user_name);
        TextView email = findViewById(R.id.user_email);
        ImageView photo = findViewById(R.id.send_image);

        name.setText(R.string.user_name);
        email.setText(R.string.example_email);
        photo.setImageResource(R.drawable.ic_person_black_24dp);
    }
}

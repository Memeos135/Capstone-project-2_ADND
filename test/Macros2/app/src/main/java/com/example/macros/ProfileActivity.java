package com.example.macros;

import android.app.ActivityOptions;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;


public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<ProfileRecordSetter> recordList;
    ProfileRecyclerAdapter profileRecyclerAdapter;

    int year;
    int month;
    int day;

    String calorie_maxs;
    String calorie_perc;

    String protein_maxs;
    String protein_cur;

    String carbs_maxs;
    String carbs_cur;

    String fat_maxs;
    String fat_cur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setupDate();

        if(savedInstanceState == null) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.VISIBLE);
                fetchUserBrief();
                setupProgressBars();
            }
        }
    }

    public void fetchUserBrief(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userCreds");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if(dataSnapshot1.getKey().equals("name")){

                            TextView name = findViewById(R.id.nav_name);
                            name.setText(dataSnapshot1.getValue().toString());

                            TextView uName = findViewById(R.id.user_name);
                            uName.setText(dataSnapshot1.getValue().toString());

                        }else if(dataSnapshot1.getKey().equals("profile_picture")){

                            ImageView photo = findViewById(R.id.nav_image);
                            Picasso.get().load(Uri.parse(dataSnapshot1.getValue().toString())).centerCrop().fit().into(photo);

                            ImageView imageView = findViewById(R.id.send_image);
                            Picasso.get().load(Uri.parse(dataSnapshot1.getValue().toString())).centerCrop().fit().into(imageView);

                        }else if(dataSnapshot1.getKey().equals("email")){

                            TextView email = findViewById(R.id.nav_email);
                            email.setText(dataSnapshot1.getValue().toString());

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setupProgressBars(){
        final ProgressBar progressProtein = findViewById(R.id.protein_progress);
        final ProgressBar progressCarbs = findViewById(R.id.carbs_progress);
        final ProgressBar progressFats = findViewById(R.id.fat_progress);

        final TextView proteinRemain = findViewById(R.id.protein_remain);
        final TextView carbsRemain = findViewById(R.id.carbs_remain);
        final TextView fatsRemain = findViewById(R.id.fat_remain);

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

                            carbs_maxs = dataSnapshot1.getValue().toString();

                        } else if (counter == 1) {

                            progressFats.setMax(Integer.parseInt(dataSnapshot1.getValue().toString()));
                            fatsRemain.setText(" / " +dataSnapshot1.getValue().toString());

                            fat_maxs = dataSnapshot1.getValue().toString();

                        } else {

                            progressProtein.setMax(Integer.parseInt(dataSnapshot1.getValue().toString()));
                            proteinRemain.setText(" / " +dataSnapshot1.getValue().toString());

                            protein_maxs = dataSnapshot1.getValue().toString();

                        }
                        counter++;
                    }

                    getCurrentProgress(progressCarbs.getMax(), progressFats.getMax(), progressProtein.getMax(), progressProtein, progressCarbs, progressFats);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getCurrentProgress(final int carbMax, final int fatMax, final int proteinMax, final ProgressBar protein, final ProgressBar carbs, final ProgressBar fat){
        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH)+1;
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        final TextView proteinText = findViewById(R.id.protein_current);
        final TextView carbsText = findViewById(R.id.carbs_current);
        final TextView fatText = findViewById(R.id.fat_current);

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

                                carbs_cur = dataSnapshot1.getValue().toString();

                            } else if (dataSnapshot1.getKey().equals("protein")) {

                                protein.setProgress(Integer.parseInt(dataSnapshot1.getValue().toString()));
                                proteinText.setText(dataSnapshot1.getValue().toString());

                                protein_cur = dataSnapshot1.getValue().toString();

                            } else if (dataSnapshot1.getKey().equals("fat")) {

                                fat.setProgress(Integer.parseInt(dataSnapshot1.getValue().toString()));
                                fatText.setText(dataSnapshot1.getValue().toString());

                                fat_cur = dataSnapshot1.getValue().toString();

                            }
                        }
                    }

                    calculateCalories(carbMax, fatMax, proteinMax, proteinText.getText().toString(), carbsText.getText().toString(), fatText.getText().toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void calculateCalories(int carbMax, int fatMax, int proteinMax, String currentProtein, String currentCarbs, String currentFat){
        int current_fours = (Integer.parseInt(currentProtein) + Integer.parseInt(currentCarbs))*4;
        int current_nines = Integer.parseInt(currentFat)*9;

        int current_total = current_fours + current_nines;

        int fours = (carbMax + proteinMax)*4;
        int nines = fatMax*9;

        int total = fours + nines;

        TextView cal = findViewById(R.id.calorie_value);
        cal.setText(String.valueOf(total));

        TextView cal_percent = findViewById(R.id.calorie_percent);

        double percent = ((double)current_total / (double)total)*100;

        cal_percent.setText((int) percent+"%");

        ProgressBar cal_progress = findViewById(R.id.progressBar);
        cal_progress.setProgress((int)percent);

        calorie_maxs = String.valueOf(total);
        calorie_perc = (int) percent+"%";

        ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.GONE);
    }

    public void setupDate(){
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);

        TextView date = findViewById(R.id.date);
        date.setText(day + "/" + month + "/" + year);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(new Intent(this, ProfileActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }

        } else if (id == R.id.signup) {
            // Testing default activity enter/exit animation
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                startActivity(new Intent(this, SignupActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(new Intent(this, SignupActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }

        } else if (id == R.id.signin) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(new Intent(this, LoginActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }

        } else if (id == R.id.pending) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(new Intent(this, PendingFriendsActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }

        } else if (id == R.id.signout) {

            FirebaseAuth.getInstance().signOut();
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);

            resetNav();

        } else if (id == R.id.home) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void backImageHandler(View view){
        onBackPressed();
    }

    public void radioHandler(View view) {
        RadioButton radioButton = (RadioButton) view;
//
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
//        // flag is used to know if a fragment is loading its data from firebase or not - false means loading is complete
            if (radioButton.getTag().equals("Overview")) {
                ConstraintLayout constraintLayout = findViewById(R.id.const_one);
                constraintLayout.setVisibility(View.GONE);

                ConstraintLayout constraintLayout1 = findViewById(R.id.const_two);
                constraintLayout1.setVisibility(View.VISIBLE);

                setupRecycler();

            } else {
                ConstraintLayout constraintLayout = findViewById(R.id.const_two);
                constraintLayout.setVisibility(View.GONE);

                ConstraintLayout constraintLayout1 = findViewById(R.id.const_one);
                constraintLayout1.setVisibility(View.VISIBLE);

                setupProgressBars();
            }
        }
    }

    public void setupRecycler(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        setupRecordList(recyclerView);
    }

    public void setupRecordList(RecyclerView recyclerView){
        recordList = new ArrayList<>();

        readRecordsFirebase(recordList, recyclerView);

    }

    public void readRecordsFirebase(final ArrayList<ProfileRecordSetter> recordList, final RecyclerView recyclerView){

        final int year = Calendar.getInstance().get(Calendar.YEAR);
        final int month = Calendar.getInstance().get(Calendar.MONTH)+1;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentMacrosProgress")
                .child(String.valueOf(year)).child(String.valueOf(month));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                        ProfileRecordSetter profileRecordSetter = dataSnapshot1.getValue(ProfileRecordSetter.class);
                        profileRecordSetter.setDate(dataSnapshot1.getKey() + "/" + month + "/" + year);
                        recordList.add(profileRecordSetter);

                    }

                    profileRecyclerAdapter = new ProfileRecyclerAdapter(getApplicationContext(), recordList);
                    recyclerView.setAdapter(profileRecyclerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            if(findViewById(R.id.const_two).getVisibility() == View.GONE){
                outState.putString("layout", "one");

                outState.putString("date", day+"/"+month+"/"+year);

                outState.putString("calorie_max", calorie_maxs);
                outState.putString("calorie_percent", calorie_perc);

                outState.putString("protein_max", protein_maxs);
                outState.putString("protein_current", protein_cur);

                outState.putString("carbs_max", carbs_maxs);
                outState.putString("carbs_current", carbs_cur);

                outState.putString("fat_max", fat_maxs);
                outState.putString("fat_current", fat_cur);

            }else{
                outState.putString("layout", "two");

                outState.putParcelableArrayList("recordList", recordList);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            if(savedInstanceState.getString("layout").equals("one")){

                calorie_perc = savedInstanceState.getString("calorie_percent");
                calorie_maxs = savedInstanceState.getString("calorie_max");

                protein_cur = savedInstanceState.getString("protein_current");
                protein_maxs = savedInstanceState.getString("protein_max");

                carbs_cur = savedInstanceState.getString("carbs_current");
                carbs_maxs = savedInstanceState.getString("carbs_max");

                fat_cur = savedInstanceState.getString("fat_current");
                fat_maxs = savedInstanceState.getString("fat_max");

                String date = savedInstanceState.getString("date");

                TextView calVal = findViewById(R.id.calorie_value);
                TextView calPerc = findViewById(R.id.calorie_percent);
                calVal.setText(calorie_maxs);
                calPerc.setText(calorie_perc);

                TextView protCur = findViewById(R.id.protein_current);
                TextView protRem = findViewById(R.id.protein_remain);
                protCur.setText(protein_cur);
                protRem.setText(" / " +protein_maxs);

                TextView carbCur = findViewById(R.id.carbs_current);
                TextView carbRem = findViewById(R.id.carbs_remain);
                carbCur.setText(carbs_cur);
                carbRem.setText(" / " +carbs_maxs);

                TextView fatCur = findViewById(R.id.fat_current);
                TextView fatRem = findViewById(R.id.fat_remain);
                fatCur.setText(fat_cur);
                fatRem.setText(" / " +fat_maxs);

                TextView dates = findViewById(R.id.date);
                dates.setText(date);

                ProgressBar protein = findViewById(R.id.protein_progress);
                double perc = ( (double) Integer.parseInt(protein_cur) / (double) Integer.parseInt(protein_maxs))*100;
                protein.setProgress((int) perc);

                ProgressBar carbs = findViewById(R.id.carbs_progress);
                double percC = ( (double) Integer.parseInt(carbs_cur) / (double) Integer.parseInt(carbs_maxs))*100;
                carbs.setProgress((int) percC);

                ProgressBar fat = findViewById(R.id.fat_progress);
                double percF = ( (double) Integer.parseInt(fat_cur) / (double) Integer.parseInt(fat_maxs))*100;
                fat.setProgress((int) percF);

            }else{

                ConstraintLayout constraintLayout = findViewById(R.id.const_one);
                constraintLayout.setVisibility(View.GONE);

                ConstraintLayout constraintLayout1 = findViewById(R.id.const_two);
                constraintLayout1.setVisibility(View.VISIBLE);

                RadioButton radioButton = findViewById(R.id.overviewRadio);
                radioButton.setChecked(true);

                recordList = savedInstanceState.getParcelableArrayList("recordList");
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                profileRecyclerAdapter = new ProfileRecyclerAdapter(getApplicationContext(), recordList);
                recyclerView.setAdapter(profileRecyclerAdapter);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.logged_drawer);

            fetchUserBrief();

        }else{
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);
        }
    }

    public void resetNav(){
        TextView name = findViewById(R.id.nav_name);
        TextView email = findViewById(R.id.nav_email);
        ImageView photo = findViewById(R.id.nav_image);

        name.setText(R.string.user_name);
        email.setText(R.string.example_email);
        photo.setImageResource(R.drawable.ic_person_black_24dp);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            triggerWidget();
            setupWidget();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setupWidget(){
        Intent intent = new Intent(this, MacrosWidget.class);
        intent.setAction("setBase");

        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), MacrosWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    public void triggerWidget(){
        Intent intent = new Intent(this, MacrosWidget.class);
        intent.setAction("update");

        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), MacrosWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }
}

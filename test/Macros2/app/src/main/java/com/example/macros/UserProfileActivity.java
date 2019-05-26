package com.example.macros;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    boolean baseFlag = false;
    String number = "";
    String counterBase = "";

    String imageURI;
    String username;

    String prot_perc;
    String carb_perc;
    String fat_perc;

    String userStatus = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null) {
            loadUserInfo();
            readFriendsCount();

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                readTodayMacros();
                removeGradient();
            }
        }
    }

    public void fetchNumber(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(getIntent().getStringExtra("uID"))
                .child("userCreds").child("mobileNumber");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    number = dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void readFriendsCount(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(getIntent().getStringExtra("uID"))
                .child("friends");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextView friend_count = findViewById(R.id.followers_val);
                int counter = 0;

                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        counter++;
                    }
                    friend_count.setText(String.valueOf(counter));
                    counterBase = String.valueOf(counter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeGradient(){
        ImageView imageView = findViewById(R.id.imageView3);
        ConstraintLayout constraintLayout = findViewById(R.id.lower_constraint);
        constraintLayout.removeView(imageView);
    }

    public void showAddFriend(){
        final CardView cardView = findViewById(R.id.friend_card);
        cardView.animate().alpha(1).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                cardView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void showFab(){
        final FloatingActionButton floatingActionButton = findViewById(R.id.chat_fab);
        floatingActionButton.animate().alpha(1).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onAnimationEnd(Animator animator) {
                floatingActionButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void readTodayMacros(){
        final ProgressBar protein = findViewById(R.id.protein_progress);
        final ProgressBar carbs = findViewById(R.id.carbs_progress);
        final ProgressBar fats = findViewById(R.id.fat_progress);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(getIntent().getStringExtra("uID"))
                .child("userGoalMacros");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if(dataSnapshot1.getKey().equals("carbs")){
                            carbs.setMax(Integer.parseInt(dataSnapshot1.getValue().toString()));
                        }else if(dataSnapshot1.getKey().equals("protein")){
                            protein.setMax(Integer.parseInt(dataSnapshot1.getValue().toString()));
                        }else if(dataSnapshot1.getKey().equals("fats")){
                            fats.setMax(Integer.parseInt(dataSnapshot1.getValue().toString()));
                        }
                    }
                    setCurrents(protein, carbs, fats);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setCurrents(final ProgressBar protein, final ProgressBar carbs, final ProgressBar fats){
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH)+1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(getIntent().getStringExtra("uID"))
                .child("currentMacrosProgress").child(String.valueOf(year))
                .child(String.valueOf(month)).child(String.valueOf(day));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if(dataSnapshot1.getKey().equals("protein")){
                            protein.setProgress(Integer.parseInt(dataSnapshot1.getValue().toString()));
                        }else if(dataSnapshot1.getKey().equals("carbs")){
                            carbs.setProgress(Integer.parseInt(dataSnapshot1.getValue().toString()));
                        }else if(dataSnapshot1.getKey().equals("fat")){
                            fats.setProgress(Integer.parseInt(dataSnapshot1.getValue().toString()));
                        }
                    }
                    setPercentages(protein, carbs, fats);
                }else{
                    checkIfUserIsSame();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setPercentages(ProgressBar protein, ProgressBar carbs, ProgressBar fats){
        TextView protein_percent = findViewById(R.id.protein_remain);
        TextView carbs_percent = findViewById(R.id.carbs_remain);
        TextView fat_percent = findViewById(R.id.fat_remain);

        double proteinVal = ((double)protein.getProgress() / (double)protein.getMax())*100;
        double carbsVal = ((double)carbs.getProgress() / (double)carbs.getMax())*100;
        double fatVal = ((double)fats.getProgress() / (double)fats.getMax())*100;

        protein_percent.setText((int)proteinVal+"%");
        carbs_percent.setText((int)carbsVal+"%");
        fat_percent.setText((int)fatVal+"%");

        prot_perc = String.valueOf((int) proteinVal);
        carb_perc = String.valueOf((int) carbsVal);
        fat_perc = String.valueOf((int) fatVal);

        checkIfUserIsSame();
    }

    public void checkIfUserIsSame(){
        // check if I am seeing myself
        if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(getIntent().getStringExtra("uID"))) {
            Log.i("XXX", "passed NOT SAME USER");
            checkIfUserIsFriend();
        }else{
            userStatus = "same user";
        }
    }

    public void checkRequests(){
        final boolean[] flag = {false};
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("friendRequests");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if(dataSnapshot1.getValue().toString().equals(getIntent().getStringExtra("uID"))){
                            flag[0] = true;
                            break;
                        }
                    }
                    if(flag[0]){
                        showSentRequest();
                        userStatus = "request sent";
                    }else{
                        Log.i("XXX", "passed not in REQUESTS");
                        checkPending();
                    }
                }else{
                    Log.i("XXX", "passed not in REQUESTS");
                    checkPending();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkPending(){
        final boolean[] pendingFlag = {false};
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("pendingInvites");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if(dataSnapshot1.getValue().toString().equals(getIntent().getStringExtra("uID"))){
                            pendingFlag[0] = true;
                            break;
                        }
                    }
                    if(pendingFlag[0]){
                        showPending();
                        userStatus = "pending";
                    }else{
                        Log.i("XXX", "passed not in PENDING");
                        showAddFriend();
                    }
                }else{
                    Log.i("XXX", "passed not in PENDING");
                    showAddFriend();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showPending(){
        final CardView cardView = findViewById(R.id.friend_card);
        cardView.animate().alpha(1).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                cardView.setVisibility(View.VISIBLE);
                Button button = findViewById(R.id.add_button);
                button.setText(R.string.pending);
                button.setBackgroundColor(button.getResources().getColor(R.color.grey));
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void showSentRequest(){
        final CardView cardView = findViewById(R.id.friend_card);
        cardView.animate().alpha(1).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                cardView.setVisibility(View.VISIBLE);
                Button button = findViewById(R.id.add_button);
                button.setText(R.string.sent);
                button.setBackgroundColor(button.getResources().getColor(R.color.grey));
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void addFriend(final Button button){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(getIntent().getStringExtra("uID"))
                .child("pendingInvites");

        databaseReference.push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    addToMyRequests(button);
                }
            }
        });
    }

    public void addToMyRequests(final Button button){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("friendRequests");

        databaseReference.push().setValue(getIntent().getStringExtra("uID")).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    button.setText(R.string.sent);
                    button.setBackgroundColor(button.getResources().getColor(R.color.grey));
                }
            }
        });
    }

    public void checkIfUserIsFriend(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("friends");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if(dataSnapshot1.getValue().toString().equals(getIntent().getStringExtra("uID"))){
                            baseFlag = true;
                            break;
                        }
                    }
                    if(!baseFlag){
                        Log.i("XXX", "passed NOT FRIENDS");
                        checkRequests();
                    }else{
                        userStatus = "friend";
                        fetchNumber();
                        showFab();
                    }
                }else{
                    Log.i("XXX", "passed NOT FRIENDS");
                    checkRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadUserInfo(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(getIntent().getStringExtra("uID"))
                .child("userCreds");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if(dataSnapshot1.getKey().equals("profile_picture")) {
                            ImageView photo = findViewById(R.id.send_image);
                            imageURI = dataSnapshot1.getValue().toString();
                            Picasso.get().load(dataSnapshot1.getValue().toString()).centerCrop().fit().into(photo);
                        }else if(dataSnapshot1.getKey().equals("name")){
                            TextView name = findViewById(R.id.user_name);
                            username = dataSnapshot1.getValue().toString();
                            name.setText(dataSnapshot1.getValue().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    public void handleButton(View view){
        Button button = (Button) view;
        addFriend(button);
    }

    public void backImageHandler(View view){
        onBackPressed();
    }

    public void chatFabHandler(View view){
        // launch whatsapp for a specific number based on the selected user - if user does not exist in contact list, invite through SMS
        Uri uri = Uri.parse("smsto: " + number);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(i);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("protein_percent", prot_perc);
        outState.putString("carbs_percent", carb_perc);
        outState.putString("fat_percent", fat_perc);
        outState.putString("mobile_number", number);
        outState.putString("counter", counterBase);
        outState.putString("name", username);
        outState.putString("photo", imageURI);

        outState.putString("status", userStatus);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        userStatus = savedInstanceState.getString("status");

        username = savedInstanceState.getString("name");
        imageURI = savedInstanceState.getString("photo");

        number = savedInstanceState.getString("mobile_number");
        counterBase = savedInstanceState.getString("counter");

        prot_perc = savedInstanceState.getString("protein_percent");
        carb_perc = savedInstanceState.getString("carbs_percent");
        fat_perc = savedInstanceState.getString("fat_percent");

        if(userStatus.equals("pending")){
            showPending();
        }else if(userStatus.equals("request sent")){
            showSentRequest();
        }else if(userStatus.equals("friend")){
            showFab();
        }else if(userStatus.equals("same user")){
            // do nothing
        }else{
            showAddFriend();
        }

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            removeGradient();
        }

        TextView name = findViewById(R.id.user_name);
        ImageView photo = findViewById(R.id.send_image);
        TextView friends = findViewById(R.id.followers_val);

        TextView proteinRemain = findViewById(R.id.protein_remain);
        TextView carbsRemain = findViewById(R.id.carbs_remain);
        TextView fatRemain = findViewById(R.id.fat_remain);

        name.setText(username);
        friends.setText(counterBase);
        Picasso.get().load(Uri.parse(imageURI)).centerCrop().fit().into(photo);

        proteinRemain.setText(prot_perc+"%");
        carbsRemain.setText(carb_perc+"%");
        fatRemain.setText(fat_perc+"%");
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

    public void resetNav(){
        TextView name = findViewById(R.id.user_name);
        TextView email = findViewById(R.id.user_email);
        ImageView photo = findViewById(R.id.send_image);

        name.setText(R.string.user_name);
        email.setText(R.string.example_email);
        photo.setImageResource(R.drawable.ic_person_black_24dp);
    }
}

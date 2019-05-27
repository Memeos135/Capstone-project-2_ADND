package com.example.macros;

import android.app.ActivityOptions;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PendingFriendsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Context context;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.VISIBLE);
            setupRecycler();
        }
    }

    public void setupRecycler(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        setupRecyclerData(recyclerView);
    }

    public void setupRecyclerData(final RecyclerView recyclerView){
        final ArrayList<String> pendingList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("pendingInvites");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        pendingList.add(dataSnapshot1.getValue().toString());
                    }
                    readUserRequestInfo(pendingList, recyclerView);
                }else{
                    ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });
    }

    public void readUserRequestInfo(final ArrayList<String> pendingList, final RecyclerView recyclerView){
        final ArrayList<PendingInfo> infoList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        for(int i = 0; i < pendingList.size(); i++){
            final int finalI = i;
            databaseReference.child(pendingList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.getKey().equals("userCreds")) {
                                String name = dataSnapshot1.child("name").getValue().toString();
                                String email = dataSnapshot1.child("email").getValue().toString();
                                String imageURL = dataSnapshot1.child("profile_picture").getValue().toString();

                                PendingInfo pendingInfo = new PendingInfo(name, email, imageURL, pendingList.get(finalI));
                                infoList.add(pendingInfo);
                            }
                        }
                        // init adapter
                        PendingAdapter pendingAdapter = new PendingAdapter(context, infoList);
                        recyclerView.setAdapter(pendingAdapter);
                        ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.GONE);
                    }else{
                        ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled( DatabaseError databaseError) {

                }
            });
        }
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

    public void resetNav(){
        TextView name = findViewById(R.id.nav_name);
        TextView email = findViewById(R.id.nav_email);
        ImageView photo = findViewById(R.id.nav_image);

        name.setText(R.string.user_name);
        email.setText(R.string.example_email);
        photo.setImageResource(R.drawable.ic_person_black_24dp);
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

    public void backImageHandler(View view){
        onBackPressed();
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

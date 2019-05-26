package com.example.macros;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    ProfileFragment profileFragment;
    ProfileFragmentTwo profileFragmentTwo;
    public static boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (savedInstanceState == null) {

            profileFragment = new ProfileFragment();
            profileFragmentTwo = new ProfileFragmentTwo();

            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.fragment_container, profileFragment).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // check if user is logged in
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.logged_drawer);

            setupProfileImage();
        }
    }

    public void setupProfileImage(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userCreds");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if(dataSnapshot1.getKey().equals("name")){

                            TextView name = findViewById(R.id.user_name);
                            name.setText(dataSnapshot1.getValue().toString());

                        }else if(dataSnapshot1.getKey().equals("profile_picture")){

                            ImageView photo = findViewById(R.id.send_image);
                            Picasso.get().load(Uri.parse(dataSnapshot1.getValue().toString())).centerCrop().fit().into(photo);

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

    public void backImageHandler(View view){
        onBackPressed();
    }

    public void radioHandler(View view) {
        RadioButton radioButton = (RadioButton) view;

        // flag is used to know if a fragment is loading its data from firebase or not - false means loading is complete
        if(!flag) {
            if (radioButton.getTag().equals("Overview")) {

                // first check if current fragment is NOT Overview
                if (getSupportFragmentManager().getFragments().get(0).toString().startsWith("ProfileFragment")) {

                    Toast.makeText(this, "ProfileFragment", Toast.LENGTH_SHORT).show();

                    flag = true;

                    fragmentManager.beginTransaction().remove(profileFragment).commit();
                    // build ProfileFragmentTwo
                    // this line is to handle retard fast swap clicks from overview to target
                    if(!fragmentManager.getFragments().contains(profileFragmentTwo)) {
                        fragmentManager.beginTransaction().add(R.id.fragment_container, profileFragmentTwo).commit();
                    }else{
                        fragmentManager.popBackStack();
                        flag = false;
                    }
                }

            } else {

                // first check if current fragment is NOT Target
                if (getSupportFragmentManager().getFragments().get(0).toString().startsWith("ProfileFragmentTwo")) {

                    Toast.makeText(this, "ProfileFragmentTwo", Toast.LENGTH_SHORT).show();

                    flag = true;

                    fragmentManager.beginTransaction().remove(profileFragmentTwo).commit();
                    // build ProfileFragment
                    // this line is to handle retard fast swap clicks from overview to target
                    if(!fragmentManager.getFragments().contains(profileFragment)) {
                        fragmentManager.beginTransaction().add(R.id.fragment_container, profileFragment).commit();
                    }else{
                        fragmentManager.popBackStack();
                        flag = false;
                    }

                }
            }
        }else{
            flag = false;
        }
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

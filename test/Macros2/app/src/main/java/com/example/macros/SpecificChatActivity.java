package com.example.macros;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SpecificChatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specific_chat_activity);
        context = this;
        setupRecycler();
        setupMessageListener();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.logged_drawer);
        }
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

    public void setupMessageListener(){
        TextView textView = findViewById(R.id.msg_text);

        textView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    Toast.makeText(context, "sendMessage", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

    public void sendMessage(View view){
        Toast.makeText(context, "sendMessage", Toast.LENGTH_SHORT).show();
    }

    public void backImageHandler(View view){
        onBackPressed();
    }

    public void setupRecycler(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        setupRecyclerAdapter(recyclerView);
    }

    public void setupRecyclerAdapter(RecyclerView recyclerView){
        ArrayList<FriendMessage> msgList = new ArrayList<>();

        msgList.add(new FriendMessage("Hello My Name is Mohammed Bokhari", "11:59PM", "self"));
        msgList.add(new FriendMessage("Hello My Name is Mohammed Bokhari", "11:59PM", "notself"));
        msgList.add(new FriendMessage("Hello My Name is Mohammed Bokhari", "11:59PM", "self"));
        msgList.add(new FriendMessage("Hello My Name is Mohammed Bokhari", "11:59PM", "notself"));
        msgList.add(new FriendMessage("Hello My Name is Mohammed Bokhari", "11:59PM", "notself"));
        msgList.add(new FriendMessage("Hello My Name is Mohammed Bokhari", "11:59PM", "notself"));
        msgList.add(new FriendMessage("Hello My Name is Mohammed Bokhari", "11:59PM", "self"));
        msgList.add(new FriendMessage("Hello My Name is Mohammed Bokhari", "11:59PM", "notself"));

        MessageRecyclerAdapter messageRecyclerAdapter = new MessageRecyclerAdapter(context, msgList);
        recyclerView.setAdapter(messageRecyclerAdapter);
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

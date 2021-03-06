package com.example.macros;

import android.Manifest;
import android.app.ActivityOptions;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

public class SignupActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String email;
    private String fName;
    private String password;
    private String confPassword;
    private String protein;
    private String carbs;
    private String fats;
    private String mobileNumber;
    private Uri selectedImage;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity_root);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        imageListener();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.logged_drawer);
        }

        checkPermission();
    }

    public void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
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

        } else if (id == R.id.home) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void handleGetStarted(View view){
        // Transition to step 2 of registration.
        runRegex();
    }

    public void runRegex(){
        EditText firstName = findViewById(R.id.name_input);
        EditText email = findViewById(R.id.email_input);
        EditText password = findViewById(R.id.password_input);
        EditText passConf = findViewById(R.id.confpass_input);
        EditText mobileNumber = findViewById(R.id.phone_number);

        if(firstName.getText().toString().equals("") || email.getText().toString().equals("") || passConf.getText().toString().equals("") || password.getText().toString().equals("")
                || mobileNumber.getText().toString().equals("")){
            Toast.makeText(this, "Please fill all empty fields", Toast.LENGTH_SHORT).show();
        }else{
            if(!checkContainsDigit(firstName.getText().toString())){
                if(selectedImage == null){
                    Toast.makeText(this, "Please provide a profile picture", Toast.LENGTH_SHORT).show();
                }else {
                    if(!passConf.getText().toString().equals(password.getText().toString())){
                        Toast.makeText(this, "Both passwords must match", Toast.LENGTH_SHORT).show();
                    }else {
                        if(mobileNumber.getText().toString().startsWith("0")){
                            Toast.makeText(this, "Please leave out the zero prefix - e.g 547171060 instead of 0547171060", Toast.LENGTH_SHORT).show();
                        }else if(mobileNumber.getText().toString().length() > 9 || mobileNumber.getText().toString().length() < 9) {
                            Toast.makeText(this, "Mobile numbers cannot exceed or be less than 9 digits, excluding the zero", Toast.LENGTH_SHORT).show();
                        }else{
                            fName = firstName.getText().toString();
                            this.email = email.getText().toString();
                            this.password = password.getText().toString();
                            confPassword = passConf.getText().toString();
                            this.mobileNumber = mobileNumber.getText().toString();

                            TransitionManager.go(Scene.getSceneForLayout((ViewGroup) findViewById(R.id.scene_root),
                                    R.layout.signup_activity_root_two,
                                    this));
                        }
                    }
                }
            }else{
                Toast.makeText(this, "Name can only contain letters", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean checkContainsDigit(String firstName){
        return firstName.matches(".*[0-9].*");
    }

    public void handleAlreadyMember(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void backImage(View view){
        if(findViewById(R.id.stage_two_title) != null){
            // Transition back to step 1 of registration.
            TransitionManager.go(Scene.getSceneForLayout((ViewGroup) findViewById(R.id.scene_root_two),
                    R.layout.signup_activity_root,
                    this));
        }else{
            onBackPressed();
        }
    }

    public void handleRegistration(View view){
        TextView protein = findViewById(R.id.protein_input);
        TextView carbs = findViewById(R.id.carbs_input);
        TextView fats = findViewById(R.id.fat_input);

        if(protein.getText().toString().equals("") || carbs.getText().toString().equals("") || fats.getText().toString().equals("")){
            Toast.makeText(this, "Please fill all empty fields", Toast.LENGTH_SHORT).show();
        }else{
            this.protein = protein.getText().toString();
            this.carbs = carbs.getText().toString();
            this.fats = fats.getText().toString();

            // do firebase
            buildUserRecord();
            ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.VISIBLE);
        }
    }

    public void buildUserRecord(){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    // on successful auth creation, add image to storage using uID
                    addImageStorage(firebaseUser.getUid());
                }else{
                    Toast.makeText(SignupActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.GONE);
                }
            }
        });
    }

    public void addImageStorage(final String uID){
        final StorageReference storageReferences = storageReference.child(uID+"/profile_picture");
        UploadTask uploadTask = storageReferences.putFile(selectedImage);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReferences.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            // create a user record in the database
                            storeUserRecord(uID, downloadUri);
                        }else{
                            ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    public void storeUserRecord(final String uID, final Uri downloadUri){
        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("users").child(uID);

        tempRef.setValue(new UserProfileRecord(new UserCreds(email, password, downloadUri.toString(), fName, mobileNumber), new UserGoalMacros(protein, carbs, fats)))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("search");

                databaseReference.push().setValue(new SearchNode(uID, fName)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.GONE);
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class)
                                .putExtra("username", email)
                                .putExtra("password", password));
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.GONE);
            }
        });
    }

    public void imageListener(){
        ImageView image = findViewById(R.id.send_image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open gallery
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && data != null){
            // Load image
            if(data.getDataString().contains("external")){
                String path = getPath(data.getDataString());
                File file = new File(path);
                selectedImage = Uri.fromFile(file);
                ((ImageView) findViewById(R.id.send_image)).setImageResource(R.drawable.ic_check_red_24dp);
                ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.GONE);
            }else{
                selectedImage = data.getData();
                Picasso.get().load(selectedImage).noPlaceholder().centerCrop().fit().into((ImageView) findViewById(R.id.send_image), new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        ((RelativeLayout) findViewById(R.id.loadingPanel)).setVisibility(View.GONE);
                    }


                });
            }
        }
    }

    public String getPath(String data){
        Cursor c = getContentResolver().query(
                Uri.parse(data),null,null,null,null);
        c.moveToNext();
        String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
        c.close();
        return path;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(findViewById(R.id.stage_two_title) != null) {
            outState.putString("stage", "two");
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState.getString("stage") != null){
            setContentView(R.layout.signup_activity_root_two);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if(findViewById(R.id.stage_two_title) != null){
            TransitionManager.go(Scene.getSceneForLayout((ViewGroup) findViewById(R.id.scene_root_two),
                    R.layout.signup_activity_root,
                    this));

            imageListener();
            TextView name = findViewById(R.id.name_input);
            TextView email = findViewById(R.id.email_input);
            TextView password = findViewById(R.id.password_input);
            TextView passwordConf = findViewById(R.id.confpass_input);
            TextView number = findViewById(R.id.phone_number);

            name.setText(fName);
            email.setText(this.email);
            password.setText(this.password);
            passwordConf.setText(confPassword);
            number.setText(mobileNumber);

        }else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
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
        }else{
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            triggerWidget();
            setupWidget();
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

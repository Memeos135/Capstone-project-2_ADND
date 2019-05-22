package com.example.macros;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import android.widget.ImageView;
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

public class SignupActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String email;
    private String fName;
    private String password;
    private String confPassword;
    private String protein;
    private String carbs;
    private String fats;
    private Uri selectedImage;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity_root);
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
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

        } else if (id == R.id.home) {

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
        TextView firstName = findViewById(R.id.name_input);
        TextView email = findViewById(R.id.email_input);
        TextView password = findViewById(R.id.password_input);
        TextView passConf = findViewById(R.id.confpass_input);

        if(firstName.getText().toString().equals("") || email.getText().toString().equals("") || passConf.getText().toString().equals("") || password.getText().toString().equals("")){
            Toast.makeText(this, "Please fill all empty fields", Toast.LENGTH_SHORT).show();
        }else{
            if(!checkContainsDigit(firstName.getText().toString())){
                if(selectedImage == null){
                    Toast.makeText(this, "Please provide a profile picture", Toast.LENGTH_SHORT).show();
                }else {
                    if(!passConf.getText().toString().equals(password.getText().toString())){
                        Toast.makeText(this, "Both passwords must match", Toast.LENGTH_SHORT).show();
                    }else {
                        fName = firstName.getText().toString();
                        this.email = email.getText().toString();
                        this.password = password.getText().toString();
                        confPassword = passConf.getText().toString();

                        TransitionManager.go(Scene.getSceneForLayout((ViewGroup) findViewById(R.id.scene_root),
                                R.layout.signup_activity_root_two,
                                this));
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
                        }
                    }
                });
            }
        });
    }

    public void storeUserRecord(String uID, final Uri downloadUri){
        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("users").child(uID);

        tempRef.setValue(new UserProfileRecord(new UserCreds(email, password, downloadUri.toString(), fName), new UserGoalMacros(protein, carbs, fats))).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class)
                .putExtra("username", email)
                .putExtra("password", password));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void profileImageHandler(View view){
        // Open gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && data != null){
            // Load image
            selectedImage = data.getData();
            Picasso.get().load(selectedImage).noPlaceholder().centerCrop().fit().into((ImageView) findViewById(R.id.send_image));
        }
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
        }else {
            super.onBackPressed();
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
}

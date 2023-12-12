package com.example.pokeapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.bumptech.glide.Glide;


public class UserProfileActivity extends AppCompatActivity {

    TextView textViewWelcome, textViewFullName, textViewEmail, TextViewDoB, textViewMobile;

    ProgressBar pBar;

    String fullName, email, doB, mobile;
    ImageView  imageViewSetting, imageView_profilePicture;

    FirebaseAuth authProfile;

    SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        
        swipeToRefresh();

        textViewWelcome= findViewById(R.id.textView_show_welcome);
        textViewFullName = findViewById(R.id.textView_show_full_name);
        textViewEmail = findViewById(R.id.textView_show_email);
        TextViewDoB = findViewById(R.id.textView_show_dob);
        textViewMobile = findViewById(R.id.textView_show_mobile);
        pBar = findViewById(R.id.progress_bar);
        imageViewSetting = findViewById(R.id.imageViewSetting);

        imageView_profilePicture = findViewById(R.id.imageView_profilePicture);

        imageView_profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfileActivity.this,UploadProfilePicture.class));
            }
        });

        imageViewSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfileActivity.this, SettingActivity.class));
            }
        });

        authProfile =   FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(UserProfileActivity.this, "Something went wrong. User's details are not available at the moment!", Toast.LENGTH_LONG).show();
        }else{
            checkIfEmailVerified(firebaseUser);
            pBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);

            loadProfilePicture(firebaseUser);

        }

    }

    private void swipeToRefresh() {
        swipeContainer = findViewById(R.id.swipeContainerSwiper);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startActivity(getIntent());
                finish();
                overridePendingTransition(0, 0);
                swipeContainer.setRefreshing(false);
            }
        });


    }


    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can not login without email verification next time.");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registred User");
        Log.d("Ref ",referenceProfile.toString());
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("UserProfileActivity", "onDataChange Triggered");

                if (snapshot.exists()) {
                    Log.d("UserProfileActivity", "Snapshot Exists");
                    ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                    if (readUserDetails != null) {
                        Log.d("UserProfileActivity", "ReadUserDetails is NOT null");

                        fullName = firebaseUser.getDisplayName();
                        doB = readUserDetails.getDob();
                        email = firebaseUser.getEmail();
                        mobile = readUserDetails.getMobile();

                        textViewWelcome.setText("Welcome " + fullName);
                        textViewFullName.setText(fullName);
                        textViewEmail.setText(email);
                        textViewMobile.setText(mobile);
                        TextViewDoB.setText(doB);

                        //Uri uri = firebaseUser.getPhotoUrl();


                    } else {
                        Log.d("UserProfileActivity", "ReadUserDetails is NULL");
                        Toast.makeText(UserProfileActivity.this, "null user Details", Toast.LENGTH_LONG).show();
                    }
                    pBar.setVisibility(View.GONE);
                } else {
                    Log.d("UserProfileActivity", "Snapshot Does NOT Exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserProfileActivity", "onCancelled: " + error.getMessage());
                Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                pBar.setVisibility(View.GONE);
            }

        });
    }

    private void loadProfilePicture(FirebaseUser firebaseUser) {
        // Get the storage reference for the profile picture
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("DisplayPics");
        StorageReference fileRef = storageRef.child(firebaseUser.getUid() + ".jpg");

        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Download successful, load the image using the provided URL
            Glide.with(this) // You can use any image loading library or your preferred method
                    .load(uri)
                    .into(imageView_profilePicture);
        }).addOnFailureListener(e -> {
            // Handle failure to download the image
            Toast.makeText(UserProfileActivity.this, "Failed to download profile picture.", Toast.LENGTH_SHORT).show();
            Log.e("UserProfileActivity", "Failed to download profile picture", e);
        });
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
            //return true;
        }


        return super.onOptionsItemSelected(item);
    }


     */

}
package com.example.pokeapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadProfilePicture extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;


    Button buttonPickPicture, buttonUpload;
    ImageView imageView_profilePicture;

    FirebaseAuth authProfile;

    StorageReference storageReference;

    FirebaseUser firebaseUser;

    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_picture);

        buttonPickPicture = findViewById(R.id.buttonPickPicture);
        buttonUpload = findViewById(R.id.buttonUpload);
        imageView_profilePicture = findViewById(R.id.imageView_profilePicture);

        authProfile = FirebaseAuth.getInstance();

        firebaseUser = authProfile.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

        // Load default image if the user doesn't have a custom profile picture
        if (firebaseUser.getPhotoUrl() == null) {
            // Replace "default_image_url" with the URL of your default image in Firebase Storage
            /*
            Glide.with(this)
                    .load("default_image_url")
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .into(imageView_profilePicture);

             */
        } else {
            // Load the user's existing profile picture
            Glide.with(this)
                    .load(firebaseUser.getPhotoUrl())

                    .into(imageView_profilePicture);
        }


        buttonPickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });


        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadPick();
            }
        });




    }


    private void UploadPick() {
        if (selectedImageUri != null) {
            Toast.makeText(UploadProfilePicture.this, "Successfully picked.", Toast.LENGTH_LONG).show();

            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid() + "."
                    + getFileExtension(selectedImageUri));

            fileReference.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUri = uri;
                                    firebaseUser = authProfile.getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(downloadUri).build();
                                    firebaseUser.updateProfile(profileUpdates)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Image uploaded and user profile updated successfully
                                                    Toast.makeText(UploadProfilePicture.this,
                                                            "Profile picture updated successfully.",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(UploadProfilePicture.this, UserProfileActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle failure to update user profile
                                                Toast.makeText(UploadProfilePicture.this,
                                                        "Failed to update user profile.",
                                                        Toast.LENGTH_SHORT).show();
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to upload image
                        Toast.makeText(UploadProfilePicture.this,
                                "Failed to upload image.",
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private String getFileExtension(Uri selectedUri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(selectedUri));
    }

    private void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent , PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageView_profilePicture.setImageURI(selectedImageUri);
        }
    }
}
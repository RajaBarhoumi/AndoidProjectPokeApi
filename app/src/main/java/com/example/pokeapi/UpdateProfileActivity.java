package com.example.pokeapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfileActivity extends AppCompatActivity {

    EditText editTextUpdateName, editTextUpdateDOB, editTextUpdatePhone;

    String textFullName, textDOB, textMobile;
    FirebaseAuth authProfile;

    Button buttonUpdateProf;

    DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        editTextUpdateName = findViewById(R.id.editTextAuthPwd);
        editTextUpdateDOB = findViewById(R.id.editTextUpdateDOB);
        editTextUpdatePhone = findViewById(R.id.editTextUpdatePhone);
        buttonUpdateProf = findViewById(R.id.buttonAuth);
        
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        
        showProfile(firebaseUser);

        editTextUpdateDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textDateOfBirth [] = textDOB.split("/");

                int day = Integer.parseInt(textDateOfBirth[0]);
                int month = Integer.parseInt(textDateOfBirth[1]) - 1;
                int year = Integer.parseInt(textDateOfBirth[2]);

                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextUpdateDOB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                }, year,month,day);
                picker.show();
            }
        });

        buttonUpdateProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(UpdateProfileActivity.this, "Please enter your fullname", Toast.LENGTH_LONG).show();
                    editTextUpdateName.setError("Full name is required");
                    editTextUpdateName.requestFocus();
                }else if(TextUtils.isEmpty(textDOB)){
                    Toast.makeText(UpdateProfileActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                    editTextUpdateDOB.setError("Date of birth is required");
                    editTextUpdateDOB.requestFocus();
                }else if(TextUtils.isEmpty(textMobile)){
                    Toast.makeText(UpdateProfileActivity.this, "Please enter your phone number", Toast.LENGTH_LONG).show();
                    editTextUpdatePhone.setError("Phone number is required");
                    editTextUpdatePhone.requestFocus();
                }else if(textMobile.length() != 8){
                    Toast.makeText(UpdateProfileActivity.this, "Please enter a valid phone number(8 digits)", Toast.LENGTH_LONG).show();
                    editTextUpdatePhone.setError("Password too weak");
                    editTextUpdatePhone.requestFocus();
                }else {
                    //registerUser(textFullName, textEmail,textGender, textDob,  textMobile, textPwd);
                    textFullName = editTextUpdateName.getText().toString();
                    textDOB = editTextUpdateDOB.getText().toString();
                    textMobile = editTextUpdatePhone.getText().toString();

                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDOB, textMobile);
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registred User");
                    String userUID = firebaseUser.getUid();
                    referenceProfile.child(userUID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                                firebaseUser.updateProfile(profileUpdates);
                                Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                try{
                                    throw task.getException();
                                }catch(Exception e){
                                    Log.d("Update Profile ", e.getMessage());
                                }
                            }
                        }
                    });

                }
            }
        });
        
    }

    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registred User");

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    textFullName = firebaseUser.getDisplayName();
                    textDOB = readUserDetails.getDob();
                    textMobile = readUserDetails.getMobile();

                    editTextUpdateDOB.setText(textDOB);
                    editTextUpdateName.setText(textFullName);
                    editTextUpdatePhone.setText(textMobile);
                }else{
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong. User's details are not available at the moment!", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
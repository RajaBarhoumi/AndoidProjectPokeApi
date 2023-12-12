package com.example.pokeapi;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    EditText editText_register_full_name, editText_register_email, editText_register_dob,
            editText_register_mobile, editText_register_password;


    Button button_register, login;
    ImageView imageView_show_hide_pwd;

    DatePickerDialog picker;

    static final String TAG="Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //getSupportActionBar().setTitle("Register");

        Toast.makeText(RegisterActivity.this, "You can register now", Toast.LENGTH_LONG).show();
        editText_register_full_name = findViewById(R.id.editText_register_full_name);
        editText_register_email = findViewById(R.id.editText_register_email);
        editText_register_dob = findViewById(R.id.editText_register_dob);
        editText_register_mobile = findViewById(R.id.editText_register_mobile);
        editText_register_password = findViewById(R.id.editText_register_password);


        button_register = findViewById(R.id.button_register);
        login = findViewById(R.id.login);
        imageView_show_hide_pwd = findViewById(R.id.imageView_show_hide_pwdLogin);
        imageView_show_hide_pwd.setImageResource(R.drawable.ic_hide_pwd);

        imageView_show_hide_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText_register_password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editText_register_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageView_show_hide_pwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editText_register_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageView_show_hide_pwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        editText_register_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editText_register_dob.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                }, year,month,day);
                picker.show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textFullName = editText_register_full_name.getText().toString();
                String textEmail = editText_register_email.getText().toString();
                String textDob = editText_register_dob.getText().toString();
                String textMobile = editText_register_mobile.getText().toString();
                String textPwd = editText_register_password.getText().toString();
                String textGender = "male";
                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(RegisterActivity.this, "Please enter your fullname", Toast.LENGTH_LONG).show();
                    editText_register_full_name.setError("Full name is required");
                    editText_register_full_name.requestFocus();
                }else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    editText_register_email.setError("Email is required");
                    editText_register_email.requestFocus();
                }else if(TextUtils.isEmpty(textDob)){
                    Toast.makeText(RegisterActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                    editText_register_dob.setError("Date of birth is required");
                    editText_register_dob.requestFocus();
                }else if(TextUtils.isEmpty(textMobile)){
                    Toast.makeText(RegisterActivity.this, "Please enter your phone number", Toast.LENGTH_LONG).show();
                    editText_register_mobile.setError("Phone number is required");
                    editText_register_mobile.requestFocus();
                }else if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    editText_register_password.setError("Password is required");
                    editText_register_password.requestFocus();
                }else if(textPwd.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                    editText_register_password.setError("Password too weak");
                    editText_register_password.requestFocus();
                }else if(textMobile.length() != 8){
                    Toast.makeText(RegisterActivity.this, "Please enter a valid phone number(8 digits)", Toast.LENGTH_LONG).show();
                    editText_register_mobile.setError("Password too weak");
                    editText_register_mobile.requestFocus();
                }else {
                    registerUser(textFullName, textEmail,textGender, textDob,  textMobile, textPwd);
                }


            }
        });

    }

    private void registerUser(String textFullName, String textEmail,String textGender, String textDob, String textMobile, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                            firebaseUser.updateProfile(profileChangeRequest);
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails( textDob, textMobile);

                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registred User");
                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        firebaseUser.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this, "User registered successfully.", Toast.LENGTH_LONG).show();
                                        //progressBar.setVisibility(View.VISIBLE);



                                        Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();


                                    }else{
                                        Toast.makeText(RegisterActivity.this, "User registered failed. Please try again.", Toast.LENGTH_LONG).show();
                                    }
                                    //progressBar.setVisibility(View.GONE);

                                }
                            });

                            if (firebaseUser != null) {
                                // Send email verification
                                firebaseUser.sendEmailVerification();

                                // Log success
                                Log.d("Registration", "User registration successful");

                                // Uncomment the following code if you want to navigate to UserProfile activity

                                Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                                // Log an error if the FirebaseUser is null
                                Log.e("Registration", "FirebaseUser is null");
                            }
                        } else {
                            try{
                                throw  task.getException();
                            }catch(FirebaseAuthWeakPasswordException e){
                                editText_register_password.setError("Your password is too weak. Kindly use a mix of alphabets, numbers and special characters!");
                                editText_register_password.requestFocus();
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                editText_register_email.setError("Your email is invalid or  already in use.");
                                editText_register_email.requestFocus();
                            }catch(FirebaseAuthUserCollisionException e){
                                editText_register_email.setError("User is already registred with this email. Use another email");
                                editText_register_email.requestFocus();
                            }catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(RegisterActivity.this,e.getMessage(), Toast.LENGTH_LONG ).show();
                            }
                            //progressBar.setVisibility(View.GONE);
                            // Log an error if the registration is not successful
                            Log.e("Registration", "User registration failed", task.getException());
                        }
                    }
                });
    }
}
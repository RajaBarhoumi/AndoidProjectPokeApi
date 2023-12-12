package com.example.pokeapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText editTextTextLoginPassword, editTextTextLoginEmail;

    ProgressBar progressBarLogin;

    FirebaseAuth authProfile;

    Button buttonLogin, button_forget_password;

    ImageView imageView_show_hide_pwdLogin;

    static final String TAG="Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextTextLoginPassword = findViewById(R.id.editTextTextLoginPassword);
        editTextTextLoginEmail = findViewById(R.id.editTextTextLoginEmail);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBarLogin = findViewById(R.id.progressBarLogin);
        imageView_show_hide_pwdLogin = findViewById(R.id.imageView_show_hide_pwdLogin);

        imageView_show_hide_pwdLogin.setImageResource(R.drawable.ic_hide_pwd);
        button_forget_password = findViewById(R.id.button_forget_password);

        button_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "You can reset your password now", Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });

        imageView_show_hide_pwdLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextTextLoginPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextTextLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageView_show_hide_pwdLogin.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextTextLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageView_show_hide_pwdLogin.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        authProfile = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editTextTextLoginEmail.getText().toString();
                String pwd = editTextTextLoginPassword.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    editTextTextLoginEmail.setError("Email is required");
                    editTextTextLoginEmail.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(LoginActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextTextLoginEmail.setError("Valid email is required");
                    editTextTextLoginEmail.requestFocus();
                }else if (TextUtils.isEmpty(pwd)){
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    editTextTextLoginPassword.setError("Password is required");
                    editTextTextLoginPassword.requestFocus();
                }else{
                    progressBarLogin.setVisibility(View.VISIBLE);
                    loginUser(textEmail,pwd);
                }


            }

            private void loginUser(String email, String password) {
                authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Toast.makeText(LoginActivity.this, "You are logged in now", Toast.LENGTH_LONG).show();

                            //Get instance of the current User
                            FirebaseUser firebaseUser = authProfile.getCurrentUser();
                            //Check if email is verified before user can access their profile
                            if(firebaseUser.isEmailVerified()){
                                Toast.makeText(LoginActivity.this, "You are logged in now", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                                finish();
                            }else{
                                firebaseUser.sendEmailVerification();
                                authProfile.signOut();
                                showAlertDialog();
                            }
                        }else{
                            try{
                                throw task.getException();
                            }catch(FirebaseAuthInvalidUserException e){
                                editTextTextLoginEmail.setError("User does not exists or is no longer valid. Please register again");
                                editTextTextLoginEmail.requestFocus();
                            }catch(FirebaseAuthInvalidCredentialsException e){
                                editTextTextLoginEmail.setError("Invalid credentials. Kindly, check and re-enter.");
                                editTextTextLoginEmail.requestFocus();
                            }catch (Exception e){
                                //Log.e(TAG, e.getMessage());
                                //Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                        progressBarLogin.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can not login without email verification");

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

    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser() != null){
            Toast.makeText(LoginActivity.this, "Already logged in", Toast.LENGTH_LONG).show();
            startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
            finish();
        }else{
            Toast.makeText(LoginActivity.this, "You can login now", Toast.LENGTH_LONG).show();
        }
    }





}
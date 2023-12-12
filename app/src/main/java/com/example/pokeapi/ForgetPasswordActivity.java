package com.example.pokeapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText editTextTextPwdResetEmail;
    Button buttonPwdReset;
    FirebaseAuth authProfile;

    final static String TAG = "Foreget PWD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        editTextTextPwdResetEmail = findViewById(R.id.editTextTextPwdResetEmail);
        buttonPwdReset = findViewById(R.id.buttonPwdReset);

        buttonPwdReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextTextPwdResetEmail.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ForgetPasswordActivity.this, "Please enter your registered email", Toast.LENGTH_LONG).show();
                    editTextTextPwdResetEmail.setError("Email is required");
                    editTextTextPwdResetEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ForgetPasswordActivity.this, "Please enter a valid email", Toast.LENGTH_LONG).show();
                    editTextTextPwdResetEmail.setError("Valid email is required");
                    editTextTextPwdResetEmail.requestFocus();
                }else{
                    resetPassword(email);
                }
            }
        });
    }

    private void resetPassword(String email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgetPasswordActivity.this, "Please check your inbox for password reset link", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    try{
                        throw  task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        editTextTextPwdResetEmail.setError("User does not exists. Please register again");
                    }catch(Exception e){
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        });
    }
}
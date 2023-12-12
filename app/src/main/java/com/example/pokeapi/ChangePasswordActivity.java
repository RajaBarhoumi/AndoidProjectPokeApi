package com.example.pokeapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokeapi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText editTextAuthPwd, editTextTextPassword;
    TextView textViewAuth;

    Button buttonAuth, buttonChangePassword;

    FirebaseAuth authProfile;

    ImageView imageView_show_hide_pwdChange;

    String userPwdCurr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editTextAuthPwd = findViewById(R.id.editTextAuthPwd);
        editTextTextPassword = findViewById(R.id.editTextTextPassword);
        textViewAuth = findViewById(R.id.textViewAuth);
        buttonAuth = findViewById(R.id.buttonAuth);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        imageView_show_hide_pwdChange = findViewById(R.id.imageView_show_hide_pwdChange);

        imageView_show_hide_pwdChange.setImageResource(R.drawable.ic_hide_pwd);

        editTextTextPassword.setEnabled(false);
        buttonChangePassword.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser.equals("")){
            Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }else{
            reAuthentificateUser(firebaseUser);
        }


        imageView_show_hide_pwdChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextTextPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageView_show_hide_pwdChange.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageView_show_hide_pwdChange.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });



    }

    private void reAuthentificateUser(FirebaseUser firebaseUser) {
        buttonAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPwdCurr = editTextAuthPwd.getText().toString();

                if(TextUtils.isEmpty(userPwdCurr)){
                    editTextAuthPwd.setError("Please enter your current password to authenticate");
                    editTextAuthPwd.requestFocus();
                }else{
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                editTextAuthPwd.setEnabled(false);
                                editTextTextPassword.setEnabled(true);
                                buttonAuth.setEnabled(false);
                                buttonChangePassword.setEnabled(true);
                                textViewAuth.setText("You can change your password now.");

                                buttonChangePassword.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changePwd(firebaseUser);
                                    }
                                });
                            }else{
                                try{
                                    throw task.getException();
                                }catch(Exception e){
                                    editTextAuthPwd.setError("Wrong password");
                                    editTextAuthPwd.requestFocus();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String newPwd = editTextTextPassword.getText().toString();
        if(TextUtils.isEmpty(newPwd)){
            editTextTextPassword.setError("Password is needed");
            editTextTextPassword.requestFocus();
        }else if(userPwdCurr.matches(newPwd)){
            editTextTextPassword.setError("New password cannot be same as old password!");
            editTextTextPassword.requestFocus();
        }else{
            firebaseUser.updatePassword(newPwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this, "Password has been changed", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else{
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Log.d("Change password", e.getMessage());
                        }
                    }
                }
            });
        }
    }
}
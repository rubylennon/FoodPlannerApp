package com.example.firebasecrudapplication;

/*
 * @Author: Ruby Lennon (x19128355)
 * 28th April 2023
 * LoginActivity.java
 * Description - Login Activity to allow registered users to login to application
 */

// @REF 1 - GeeksForGeeks Tutorial - https://www.youtube.com/watch?v=-Gvpf8tXpbc
// Ref Description - User Authentication and CRUD Operation with Firebase Realtime Database in Android

//imports
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    // variables
    private TextInputEditText userNameEdt,
            pwdEdt;
    private ProgressBar loadingPB;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the activity layout
        setContentView(R.layout.activity_login);

        // assign layout elements by ID and assign to variables
        userNameEdt = findViewById(R.id.idEdtUserName);
        pwdEdt = findViewById(R.id.idEdtPwd);
        Button loginBtn = findViewById(R.id.idBtnLogin);
        loadingPB = findViewById(R.id.idPBLoading);
        loadingPB.setVisibility(View.GONE);
        TextView registerTV = findViewById(R.id.idTVRegister);
        mAuth = FirebaseAuth.getInstance();

        // set the activity title to Login
        setTitle("Login");

        // if the register activity link is clicked then redirect user to registration activity
        registerTV.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(i);
        });

        // if the login button is clicked then execute the following
        loginBtn.setOnClickListener(v -> {
            // hide the loading progress bar
            loadingPB.setVisibility(View.VISIBLE);

            // get the username and password user input values and store to local variables
            String userName = Objects.requireNonNull(userNameEdt.getText()).toString();
            String pwd = Objects.requireNonNull(pwdEdt.getText()).toString();

            // if either the password or username is empty execute the following
            if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)){
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Please add your credentials...",
                        Toast.LENGTH_SHORT).show();
            } else {
                // else if the password and username value is provided then execute the following
                // sign in the user to their account using the provided username and password
                mAuth.signInWithEmailAndPassword(userName,pwd).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        loadingPB.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        // redirect the user to the main activity (My Recipes)
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        // if the login fails then execute the following
                        loadingPB.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Login Failed. Please try again...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        // if the user is already signed in then redirect the user to the main activity (My Recipes)
        if(user != null) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            this.finish();
        }
    }
}
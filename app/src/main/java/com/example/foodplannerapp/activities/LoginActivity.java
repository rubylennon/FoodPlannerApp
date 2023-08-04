package com.example.foodplannerapp.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 28th April 2023
 * LoginActivity.java
 * Description - Login Activity to allow registered users to login to application
 */

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

import com.example.foodplannerapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

// @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
// Reference description - tutorial on how to create a Login Activity for Firebase Authenticate
public class LoginActivity extends AppCompatActivity {
    // declare variables
    private TextInputEditText userNameEdt,
            pwdEdt;
    private ProgressBar loadingPB;
    private FirebaseAuth firebaseAuth;

    // onCreate method to be execute when activity is launched
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the activity layout
        setContentView(R.layout.activity_login);

        // set the activity title to Login
        setTitle("Login");

        // assign layout elements by ID and assign to variables
        userNameEdt = findViewById(R.id.idEdtUserName);
        pwdEdt = findViewById(R.id.idEdtPwd);
        loadingPB = findViewById(R.id.idPBLoading);
        TextView registerTV = findViewById(R.id.idTVRegister);
        Button loginBtn = findViewById(R.id.idBtnLogin);

        // hide the progress bar
        loadingPB.setVisibility(View.GONE);

        // retrieve and store the current firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance();

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
                firebaseAuth.signInWithEmailAndPassword(userName,pwd).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){// if the tak is successful
                        loadingPB.setVisibility(View.GONE);// hide the progress loading bar
                        Toast.makeText(LoginActivity.this, "Login Successful",
                                Toast.LENGTH_SHORT).show();// show toast
                        // redirect the user to the main activity (My Recipes)
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);// start Main activity
                        finish();// finish this activity
                    } else {// if the task is not successful
                        // if the login fails then execute the following
                        loadingPB.setVisibility(View.GONE);// hide the progress loading bar
                        Toast.makeText(LoginActivity.this, "Login Failed. Please try again...",
                                Toast.LENGTH_SHORT).show();// show toasr
                    }
                });
            }
        });
    }

    // onStart method to be executed when the activity starts
    @Override
    protected void onStart() {
        super.onStart();

        // get current firebase user and assign to variable
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // if the user is already signed in then redirect the user to the main activity (My Recipes)
        if(user != null) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            this.finish();// finish this activity
        }
    }
}
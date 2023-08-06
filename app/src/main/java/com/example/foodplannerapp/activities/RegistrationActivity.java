package com.example.foodplannerapp.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 28th April 2023
 * RegistrationActivity.java
 * Description - Registration Activity to allow new users to register for an account
 */

// imports
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
import com.example.foodplannerapp.utilities.ValidPasswordCheck;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

// @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
// Reference description - tutorial on how to create a Recycler View Adapter
public class RegistrationActivity extends AppCompatActivity {
    // declare variables
    private TextInputEditText userNameEdt,
            pwdEdt,
            cnfPwdEdt;
    private ProgressBar loadingPB;
    private FirebaseAuth firebaseAuth;
    private TextView pwdHelpText;

    // onCreate method to be execute when activity is launched
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set activity layout
        setContentView(R.layout.activity_registration);

        // set activity title
        setTitle("Create Account");

        // find layout elements by ID and assign to variables
        userNameEdt = findViewById(R.id.idEdtUserName);
        pwdEdt = findViewById(R.id.idEdtPwd);
        cnfPwdEdt = findViewById(R.id.idEdtCnfPwd);
        loadingPB = findViewById(R.id.idPBLoading);
        loadingPB.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        pwdHelpText = findViewById(R.id.pwdHelpText);
        pwdHelpText.setVisibility(View.GONE);
        Button registerBtn = findViewById(R.id.idBtnRegister);
        TextView loginTV = findViewById(R.id.idTVLogin);

        // if user clicks Login TextView redirect them to the Login Page
        loginTV.setOnClickListener(v -> {
            Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(i);// start Login Activity
        });

        // if user clicks Register button execute the following
        registerBtn.setOnClickListener(v -> {
            loadingPB.setVisibility(View.VISIBLE);// show progress bar
            pwdHelpText.setVisibility(View.GONE);// hide password help text

            // get text from input fields
            String userName = Objects.requireNonNull(userNameEdt.getText()).toString();
            String pwd = Objects.requireNonNull(pwdEdt.getText()).toString();
            String cnfPwd = Objects.requireNonNull(cnfPwdEdt.getText()).toString();

            // minimum and password length
            int MIN_PASSWORD_LENGTH = 8;
            int MAX_PASSWORD_LENGTH = 20;

            // if the email, password and password confirmation fields are not empty complete
            // the following
            if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(cnfPwd)){
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(RegistrationActivity.this, "Please add your credentials...", Toast.LENGTH_SHORT).show();
            } else if(!pwd.equals(cnfPwd)){
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(RegistrationActivity.this, "Passwords must match", Toast.LENGTH_SHORT).show();
            } else if(pwd.length() < MIN_PASSWORD_LENGTH){
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(RegistrationActivity.this, "Passwords must be at least 8 characters", Toast.LENGTH_SHORT).show();
            } else if(pwd.length() > MAX_PASSWORD_LENGTH){
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(RegistrationActivity.this, "Passwords must be less than 20 characters", Toast.LENGTH_SHORT).show();
            } else if(!ValidPasswordCheck.isPasswordValid(pwd)){// check password meets requirements using ValidPasswordCheck isPasswordValid utility check
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(RegistrationActivity.this, "Invalid password.", Toast.LENGTH_SHORT).show();
                pwdHelpText.setVisibility(View.VISIBLE);
            } else{// if all required information is valid and provided execute the following
                // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
                // Reference description - tutorial on how to create a Register Activity for Firebase Authenticate
                // create a Firebase Authentication user
                firebaseAuth.createUserWithEmailAndPassword(userName,pwd).addOnCompleteListener(task -> {
                     if(task.isSuccessful()){ // if the task is successful then execute the following
                         loadingPB.setVisibility(View.GONE);// hide the progress bar
                         Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                         Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
                         startActivity(i);// start Main activity
                         finish();
                     } else { // if the task fails execute the following
                         loadingPB.setVisibility(View.GONE);// hide the loading progress bar
                         // display error toast with failure reason
                         if(Objects.requireNonNull(task.getException()).toString().contains("The email address is badly formatted.")){
                             Toast.makeText(RegistrationActivity.this, "Registration failed. Please add valid email.",
                                     Toast.LENGTH_SHORT).show();
                         } else if(Objects.requireNonNull(task.getException()).toString().contains("The given password is invalid. [ Password should be at least 6 characters ]")){
                             Toast.makeText(RegistrationActivity.this, "Registration failed. Password should be at least 6 characters.",
                                     Toast.LENGTH_SHORT).show();
                         } else if(Objects.requireNonNull(task.getException()).toString().contains("The email address is already in use by another account")){
                             Toast.makeText(RegistrationActivity.this, "Registration failed. The email address is already in use by another account.",
                                     Toast.LENGTH_SHORT).show();
                         } else {
                             Toast.makeText(RegistrationActivity.this, "Registration failed. Please try again...", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
            }
        });
    }
}
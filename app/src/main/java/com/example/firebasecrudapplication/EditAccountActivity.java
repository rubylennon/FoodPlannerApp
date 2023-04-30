/*
 * @Author: Ruby Lennon (x19128355)
 * 30th February 2023
 * EditAccountActivity.java
 * Description - Edit Account Activity of Java Android App 'FoodPlannerApp'
 */

// @REF: GeeksForGeeks - https://www.youtube.com/watch?v=FptELNWvnqQ
// Ref Description - Firebase Authentication 4: Change password, email & Delete users #2020 | Android Studio Tutorial

package com.example.firebasecrudapplication;

//imports
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditAccountActivity extends AppCompatActivity {
    // variables
    private TextInputEditText idEdtEmail,
            idEdtPassword;
    private Button idBtnUpdateEmail,
            idBtnUpdatePassword,
            idBtnDeleteAccount;
    ProgressBar idPBLoading;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        // set the actionbar title
        setTitle("Account Settings");

        // assign layout fields to variables
        idBtnUpdateEmail = findViewById(R.id.idBtnUpdateEmail);
        idBtnUpdatePassword = findViewById(R.id.idBtnUpdatePassword);
        idBtnDeleteAccount = findViewById(R.id.idBtnDeleteAccount);
        idPBLoading = findViewById(R.id.idPBLoading);
        idEdtEmail = findViewById(R.id.idEdtEmail);
        idEdtPassword = findViewById(R.id.idEdtPassword);

        //get firebase instance
        firebaseAuth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // if user is not logged in navigate to login page
                if(user == null){
                    Intent i = new Intent(EditAccountActivity.this, LoginActivity.class );
                    startActivity(i);
                    finish();
                }
            }
        };

        // update email method
        idBtnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set progress bar to visible
                idPBLoading.setVisibility(View.VISIBLE);
                // if the email field is empty do the following
                if(idEdtEmail.getText().toString().trim().equals("")){
                    Toast.makeText(EditAccountActivity.this, "Please enter new email", Toast.LENGTH_SHORT).show();
                    idPBLoading.setVisibility(View.GONE);
                }
                //if user is logged in and email is not empty do the following
                if(user != null && !idEdtEmail.getText().toString().trim().equals("")){
                    //update email of signed in user using new email value
                    user.updateEmail(idEdtEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(EditAccountActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
                                        idPBLoading.setVisibility(View.GONE);
                                    }else{
                                        Toast.makeText(EditAccountActivity.this, "Email update failed", Toast.LENGTH_SHORT).show();
                                        idPBLoading.setVisibility(View.GONE);
                                    }

                                }
                            });
                }
            }
        });


        idBtnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                idPBLoading.setVisibility(View.VISIBLE);

                if(idEdtPassword.getText().toString().trim().equals("")){
                    Toast.makeText(EditAccountActivity.this, "Please enter new password", Toast.LENGTH_SHORT).show();
                    idPBLoading.setVisibility(View.GONE);
                }

                if(idEdtPassword.getText().toString().length() < 7){
                    Toast.makeText(EditAccountActivity.this, "Please enter minimum 7 character password", Toast.LENGTH_SHORT).show();
                    idPBLoading.setVisibility(View.GONE);
                }

                if(user != null && idEdtPassword.getText().toString().length() >= 7){

                    user.updatePassword(idEdtPassword.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(EditAccountActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                        idPBLoading.setVisibility(View.GONE);
                                    }else{
                                        Toast.makeText(EditAccountActivity.this, "Password update failed", Toast.LENGTH_SHORT).show();
                                        idPBLoading.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });

        idBtnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                idPBLoading.setVisibility(View.VISIBLE);

                if(user != null){

                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        Toast.makeText(EditAccountActivity.this, "Account Successfully Deleted", Toast.LENGTH_SHORT).show();
                                        idPBLoading.setVisibility(View.GONE);

                                        //navigate to login page
                                        Intent i = new Intent(EditAccountActivity.this, LoginActivity.class);
                                        startActivity(i);
                                        finish();
                                    }else{
                                        Toast.makeText(EditAccountActivity.this, "Account Deletion Failed", Toast.LENGTH_SHORT).show();
                                        idPBLoading.setVisibility(View.GONE);
                                    }

                                }
                            });
                }
            }
        });
    }
}
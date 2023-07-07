package com.example.firebasecrudapplication;

//@REF 1 - GeeksForGeeks Tutorial - https://www.youtube.com/watch?v=-Gvpf8tXpbc
//@REF 2 - https://firebase.google.com/docs/auth/android/password-auth

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    // variables
    private TextInputEditText userNameEdt,
            pwdEdt,
            cnfPwdEdt;
    private Button registerBtn;
    private ProgressBar loadingPB;
    private TextView LoginTV;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // initialise variables
        userNameEdt = findViewById(R.id.idEdtUserName);
        pwdEdt = findViewById(R.id.idEdtPwd);
        cnfPwdEdt = findViewById(R.id.idEdtCnfPwd);
        registerBtn = findViewById(R.id.idBtnRegister);
        loadingPB = findViewById(R.id.idPBLoading);
        loadingPB.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        LoginTV = findViewById(R.id.idTVLogin);

        // if user clicks Login TextView redirect them to the Login Page
        LoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        // if user clicks Register button execute the following
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);

                // get text from input fields
                String userName = userNameEdt.getText().toString();
                String pwd = pwdEdt.getText().toString();
                String cnfPwd = cnfPwdEdt.getText().toString();

                if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(cnfPwd)){
                    loadingPB.setVisibility(View.GONE);
                    Toast.makeText(RegistrationActivity.this, "Please add your credentials...", Toast.LENGTH_SHORT).show();
                } else if(!pwd.equals(cnfPwd)){
                    Toast.makeText(RegistrationActivity.this, "Passwords must match", Toast.LENGTH_SHORT).show();
                } else{
                     mAuth.createUserWithEmailAndPassword(userName,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                loadingPB.setVisibility(View.GONE);
                                Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Log.w("createUserWithEmail:failure", task.getException());

                                loadingPB.setVisibility(View.GONE);

                                if(Objects.requireNonNull(task.getException()).toString().contains("The email address is badly formatted.")){
                                    Toast.makeText(RegistrationActivity.this, "Registration failed. Please add valid email.", Toast.LENGTH_SHORT).show();
                                } else if(Objects.requireNonNull(task.getException()).toString().contains("The given password is invalid. [ Password should be at least 6 characters ]")){
                                    Toast.makeText(RegistrationActivity.this, "Registration failed. Password should be at least 6 characters.", Toast.LENGTH_SHORT).show();
                                } else if(Objects.requireNonNull(task.getException()).toString().contains("The email address is already in use by another account")){
                                    Toast.makeText(RegistrationActivity.this, "Registration failed. The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                                } else if(Objects.requireNonNull(task.getException()).toString().contains("The given password is invalid. [ Password should be at least 6 characters ]")){
                                    Toast.makeText(RegistrationActivity.this, "Registration failed. The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegistrationActivity.this, "Registration failed. Please try again...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }

            }
        });
    }
}
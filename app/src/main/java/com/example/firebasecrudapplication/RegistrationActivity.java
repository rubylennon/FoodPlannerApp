package com.example.firebasecrudapplication;

//@REF 1 - GeeksForGeeks Tutorial - https://www.youtube.com/watch?v=-Gvpf8tXpbc
//@REF 2 - https://firebase.google.com/docs/auth/android/password-auth
//@REF 3 - https://www.geeksforgeeks.org/how-to-validate-a-password-using-regular-expressions-in-java/

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.text.Regex;

public class RegistrationActivity extends AppCompatActivity {

    // variables
    private TextInputEditText userNameEdt,
            pwdEdt,
            cnfPwdEdt;
    private Button registerBtn;
    private ProgressBar loadingPB;
    private TextView LoginTV;
    private FirebaseAuth mAuth;
    private TextView pwdHelpText;

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
        pwdHelpText = findViewById(R.id.pwdHelpText);
        pwdHelpText.setVisibility(View.GONE);

        setTitle("Create Account");

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
                pwdHelpText.setVisibility(View.GONE);

                // get text from input fields
                String userName = userNameEdt.getText().toString();
                String pwd = pwdEdt.getText().toString();
                String cnfPwd = cnfPwdEdt.getText().toString();

                int MIN_PASSWORD_LENGTH = 8;
                int MAX_PASSWORD_LENGTH = 20;

                System.out.println(isValidPassword(pwd));

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
                } else if(!isValidPassword(pwd)){
                    loadingPB.setVisibility(View.GONE);
                    Toast.makeText(RegistrationActivity.this, "Invalid password.", Toast.LENGTH_SHORT).show();
                    pwdHelpText.setVisibility(View.VISIBLE);
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

    public static boolean isValidPassword(String password){

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the password is empty
        // return false
        if (password == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.
        Matcher m = p.matcher(password);

        // Return if the password
        // matched the ReGex
        return m.matches();
    }
}
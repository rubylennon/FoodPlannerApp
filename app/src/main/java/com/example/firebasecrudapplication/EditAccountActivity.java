/*
 * @Author: Ruby Lennon (x19128355)
 * 30th February 2023
 * EditAccountActivity.java
 * Description - Edit Account Activity of Java Android App 'FoodPlannerApp'
 */

// @REF: https://www.youtube.com/watch?v=FptELNWvnqQ
// Ref Description - Firebase Authentication 4: Change password, email & Delete users #2020 | Android Studio Tutorial

package com.example.firebasecrudapplication;

//imports
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditAccountActivity extends AppCompatActivity {
    // variables
    private TextInputEditText idEdtEmail,
            idEdtEmailConfirmation,
            idEdtPasswordNewPwd,
            idEdtConfirmNewPwdConfirmation,
            idCurrentEmail;
    private ProgressBar idPBLoading;
    private FirebaseAuth mAuth;
    private TextView emailHelpText,
            pwdHelpText,
            pwdHelpText2,
            deleteHelpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        // set the actionbar title
        setTitle("Account Settings");

        // assign layout fields to variables
        mAuth = FirebaseAuth.getInstance();
        Button idBtnUpdateEmail = findViewById(R.id.idBtnUpdateEmail);
        Button idBtnUpdatePassword = findViewById(R.id.idBtnUpdatePassword);
        Button idBtnDeleteAccount = findViewById(R.id.idBtnDeleteAccount);
        idPBLoading = findViewById(R.id.idPBLoading);
        idEdtEmail = findViewById(R.id.idEdtEmail);
        idEdtEmailConfirmation = findViewById(R.id.idEdtEmailConfirmation);
        idEdtPasswordNewPwd = findViewById(R.id.idEdtPasswordNewPwd);
        idEdtConfirmNewPwdConfirmation = findViewById(R.id.idEdtConfirmNewPwdConfirmation);
        idCurrentEmail = findViewById(R.id.idCurrentEmail);
        emailHelpText = findViewById(R.id.emailHelpText);
        emailHelpText.setVisibility(View.GONE);
        pwdHelpText = findViewById(R.id.pwdHelpText);
        pwdHelpText.setVisibility(View.GONE);
        pwdHelpText2 = findViewById(R.id.pwdHelpText2);
        pwdHelpText2.setVisibility(View.GONE);
        deleteHelpText = findViewById(R.id.deleteHelpText);
        deleteHelpText.setVisibility(View.GONE);

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //update current email text view to display current users email address
        assert user != null;
        idCurrentEmail.setText(user.getEmail());
        idCurrentEmail.setFocusable(false);
        idCurrentEmail.setEnabled(false);
        idCurrentEmail.setCursorVisible(false);

        // update email method
        idBtnUpdateEmail.setOnClickListener(view -> {
            // set progress bar to visible
            idPBLoading.setVisibility(View.VISIBLE);
            // hide email update help text
            emailHelpText.setVisibility(View.GONE);

            // get new email values from text input fields
            String newEmail = Objects.requireNonNull(idEdtEmail.getText()).toString();
            String newEmailConfirmation = Objects.requireNonNull(idEdtEmailConfirmation.getText()).toString();

            // if the email field is empty do the following
            if(TextUtils.isEmpty(newEmail)){
                Toast.makeText(EditAccountActivity.this, "Please enter your new email", Toast.LENGTH_SHORT).show();
                idPBLoading.setVisibility(View.GONE);
            } else if (TextUtils.isEmpty(newEmailConfirmation) || newEmailConfirmation.equals("Confirm New Email")){
                Toast.makeText(EditAccountActivity.this, "Please confirm your new email", Toast.LENGTH_SHORT).show();
                idPBLoading.setVisibility(View.GONE);
            } else if (newEmail.equals(user.getEmail())){
                Toast.makeText(EditAccountActivity.this, "Your account email is already set to this email", Toast.LENGTH_SHORT).show();
                idPBLoading.setVisibility(View.GONE);
            } else if (!newEmail.equals(newEmailConfirmation)){
                Toast.makeText(EditAccountActivity.this, "Email addresses do not match", Toast.LENGTH_SHORT).show();
                idPBLoading.setVisibility(View.GONE);
            } else {
                //update email of signed in user using new email value
                user.updateEmail(idEdtEmail.getText().toString().trim())
                        .addOnCompleteListener(task -> {

                            if(task.isSuccessful()){
                                Toast.makeText(EditAccountActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
                                idCurrentEmail.setText(newEmail);
                                idPBLoading.setVisibility(View.GONE);
                            }else{
                                idPBLoading.setVisibility(View.GONE);

                                if(Objects.requireNonNull(task.getException()).toString().contains("The email address is badly formatted.")){
                                    Toast.makeText(EditAccountActivity.this, "Email update failed. Please add valid email.", Toast.LENGTH_SHORT).show();
                                } else if(Objects.requireNonNull(task.getException()).toString().contains("The email address is already in use by another account")){
                                    Toast.makeText(EditAccountActivity.this, "Email update failed. The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                                } else if(Objects.requireNonNull(task.getException()).toString().contains("This operation is sensitive and requires recent authentication. Log in again before retrying this request.")){
                                    Toast.makeText(EditAccountActivity.this, "Email update failed. Please log in again before retrying this request.", Toast.LENGTH_SHORT).show();
                                    emailHelpText.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(EditAccountActivity.this, "Email update failed. Please try again...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
        });

        // update password button functionality
        idBtnUpdatePassword.setOnClickListener(view -> {
            idPBLoading.setVisibility(View.VISIBLE);
            pwdHelpText.setVisibility(View.GONE);
            pwdHelpText2.setVisibility(View.GONE);

            // get text from input fields
            String pwd = Objects.requireNonNull(idEdtPasswordNewPwd.getText()).toString();
            String cnfPwd = Objects.requireNonNull(idEdtConfirmNewPwdConfirmation.getText()).toString();

            // minimum and password length
            int MIN_PASSWORD_LENGTH = 8;
            int MAX_PASSWORD_LENGTH = 20;

            // if the password field is not empty complete the following
            if(TextUtils.isEmpty(pwd)){
                idPBLoading.setVisibility(View.GONE);
                Toast.makeText(EditAccountActivity.this, "Please add your new password...", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(cnfPwd) || cnfPwd.equals("Confirm New Email")) {
                idPBLoading.setVisibility(View.GONE);
                Toast.makeText(EditAccountActivity.this, "Please confirm your new password...", Toast.LENGTH_SHORT).show();
            }else if(!pwd.equals(cnfPwd)){
                idPBLoading.setVisibility(View.GONE);
                Toast.makeText(EditAccountActivity.this, "Passwords must match", Toast.LENGTH_SHORT).show();
            } else if(pwd.length() < MIN_PASSWORD_LENGTH){
                idPBLoading.setVisibility(View.GONE);
                Toast.makeText(EditAccountActivity.this, "Passwords must be at least 8 characters", Toast.LENGTH_SHORT).show();
            } else if(pwd.length() > MAX_PASSWORD_LENGTH){
                idPBLoading.setVisibility(View.GONE);
                Toast.makeText(EditAccountActivity.this, "Passwords must be less than 20 characters", Toast.LENGTH_SHORT).show();
            } else if(!isValidPassword(pwd)){
                idPBLoading.setVisibility(View.GONE);
                Toast.makeText(EditAccountActivity.this, "Invalid password.", Toast.LENGTH_SHORT).show();
                pwdHelpText.setVisibility(View.VISIBLE);
            } else{
                //update the users password
                user.updatePassword(pwd).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(EditAccountActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                        idPBLoading.setVisibility(View.GONE);
                        pwdHelpText.setVisibility(View.GONE);
                    }else{
                        pwdHelpText.setVisibility(View.GONE);
                        if(Objects.requireNonNull(task.getException()).toString().contains("The given password is invalid. [ Password should be at least 6 characters ]")){
                            Toast.makeText(EditAccountActivity.this, "Registration failed. Password should be at least 6 characters.", Toast.LENGTH_SHORT).show();
                        } else if(Objects.requireNonNull(task.getException()).toString().contains("This operation is sensitive and requires recent authentication. Log in again before retrying this request.")){
                            Toast.makeText(EditAccountActivity.this, "Email update failed. Please log in again before retrying this request.", Toast.LENGTH_SHORT).show();
                            pwdHelpText2.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(EditAccountActivity.this, "Password update failed. Please try again...", Toast.LENGTH_SHORT).show();
                        }
                        idPBLoading.setVisibility(View.GONE);
                    }
                });

            }
        });

        // delete account button functionality
        idBtnDeleteAccount.setOnClickListener(view -> {
            idPBLoading.setVisibility(View.VISIBLE);
            deleteHelpText.setVisibility(View.GONE);

            user.delete().addOnCompleteListener(task -> {

                if(task.isSuccessful()){
                    // display the following toast notification
                    Toast.makeText(EditAccountActivity.this, "Account Successfully Deleted", Toast.LENGTH_SHORT).show();
                    idPBLoading.setVisibility(View.GONE);

                    //navigate to login page
                    Intent i = new Intent(EditAccountActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }else if(Objects.requireNonNull(task.getException()).toString().contains("This operation is sensitive and requires recent authentication. Log in again before retrying this request.")){
                    Toast.makeText(EditAccountActivity.this, "Account deletion failed. Please log in again before retrying this request.", Toast.LENGTH_SHORT).show();
                    deleteHelpText.setVisibility(View.VISIBLE);
                    idPBLoading.setVisibility(View.GONE);
                } else{
                    Toast.makeText(EditAccountActivity.this, "Account Deletion Failed", Toast.LENGTH_SHORT).show();
                    idPBLoading.setVisibility(View.GONE);
                }

            });
        });
    }

    // method for checking if user supplied password is valid
    public static boolean isValidPassword(String password){
        // Regex to check valid password.
        String regex = "^(?=.*\\d)"
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

    // settings menu start
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_main,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case R.id.idAddRecipe:
                Intent i1 = new Intent(EditAccountActivity.this, AddRecipeActivity.class);
                startActivity(i1);
                return true;
            case R.id.idMyRecipes:
                Intent i2 = new Intent(EditAccountActivity.this, MainActivity.class);
                startActivity(i2);
                return true;
            case R.id.idPublicRecipes:
                Intent i3 = new Intent(EditAccountActivity.this, PublicRecipesActivity.class);
                startActivity(i3);
                return true;
            case R.id.idScan:
                Intent i4 = new Intent(EditAccountActivity.this, IngredientsScannerActivity.class);
                startActivity(i4);
                return true;
            case R.id.idSearch:
                Intent i5 = new Intent(EditAccountActivity.this, RecipeSearchActivity.class);
                startActivity(i5);
                return true;
            case R.id.idMealPlan:
                Intent i6 = new Intent(EditAccountActivity.this, MealPlanActivity.class);
                startActivity(i6);
                return true;
            case R.id.idEditAccount:
                Intent i7 = new Intent(EditAccountActivity.this, EditAccountActivity.class);
                startActivity(i7);
                return true;
            case R.id.idLogout:
                Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent i8 = new Intent(EditAccountActivity.this, LoginActivity.class);
                startActivity(i8);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // settings menu end
}
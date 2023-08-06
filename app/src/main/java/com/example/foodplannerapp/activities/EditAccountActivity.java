package com.example.foodplannerapp.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 30th February 2023
 * EditAccountActivity.java
 * Description - Edit Account Activity for editing user account details
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

import androidx.appcompat.app.AlertDialog;

import com.example.foodplannerapp.R;
import com.example.foodplannerapp.utilities.ValidPasswordCheck;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class EditAccountActivity extends BaseMenuActivity {
    // declare variables
    private TextInputEditText idEdtEmail,
            idEdtEmailConfirmation,
            idEdtPasswordNewPwd,
            idEdtConfirmNewPwdConfirmation,
            idCurrentEmail;
    private ProgressBar idPBLoading;
    private TextView emailHelpText,
            pwdHelpText,
            pwdHelpText2,
            deleteHelpText;

    // onCreate method for Edit Account activity which executes when activity launches
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the layout view
        setContentView(R.layout.activity_edit_account);

        // set the actionbar title
        setTitle("Account Settings");

        // assign layout fields to variables
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
        pwdHelpText = findViewById(R.id.pwdHelpText);
        pwdHelpText2 = findViewById(R.id.pwdHelpText2);
        deleteHelpText = findViewById(R.id.deleteHelpText);

        // hide help text
        emailHelpText.setVisibility(View.GONE);
        pwdHelpText.setVisibility(View.GONE);
        pwdHelpText2.setVisibility(View.GONE);
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

            // if the email fields are empty, do not match or are current email do the following
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
            } else { // if the above checks pass then execute the following
                //@Reference - https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
                //Reference description - tutorial on how to Create an Alert Dialog Box in Android
                // Create AlertDialog Builder class object
                AlertDialog.Builder builder = new AlertDialog.Builder(EditAccountActivity.this);
                // Set the alert dialog message
                builder.setMessage("Do you want to update your email address?");
                // Set alert Title
                builder.setTitle("Update Email Confirmation");
                // set cancelable by clicking outside dialog to false
                builder.setCancelable(false);
                // Set Yes button action
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    // @Reference - https://www.youtube.com/watch?v=FptELNWvnqQ
                    // Ref Description - Tutorial on how to update Firebase Authentication users password/email
                    // and on how to delete users
                    // update email of signed in user using new email value
                    user.updateEmail(idEdtEmail.getText().toString().trim())
                            .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){// if the task is successful
                                Toast.makeText(EditAccountActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
                                idCurrentEmail.setText(newEmail);// update the current email textview value
                                idPBLoading.setVisibility(View.GONE);// hide the progress bar
                            }else{// if the task is not successful
                                idPBLoading.setVisibility(View.GONE);// hide the progress bar
                                // return different errors based on exception from Firebase Authentication
                                if(Objects.requireNonNull(task.getException()).toString().contains("The email address is badly formatted.")){
                                    Toast.makeText(EditAccountActivity.this, "Email update failed. Please add valid email.", Toast.LENGTH_SHORT).show();
                                } else if(Objects.requireNonNull(task.getException()).toString().contains("The email address is already in use by another account")){
                                    Toast.makeText(EditAccountActivity.this, "Email update failed. The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                                } else if(Objects.requireNonNull(task.getException()).toString().contains("This operation is sensitive and requires recent authentication. Log in again before retrying this request.")){
                                    Toast.makeText(EditAccountActivity.this, "Email update failed. Please log in again before retrying this request.", Toast.LENGTH_SHORT).show();
                                    emailHelpText.setVisibility(View.VISIBLE);// display the email help text on screen
                                } else {
                                    Toast.makeText(EditAccountActivity.this, "Email update failed. Please try again...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                });
                // Set the No button action
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();// If user click no then dialog box is canceled and closed
                    idPBLoading.setVisibility(View.GONE);// hide progress bar
                });
                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog
                alertDialog.show();
            }
        });

        // update password button functionality
        idBtnUpdatePassword.setOnClickListener(view -> {
            idPBLoading.setVisibility(View.VISIBLE);// show the progress bar
            pwdHelpText.setVisibility(View.GONE);// hide the password help text
            pwdHelpText2.setVisibility(View.GONE);// hide the password help text

            // get text from input fields
            String pwd = Objects.requireNonNull(idEdtPasswordNewPwd.getText()).toString();
            String cnfPwd = Objects.requireNonNull(idEdtConfirmNewPwdConfirmation.getText()).toString();

            // set minimum and password length
            int MIN_PASSWORD_LENGTH = 8;
            int MAX_PASSWORD_LENGTH = 20;

            // password validation checks
            if(TextUtils.isEmpty(pwd)){
                idPBLoading.setVisibility(View.GONE);// hide the progress bar
                Toast.makeText(EditAccountActivity.this, "Please add your new password...", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(cnfPwd) || cnfPwd.equals("Confirm New Password")) {
                idPBLoading.setVisibility(View.GONE);// hide the progress bar
                Toast.makeText(EditAccountActivity.this, "Please confirm your new password...", Toast.LENGTH_SHORT).show();
            }else if(!pwd.equals(cnfPwd)){
                idPBLoading.setVisibility(View.GONE);// hide the progress bar
                Toast.makeText(EditAccountActivity.this, "Passwords must match", Toast.LENGTH_SHORT).show();
            } else if(pwd.length() < MIN_PASSWORD_LENGTH){
                idPBLoading.setVisibility(View.GONE);// hide the progress bar
                Toast.makeText(EditAccountActivity.this, "Passwords must be at least 8 characters", Toast.LENGTH_SHORT).show();
            } else if(pwd.length() > MAX_PASSWORD_LENGTH){
                idPBLoading.setVisibility(View.GONE);// hide the progress bar
                Toast.makeText(EditAccountActivity.this, "Passwords must be less than 20 characters", Toast.LENGTH_SHORT).show();
            } else if(ValidPasswordCheck.isPasswordValid(pwd)){// password check using ValidPasswordCheck isPasswordValid utility method
                idPBLoading.setVisibility(View.GONE);// hide the progress bar
                Toast.makeText(EditAccountActivity.this, "Invalid password.", Toast.LENGTH_SHORT).show();
                pwdHelpText.setVisibility(View.VISIBLE);// display password help text
            } else{// if password checks pass then execute the following
                //@Reference - https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
                //Reference description - tutorial on how to Create an Alert Dialog Box in Android
                // Create AlertDialog Builder class object
                AlertDialog.Builder builder2 = new AlertDialog.Builder(EditAccountActivity.this);
                // Set the alert dialog message
                builder2.setMessage("Do you want to update your password?");
                // Set Alert Title
                builder2.setTitle("Update Password Confirmation");
                // set cancelable by clicking outside dialog to false
                builder2.setCancelable(false);
                // Set the Yes button action
                builder2.setPositiveButton("Yes", (dialog, which) -> {
                    // @Reference - https://www.youtube.com/watch?v=FptELNWvnqQ
                    // Ref Description - Tutorial on how to update Firebase Authentication users password/email
                    // and on how to delete users
                    //update the users password
                    user.updatePassword(pwd).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){// if the task is successful
                            Toast.makeText(EditAccountActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                            idPBLoading.setVisibility(View.GONE);// hide the progress bar
                            pwdHelpText.setVisibility(View.GONE);// hide the password help text
                        }else{// display different error message based on error received from Firebase Authenticate
                            pwdHelpText.setVisibility(View.GONE);// hide the password help text
                            if(Objects.requireNonNull(task.getException()).toString().contains("The given password is invalid. [ Password should be at least 6 characters ]")){
                                Toast.makeText(EditAccountActivity.this, "Registration failed. Password should be at least 6 characters.", Toast.LENGTH_SHORT).show();
                            } else if(Objects.requireNonNull(task.getException()).toString().contains("This operation is sensitive and requires recent authentication. Log in again before retrying this request.")){
                                Toast.makeText(EditAccountActivity.this, "Email update failed. Please log in again before retrying this request.", Toast.LENGTH_SHORT).show();
                                pwdHelpText2.setVisibility(View.VISIBLE);// display help text
                            } else {
                                Toast.makeText(EditAccountActivity.this, "Password update failed. Please try again...", Toast.LENGTH_SHORT).show();
                            }
                            idPBLoading.setVisibility(View.GONE);// hide the progress bar
                        }
                    });
                });
                // Set the No button action
                builder2.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();// If user click no then dialog box is canceled and closed
                    idPBLoading.setVisibility(View.GONE);// hide the progress bar
                });
                // Create Alert dialog
                AlertDialog alertDialog = builder2.create();
                // Show Alert Dialog box
                alertDialog.show();
            }
        });

        // delete account button functionality
        idBtnDeleteAccount.setOnClickListener(view -> {
            //@Reference - https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
            //Reference description - tutorial on how to Create an Alert Dialog Box in Android
            // Create AlertDialog Builder class object
            AlertDialog.Builder builder = new AlertDialog.Builder(EditAccountActivity.this);
            // Set the alert message
            builder.setMessage("Do you want to delete your account? This action cannot be undone. " +
                    "Any public recipes you own will still be visible to other users.");
            // Set Alert Title
            builder.setTitle("Delete Account Confirmation");
            // set cancelable by clicking outside dialog to false
            builder.setCancelable(false);
            // Set the Yes button action
            builder.setPositiveButton("Yes", (dialog, which) -> {
                idPBLoading.setVisibility(View.VISIBLE);// show loading bar
                deleteHelpText.setVisibility(View.GONE);// hide delete help text
                // @Reference - https://www.youtube.com/watch?v=FptELNWvnqQ
                // Ref Description - Tutorial on how to delete Firebase Authentication users
                // and on how to delete users
                user.delete().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){// if task is successful
                        // display the following toast notification
                        Toast.makeText(EditAccountActivity.this, "Account Successfully Deleted", Toast.LENGTH_SHORT).show();
                        idPBLoading.setVisibility(View.GONE);// hide loading bar
                        Intent i = new Intent(EditAccountActivity.this, LoginActivity.class);
                        startActivity(i);//navigate to login page
                        finish();// finish the activity
                    // if the action is unsuccessful then execute the following
                    }else if(Objects.requireNonNull(task.getException()).toString().contains("This " +
                            "operation is sensitive and requires recent authentication. Log in " +
                            "again before retrying this request.")){
                        Toast.makeText(EditAccountActivity.this, "Account deletion " +
                                "failed. Please log in again before retrying this request.",
                                Toast.LENGTH_SHORT).show();
                        deleteHelpText.setVisibility(View.VISIBLE);// display delete help text
                        idPBLoading.setVisibility(View.GONE);// hide loading bar
                    } else{
                        Toast.makeText(EditAccountActivity.this, "Account Deletion " +
                                "Failed", Toast.LENGTH_SHORT).show();
                        idPBLoading.setVisibility(View.GONE);// hide loading bar
                    }
                });
            });
            // Set the No button action
            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.cancel();// If user click no then dialog box is canceled and closed
                idPBLoading.setVisibility(View.GONE);// hide the progress loading bar
            });
            // Create Alert dialog
            AlertDialog alertDialog = builder.create();
            // Show Alert Dialog box
            alertDialog.show();
        });
    }
}
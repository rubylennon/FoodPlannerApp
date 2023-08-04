package com.example.foodplannerapp.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 30th July 2023
 * BaseMenuActivity.java
 * Description - Toolbar Menu Activity for adding toolbar settings menu extended my most Activities
 */

// imports
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodplannerapp.R;
import com.google.firebase.auth.FirebaseAuth;

//@Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
//Reference description - tutorial on how to create settings menu
public class BaseMenuActivity extends AppCompatActivity {
    // declare variables
    private FirebaseAuth firebaseAuth;

    // menu onCreateOptionsMenu
    public boolean onCreateOptionsMenu(Menu menu) {
        // assign current Firebase Authentication instance
        firebaseAuth = FirebaseAuth.getInstance();
        // inflate menu using settings_menu layout
        getMenuInflater().inflate(R.menu.settings_main,menu);
        return true;
    }

    // on options selected functionality handler
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        // get the selected menu item ID
        int id = item.getItemId();
        switch (id) {// case switch for defining menu item click actions
            case R.id.idAddRecipe:// Add Recipe option clicked
                Intent i1 = new Intent(BaseMenuActivity.this, AddRecipeActivity.class);
                startActivity(i1);// start Add Recipe activity
                return true;
            case R.id.idMyRecipes:// My Recipes option clicked
                Intent i2 = new Intent(BaseMenuActivity.this, MainActivity.class);
                startActivity(i2);// start Main (My Recipes) activity
                return true;
            case R.id.idPublicRecipes:// Public Recipes option clicked
                Intent i3 = new Intent(BaseMenuActivity.this, PublicRecipesActivity.class);
                startActivity(i3);// start Public Recipe activity
                return true;
            case R.id.idScan:// Ingredients Scanner option clicked
                Intent i4 = new Intent(BaseMenuActivity.this, IngredientsScannerActivity.class);
                startActivity(i4);// start Ingredients Scanner activity
                return true;
            case R.id.idSearch:// Recipe Search option clicked
                Intent i5 = new Intent(BaseMenuActivity.this, RecipeSearchActivity.class);
                startActivity(i5);// start Recipe Search activity
                return true;
            case R.id.idMealPlan:// Meal Planner option clicked
                Intent i6 = new Intent(BaseMenuActivity.this, MealPlanActivity.class);
                startActivity(i6);// start Meal Planner activity
                return true;
            case R.id.idEditAccount:// Edit Account option clicked
                Intent i7 = new Intent(BaseMenuActivity.this, EditAccountActivity.class);
                startActivity(i7);
                return true;
            case R.id.idLogout:// Log Out option clicked
                Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();// sign out currently logged in user
                Intent i8 = new Intent(BaseMenuActivity.this, LoginActivity.class);
                startActivity(i8);// start Login activity
                this.finish();// finish current activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

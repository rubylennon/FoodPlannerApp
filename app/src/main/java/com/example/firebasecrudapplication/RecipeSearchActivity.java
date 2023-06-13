package com.example.firebasecrudapplication;

/*
 * @Author: Ruby Lennon (x19128355)
 * 13th June 2023
 * RecipeSearchActivity.java
 * Description - Recipe Search Activity of Java Android App 'FoodPlannerApp'
 */

// @REF 1: Search Bar + RecyclerView+Firebase Realtime Database easy Steps - https://www.youtube.com/watch?v=PmqYd-AdmC0

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecipeSearchActivity extends AppCompatActivity {

    private ProgressBar loadingPB;
    private DatabaseReference recipesDBRef;
    private ArrayList<Recipe> recipesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the actionbar title to Recipes
        setTitle("Recipe Search");

        // set activity_scan_ingredients as activity layout
        setContentView(R.layout.activity_recipe_search);

        // set firebase database reference to 'Ingredients' data
        recipesDBRef = FirebaseDatabase.getInstance().getReference("Recipes");

        // find layout elements by id and assign to variables
        loadingPB = findViewById(R.id.idPBLoading);

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);

    }

    protected void onStart() {

        super.onStart();

        // if the ingredients database reference is not null
        if(recipesDBRef != null){
            // create ingredients db reference value events listener
            recipesDBRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        recipesList = new ArrayList<>();
                        // store db recipes to arraylist
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            recipesList.add(ds.getValue(Recipe.class));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(RecipeSearchActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // print all recipes retrieved from database
        for(Recipe object : recipesList){
            Log.d("RECIPE", object.toString());
        }

    }

}

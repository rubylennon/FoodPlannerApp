package com.example.firebasecrudapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddRecipeActivity extends AppCompatActivity {
    // variables
    private TextInputEditText recipeNameEdt,
            recipeCookingTimeEdt,
            recipeSuitedForEdt,
            recipeImgEdt,
            recipeLinkEdt,
            recipeDescEdt,
            recipeMethodEdt,
            recipeIngredientsEdt;
    private Button addRecipeBtn;
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String recipeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // initialise variables
        recipeNameEdt = findViewById(R.id.idEdtRecipeName);
        recipeCookingTimeEdt = findViewById(R.id.idEdtRecipeCookingTime);
        recipeSuitedForEdt = findViewById(R.id.idEdtRecipeSuitedFor);
        recipeImgEdt = findViewById(R.id.idEdtRecipeImageLink);
        recipeLinkEdt = findViewById(R.id.idEdtRecipeLink);
        recipeDescEdt = findViewById(R.id.idEdtRecipeDesc);
        recipeMethodEdt = findViewById(R.id.idEdtRecipeMethod);
        recipeIngredientsEdt = findViewById(R.id.idEdtRecipeIngredients);
        addRecipeBtn = findViewById(R.id.idBtnAddRecipe);
        loadingPB = findViewById(R.id.idPBLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Recipes");

        // add recipe button click event action
        addRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get user input text from fields
                loadingPB.setVisibility(View.VISIBLE);
                String recipeName = recipeNameEdt.getText().toString();
                String recipeCookingTime = recipeCookingTimeEdt.getText().toString();
                String recipeSuitedFor = recipeSuitedForEdt.getText().toString();
                String recipeImg = recipeImgEdt.getText().toString();
                String recipeLink = recipeLinkEdt.getText().toString();
                String recipeDesc = recipeDescEdt.getText().toString();
                String recipeMethod = recipeMethodEdt.getText().toString();
                String recipeIngredients = recipeIngredientsEdt.getText().toString();
                recipeID = recipeName;
                RecipeRVModal recipeRVModal = new RecipeRVModal(recipeName,recipeCookingTime,recipeSuitedFor,recipeImg,recipeLink,recipeDesc,recipeMethod,recipeIngredients,recipeID);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loadingPB.setVisibility(View.GONE);
                        databaseReference.child(recipeID).setValue(recipeRVModal);
                        Toast.makeText(AddRecipeActivity.this, "Recipe Added", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddRecipeActivity.this, MainActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddRecipeActivity.this, "Error is " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
package com.example.firebasecrudapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ViewRecipeActivity extends AppCompatActivity {
    private TextInputEditText recipeNameEdt,
            recipeCookingTimeEdt,
            recipeSuitedForEdt,
            recipeDescEdt,
            recipeMethodEdt,
            recipeIngredientsEdt;
    private Button viewSourceRecipe;
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String recipeID;
    private RecipeRVModal recipeRVModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        // initialise variables
        firebaseDatabase = FirebaseDatabase.getInstance();
        recipeNameEdt = findViewById(R.id.idEdtRecipeName);
        recipeCookingTimeEdt = findViewById(R.id.idEdtRecipeCookingTime);
        recipeSuitedForEdt = findViewById(R.id.idEdtRecipeSuitedFor);
        recipeDescEdt = findViewById(R.id.idEdtRecipeDesc);
        recipeMethodEdt = findViewById(R.id.idEdtRecipeMethod);
        recipeIngredientsEdt = findViewById(R.id.idEdtRecipeIngredients);
        viewSourceRecipe = findViewById(R.id.idBtnViewSourceRecipe);
        loadingPB = findViewById(R.id.idPBLoading);
        recipeRVModal = getIntent().getParcelableExtra("recipe");

        if (recipeRVModal != null){
            recipeNameEdt.setText(recipeRVModal.getRecipeName());
            recipeCookingTimeEdt.setText(recipeRVModal.getRecipeCookingTime());
            recipeSuitedForEdt.setText(recipeRVModal.getRecipeSuitedFor());
            recipeDescEdt.setText(recipeRVModal.getRecipeDescription());
            recipeMethodEdt.setText(recipeRVModal.getRecipeMethod());
            recipeIngredientsEdt.setText(recipeRVModal.getRecipeIngredients());
            recipeID = recipeRVModal.getRecipeID();
        }

        databaseReference = firebaseDatabase.getReference("Recipes").child(recipeID);

        viewSourceRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(recipeRVModal.getRecipeLink()));
                startActivity(i);
            }
        });
    }
}
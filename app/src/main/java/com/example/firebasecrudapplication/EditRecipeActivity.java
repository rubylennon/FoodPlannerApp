package com.example.firebasecrudapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class EditRecipeActivity extends AppCompatActivity {
    private TextInputEditText recipeNameEdt,
            recipeCookingTimeEdt,
            recipeSuitedForEdt,
            recipeImgEdt,
            recipeLinkEdt,
            recipeDescEdt,
            recipeMethodEdt,
            recipeIngredientsEdt;
    private Button updateRecipeBtn,
            deleteRecipeBtn;
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String recipeID;
    private RecipeRVModal recipeRVModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        // initialise variables
        firebaseDatabase = FirebaseDatabase.getInstance();
        recipeNameEdt = findViewById(R.id.idEdtRecipeName);
        recipeCookingTimeEdt = findViewById(R.id.idEdtRecipeCookingTime);
        recipeSuitedForEdt = findViewById(R.id.idEdtRecipeSuitedFor);
        recipeImgEdt = findViewById(R.id.idEdtRecipeImageLink);
        recipeLinkEdt = findViewById(R.id.idEdtRecipeLink);
        recipeDescEdt = findViewById(R.id.idEdtRecipeDesc);
        recipeMethodEdt = findViewById(R.id.idEdtRecipeMethod);
        recipeIngredientsEdt = findViewById(R.id.idEdtRecipeIngredients);
        updateRecipeBtn = findViewById(R.id.idBtnUpdateRecipe);
        deleteRecipeBtn = findViewById(R.id.idBtnDeleteRecipe);
        loadingPB = findViewById(R.id.idPBLoading);
        recipeRVModal = getIntent().getParcelableExtra("recipe");

        if (recipeRVModal != null){
            recipeNameEdt.setText(recipeRVModal.getRecipeName());
            recipeCookingTimeEdt.setText(recipeRVModal.getRecipeCookingTime());
            recipeSuitedForEdt.setText(recipeRVModal.getRecipeSuitedFor());
            recipeImgEdt.setText(recipeRVModal.getRecipeImg());
            recipeLinkEdt.setText(recipeRVModal.getRecipeLink());
            recipeDescEdt.setText(recipeRVModal.getRecipeDescription());
            recipeMethodEdt.setText(recipeRVModal.getRecipeMethod());
            recipeIngredientsEdt.setText(recipeRVModal.getRecipeIngredients());
            recipeID = recipeRVModal.getRecipeID();
        }

        databaseReference = firebaseDatabase.getReference("Recipes").child(recipeID);

        updateRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);
                String recipeName = recipeNameEdt.getText().toString();
                String recipeCookingTime = recipeCookingTimeEdt.getText().toString();
                String recipeSuitedFor = recipeSuitedForEdt.getText().toString();
                String recipeImg = recipeImgEdt.getText().toString();
                String recipeLink = recipeLinkEdt.getText().toString();
                String recipeDesc = recipeDescEdt.getText().toString();
                String recipeMethod = recipeMethodEdt.getText().toString();
                String recipeIngredients = recipeIngredientsEdt.getText().toString();

                Map<String,Object> map = new HashMap<>();
                map.put("recipeName",recipeName);
                map.put("recipeCookingTime",recipeCookingTime);
                map.put("recipeSuitedFor",recipeSuitedFor);
                map.put("recipeImg",recipeImg);
                map.put("recipeLink",recipeLink);
                map.put("recipeDescription",recipeDesc);
                map.put("recipeMethod",recipeMethod);
                map.put("recipeIngredients",recipeIngredients);
                map.put("recipeID",recipeID);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loadingPB.setVisibility(View.GONE);
                        databaseReference.updateChildren(map);
                        Toast.makeText(EditRecipeActivity.this, "Course Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditRecipeActivity.this, MainActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditRecipeActivity.this, "Failed to update recipe", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        deleteRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecipe();
            }
        });
    }

    private void deleteRecipe(){
        databaseReference.removeValue();
        Toast.makeText(this, "Recipe Deleted", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(EditRecipeActivity.this, MainActivity.class));

    }
}
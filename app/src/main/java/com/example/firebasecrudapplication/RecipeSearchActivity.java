/*
 * @Author: Ruby Lennon (x19128355)
 * 13th June 2023
 * RecipeSearchActivity.java
 * Description - Recipe Search Activity of Java Android App 'FoodPlannerApp'
 */

// @REF: Custom Alert dialog plus send data to activity - https://www.youtube.com/watch?v=qCcsE1_yTCI

package com.example.firebasecrudapplication;

// imports

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeSearchActivity extends AppCompatActivity implements RecipeRVAdapter.RecipeClickInterface {
    private ProgressBar loadingPB;
    // RECIPE VARIABLES START
    private ArrayList<RecipeRVModal> recipesList;
    private DatabaseReference databaseReferenceRecipes;
    private RelativeLayout bottomSheetRL;
    private RecipeRVAdapter recipeRVAdapter;
    // RECIPE VARIABLES END

    // INGREDIENTS VARIABLES START
    private ArrayList<Ingredient> ingredientsList;
    private DatabaseReference ingredientsDBRef;
    private IngredientRVAdapter ingredientRVAdapter;
    private RecyclerView ingredientsRV;

    // INGREDIENTS VARIABLES END

//    private TextView tvTest;
//    private AlertDialog dialog;
//    private AlertDialog dialog2;
//    private TestIngredientListAdapter testIngredientListAdapter;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_recipes);

        // set the actionbar title to Recipes
        setTitle("Recipe Search");

        // SHARE CODE START
        // declare variables
        loadingPB = findViewById(R.id.idPBLoading);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        // SHARE CODE END

        // RECIPES CODE START
        // declare variables
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        databaseReferenceRecipes = firebaseDatabase.getReference("Recipes");
        recipesList = new ArrayList<>();
        bottomSheetRL = findViewById(R.id.idRLBSheet);

        // recipe RV adapter config
        recipeRVAdapter = new RecipeRVAdapter(recipesList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);

        // retrieve and display recipes
        getAllRecipes();

        // RECIPES CODE END

        // INGREDIENTS CODE START
        // declare variables
        ingredientsDBRef = FirebaseDatabase.getInstance().getReference().child("Ingredients");
        ingredientsRV = findViewById(R.id.idRVIngredients);

        // ingredient RV adapter config
//        ingredientRVAdapter = new IngredientRVAdapter(ingredientsList);
//        ingredientsRV.setLayoutManager(new LinearLayoutManager(this));
//        ingredientsRV.setAdapter(ingredientRVAdapter);

        Button mFilterButton = findViewById(R.id.BtnFilter);

        // retrieve and display recipes
        // getAllIngredients();

        // INGREDIENTS CODE END

//        // FILTER BUTTON 1 START
//
//        tvTest = findViewById(R.id.TvTest);
//        Button filter = findViewById(R.id.BtnFilter);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Enter Text");
//
//        //Inflate the custom_dialog view
//        View view = getLayoutInflater().inflate(R.layout.custom_dialog, null);
//        EditText eTest = view.findViewById(R.id.testInput);
//        Button submit = view.findViewById(R.id.submit);
//        submit.setOnClickListener(v -> {
//            tvTest.setText("Name : " + eTest.getText().toString());
//            dialog.dismiss();
//        });
//
//        // set this view to dialog
//        builder.setView(view);
//
//        // create dialog now
//        dialog = builder.create();
//
//        filter.setOnClickListener(v -> dialog.show());
//
//        // FILTER BUTTON 1 END
//
//        // FILTER BUTTON 2 START
//
//        Button filter2 = findViewById(R.id.BtnFilter2);
//        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
//        builder2.setTitle("Filter Recipe");
//
//        //Inflate the custom_dialog view
//        View view2 = getLayoutInflater().inflate(R.layout.custom_dialog_2, null);
//        Button submit2 = view2.findViewById(R.id.submit2);
//        CheckBox testIngredientsCB = view2.findViewById(R.id.checkbox);
//
//        submit2.setOnClickListener(v -> {
//            Log.d("checkbox", String.valueOf(testIngredientsCB));
//            dialog2.dismiss();
//        });
//
//        // set this view to dialog
//        builder2.setView(view2);
//
//        // create dialog now
//        dialog2 = builder2.create();
//
//        filter2.setOnClickListener(v -> dialog2.show());
//
//        RecyclerView testIngredientsRV = view2.findViewById(R.id.recyclerView);
//
//        testIngredientListAdapter = new TestIngredientListAdapter(ingredientsArrayList);
//        testIngredientsRV.setLayoutManager(new LinearLayoutManager(this));
//        testIngredientsRV.setAdapter(testIngredientListAdapter);
//
//        // FILTER BUTTON 2 END

        // show filter alert dialog window
        mFilterButton.setOnClickListener(v -> {
            showDialog();

            storeRecipesInArrayList();
        });

    }

    protected void onStart() {

        super.onStart();

        // if the ingredients database reference is not null
        if(ingredientsDBRef != null){
            // create ingredients db reference value events listener
            ingredientsDBRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        ingredientsList = new ArrayList<>();
                        // store db ingredients to arraylist
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            ingredientsList.add(ds.getValue(Ingredient.class));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(RecipeSearchActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        // SEARCH CODE END
    }

    private void storeRecipesInArrayList(){
        // add all ingredients from database to arraylist

        // add all ingredients from database to arraylist
        ArrayList<Ingredient> allIngredientsList = new ArrayList<>(ingredientsList);

        for(Ingredient object : allIngredientsList){
            Log.d("OBJECT", object.getIngredientName());
        }

        // update the ingredients recyclerview with matching ingredients
        RecipeSearchRVAdapter recipeSearchRVAdapter = new RecipeSearchRVAdapter(allIngredientsList);
        ingredientsRV.setLayoutManager(new LinearLayoutManager(RecipeSearchActivity.this));
        ingredientsRV.setAdapter(recipeSearchRVAdapter);

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);
    }

    private void getAllRecipes() {

        recipesList.clear();
        databaseReferenceRecipes.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                recipesList.add(snapshot.getValue(RecipeRVModal.class));

                recipeRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                recipeRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                loadingPB.setVisibility(View.GONE);
                recipeRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                recipeRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showDialog(){
        final CharSequence[] items = {" A "," B "," C "," D "};

//        final ArrayList items2 = new ArrayList();
//
//        items2.add("test");
//        items2.add("test2");
//
//        // String[] array = ingredientsList.toArray(new String[0]);

        final ArrayList selectedItems=new ArrayList();
//
//        int ingredientListSize = ingredientsList.size();
//
//        String[] arr = new String[ingredientListSize];
//        for(int i=0 ; i< ingredientsList.size();i++){
//            arr[i] = ingredientsList.get(i).getIngredientName();
//            Log.d("test arr item", arr[i]);
//            Log.d("test ingredientsArrayList item", ingredientsList.get(i).getIngredientName());
//            //getProductName or any suitable method
//        }

        AlertDialog dialog3 = new AlertDialog.Builder(this)
        .setTitle("Select One")
        .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                if (isChecked) {
                    selectedItems.add(indexSelected);
                } else if (selectedItems.contains(indexSelected)) {
                    selectedItems.remove(Integer.valueOf(indexSelected));
                }
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //  Your code when user clicked on OK
                Log.d("selectedCheckbox", selectedItems.toString());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //todo
            }
        }).create();
    dialog3.show();

    }

    private void searchIngredients(ArrayList<String> scannedIngredientsList){
        // create arraylist for storing matching ingredients (scanned & db stored ingredients)
        ArrayList<Ingredient> matchingIngredientsList = new ArrayList<>();

        // add all ingredients from database to arraylist
        ArrayList<Ingredient> allIngredientsList = new ArrayList<>(ingredientsList);

        // compare ingredients list and if they match add ingredient to matching arraylist
        for(Ingredient object : allIngredientsList){
            if(scannedIngredientsList.toString().toLowerCase().contains(object.getIngredientName().toLowerCase())){
                matchingIngredientsList.add(object);
            }
        }

        // update the ingredients recyclerview with matching ingredients
        IngredientScannerRVAdapter ingredientScannerRVAdapter = new IngredientScannerRVAdapter(matchingIngredientsList);
        ingredientsRV.setLayoutManager(new LinearLayoutManager(RecipeSearchActivity.this));
        ingredientsRV.setAdapter(ingredientScannerRVAdapter);

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);
    }

    private void getAllIngredients() {

        ingredientsList.clear();
        ingredientsDBRef.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                ingredientsList.add(snapshot.getValue(Ingredient.class));
                ingredientRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                ingredientRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                loadingPB.setVisibility(View.GONE);
                ingredientRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                ingredientRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onRecipeClick(int position) {
        displayBottomSheet(recipesList.get(position));
    }

    @SuppressLint("SetTextI18n")
    private void displayBottomSheet(RecipeRVModal recipeRVModal){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog,bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        TextView recipeNameTV = layout.findViewById(R.id.idTVRecipeName);
        TextView recipeDescTV = layout.findViewById(R.id.idTVDescription);
        TextView recipeSuitedForTV = layout.findViewById(R.id.idTVSuitedFor);
        TextView recipeCookingTimeTV = layout.findViewById(R.id.idTVCookingTime);
        ImageView recipeIV = layout.findViewById(R.id.idIVRecipe);
        Button editBtn = layout.findViewById(R.id.idBtnEdit);
        Button viewDetailsBtn = layout.findViewById(R.id.idBtnViewDetails);

        recipeNameTV.setText(recipeRVModal.getRecipeName());
        recipeDescTV.setText(recipeRVModal.getRecipeDescription());
        recipeSuitedForTV.setText("Suitable For: " + recipeRVModal.getRecipeSuitedFor());
        recipeCookingTimeTV.setText("Cooking Time: " + recipeRVModal.getRecipeCookingTime());
        Picasso.get().load(recipeRVModal.getRecipeImg()).into(recipeIV);

        editBtn.setOnClickListener(v -> {
            Intent i = new Intent(RecipeSearchActivity.this, EditRecipeActivity.class);
            i.putExtra("recipe", recipeRVModal);
            startActivity(i);
        });

        viewDetailsBtn.setOnClickListener(v -> {
            Intent i = new Intent(RecipeSearchActivity.this, ViewRecipeActivity.class);
            i.putExtra("recipe", recipeRVModal);
            startActivity(i);
        });

    }
}
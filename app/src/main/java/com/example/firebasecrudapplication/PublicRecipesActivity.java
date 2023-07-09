/*
 * @Author: Ruby Lennon (x19128355)
 * 27th February 2023
 * MainActivity.java
 * Description - Main Activity of Java Android App 'FoodPlannerApp'
 */

// @REF: GeeksForGeeks Tutorial - https://www.youtube.com/watch?v=-Gvpf8tXpbc
// Ref Description - User Authentication and CRUD Operation with Firebase Realtime Database in Android

package com.example.firebasecrudapplication;

// imports

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PublicRecipesActivity extends AppCompatActivity implements RecipeRVAdapter.RecipeClickInterface {
    // declare variables
    private RecyclerView recipeRV;
    private ProgressBar loadingPB;
    private ArrayList<RecipeRVModal> recipeRVModalArrayList;
    private RelativeLayout bottomSheetRL;
    private RecipeRVAdapter recipeRVAdapter;
    private FirebaseAuth mAuth;
    private Query query;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_activity);

        // set the actionbar title to Recipes
        setTitle("Public Recipes");

        recipeRV = findViewById(R.id.idRVRecipes);
        loadingPB = findViewById(R.id.idPBLoading);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Recipes");
        query = databaseReference.orderByChild("recipePublic").equalTo(true);
        recipeRVModalArrayList = new ArrayList<>();
        bottomSheetRL = findViewById(R.id.idRLBSheet);
        mAuth = FirebaseAuth.getInstance();
        recipeRVAdapter = new RecipeRVAdapter(recipeRVModalArrayList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(recipeRVAdapter);

        getAllPublicRecipes();

    }

    private void getAllPublicRecipes() {

        recipeRVModalArrayList.clear();
//        databaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                loadingPB.setVisibility(View.GONE);
//                recipeRVModalArrayList.add(snapshot.getValue(RecipeRVModal.class));
//                recipeRVAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                loadingPB.setVisibility(View.GONE);
//                recipeRVAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                loadingPB.setVisibility(View.GONE);
//                recipeRVAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                loadingPB.setVisibility(View.GONE);
//                recipeRVAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadingPB.setVisibility(View.GONE);

                if (dataSnapshot.exists()) {
                    Log.d("snapshot2", String.valueOf(dataSnapshot));
//                    mealPlanRVModalArrayList.add(dataSnapshot.getValue(MealPlanRVModal.class));
//                    mealPlanRVAdapter.notifyDataSetChanged();
                    //sortDates();

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do with your result
                        Log.d("issue", String.valueOf(issue));
                        recipeRVModalArrayList.add(issue.getValue(RecipeRVModal.class));
                        recipeRVAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onRecipeClick(int position) {
        displayBottomSheet(recipeRVModalArrayList.get(position));
    }

    @SuppressLint("SetTextI18n")
    private void displayBottomSheet(RecipeRVModal recipeRVModal){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog_without_edit,bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        TextView recipeNameTV = layout.findViewById(R.id.idTVRecipeName);
        TextView recipeDescTV = layout.findViewById(R.id.idTVDescription);
        TextView recipeSuitedForTV = layout.findViewById(R.id.idTVSuitedFor);
        TextView recipeCookingTimeTV = layout.findViewById(R.id.idTVCookingTime);
        ImageView recipeIV = layout.findViewById(R.id.idIVRecipe);
        Button viewDetailsBtn = layout.findViewById(R.id.idBtnViewDetails);

        recipeNameTV.setText(recipeRVModal.getRecipeName());
        recipeDescTV.setText(recipeRVModal.getRecipeDescription());
        recipeSuitedForTV.setText("Suitable For: " + recipeRVModal.getRecipeSuitedFor());
        recipeCookingTimeTV.setText("Cooking Time: " + recipeRVModal.getRecipeCookingTime());
        Picasso.get().load(recipeRVModal.getRecipeImg()).into(recipeIV);

        viewDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PublicRecipesActivity.this, ViewRecipeActivity.class);
                i.putExtra("recipe", recipeRVModal);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case R.id.idEditAccount:
                Intent i = new Intent(PublicRecipesActivity.this, EditAccountActivity.class);
                startActivity(i);
                return true;
            case R.id.idScan:
                Intent i2 = new Intent(PublicRecipesActivity.this, IngredientsScannerActivity.class);
                startActivity(i2);
                return true;
            case R.id.idSearch:
                Intent i3 = new Intent(PublicRecipesActivity.this, RecipeSearchActivity.class);
                startActivity(i3);
                return true;
            case R.id.idMealPlan:
                Intent i4 = new Intent(PublicRecipesActivity.this, MealPlanActivity.class);
                startActivity(i4);
                return true;
            case R.id.idLogout:
                Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent i5 = new Intent(PublicRecipesActivity.this, LoginActivity.class);
                startActivity(i5);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
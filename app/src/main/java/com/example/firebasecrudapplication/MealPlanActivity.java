package com.example.firebasecrudapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MealPlanActivity extends AppCompatActivity implements MealPlanRVAdapter.MealPlanClickInterface {
    private RecyclerView recipeRV;
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<MealPlanRVModal> mealPlanRVModalArrayList;
    private RelativeLayout bottomSheetRL;
    private MealPlanRVAdapter mealPlanRVAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);

        // set the actionbar title to Recipes
        setTitle("Meal Planner");

        recipeRV = findViewById(R.id.idRVRecipes);
        loadingPB = findViewById(R.id.idPBLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Meal Plans");
        mealPlanRVModalArrayList = new ArrayList<>();
        bottomSheetRL = findViewById(R.id.idRLBSheetMealPlan);
        mealPlanRVAdapter = new MealPlanRVAdapter(mealPlanRVModalArrayList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(mealPlanRVAdapter);

        getAllMealPlans();

    }

    private void getAllMealPlans() {

        mealPlanRVModalArrayList.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                mealPlanRVModalArrayList.add(snapshot.getValue(MealPlanRVModal.class));
                mealPlanRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                mealPlanRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                loadingPB.setVisibility(View.GONE);
                mealPlanRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                mealPlanRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMealPlanClick(int position) {
        displayBottomSheet(mealPlanRVModalArrayList.get(position));
    }

    @SuppressLint("SetTextI18n")
    private void displayBottomSheet(MealPlanRVModal mealPlanRVModal){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog_meal_plan,bottomSheetRL);
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

        recipeNameTV.setText(mealPlanRVModal.getRecipeName());
        recipeDescTV.setText(mealPlanRVModal.getRecipeDescription());
        recipeSuitedForTV.setText("Suitable For: " + mealPlanRVModal.getRecipeSuitedFor());
        recipeCookingTimeTV.setText("Cooking Time: " + mealPlanRVModal.getRecipeCookingTime());
        Picasso.get().load(mealPlanRVModal.getRecipeImg()).into(recipeIV);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MealPlanActivity.this, ViewMealPlanActivity.class);
                i.putExtra("meal plans", mealPlanRVModal);
                startActivity(i);
            }
        });

        viewDetailsBtn.setOnClickListener(v -> {
            Intent i = new Intent(MealPlanActivity.this, ViewMealPlanActivity.class);
            i.putExtra("MealPlan", mealPlanRVModal);
            startActivity(i);
        });

    }

}

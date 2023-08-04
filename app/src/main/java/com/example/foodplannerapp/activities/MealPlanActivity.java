package com.example.foodplannerapp.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 1 July 2023
 * MealPlanActivity.java
 * Description - Meal Plan list activity to view list of meals in meal plan
 */

// imports
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplannerapp.R;
import com.example.foodplannerapp.adapters.MealPlanRVAdapter;
import com.example.foodplannerapp.interfaces.SortMeals;
import com.example.foodplannerapp.models.Meal;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

// @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
// Reference description - tutorial on how to display retrieved data from Firebase realtime database in RecyclerView using adapter class
public class MealPlanActivity extends BaseMenuActivity implements MealPlanRVAdapter.MealPlanClickInterface {
    // declare variables
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference,
            databaseReferenceMealPlan;
    private Query query;
    private ArrayList<Meal> mealArrayList;
    private RelativeLayout bottomSheetRL;
    private MealPlanRVAdapter mealPlanRVAdapter;

    // activity onCreate method to be executed when activity is launched
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the activity layout
        setContentView(R.layout.activity_meal_plan);

        // set the actionbar title to Recipes
        setTitle("Meal Planner");

        // find layout elements by ID and assign to variables
        RecyclerView recipeRV = findViewById(R.id.idRVRecipes);
        loadingPB = findViewById(R.id.idPBLoading);
        bottomSheetRL = findViewById(R.id.idRLBSheetMealPlan);

        // get the currently signed in users ID from the current Firebase Auth instance
        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        // create Firebase database 'Recipes' reference
        firebaseDatabase = FirebaseDatabase.getInstance();
        // create Firebase database 'Meals' reference
        databaseReference = firebaseDatabase.getReference("Meal Plans");

        // initiate new meal plan arraylist
        mealArrayList = new ArrayList<>();

        // update the meal plan recycler view adapter to display meal plans
        mealPlanRVAdapter = new MealPlanRVAdapter(mealArrayList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(mealPlanRVAdapter);

        // set the query to filter the database reference to meals that match the logged in users ID
        query = databaseReference.orderByChild("userID").equalTo(userID);

        // get queried list from Firebase realtime database
        getQueriedList();

        // get all meal plans from Firebase Realtime database
        getAllMealPlans();
    }

    // get queried list from Firebase realtime database
    private void getQueriedList(){
        // clear the meal plan arraylist
        mealArrayList.clear();
        query.addListenerForSingleValueEvent(new ValueEventListener() {// query event listener
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingPB.setVisibility(View.GONE);// hide the loading bar
                // if a data snapshot is retrieved from database using query
                if (dataSnapshot.exists()) {
                    // for every data object in the snapshot execute the following
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        // add meal plan to arraylist
                        mealArrayList.add(ds.getValue(Meal.class));
                        mealPlanRVAdapter.notifyDataSetChanged();// notify the recycler view adapter the data has changed
                    }
                    // sort the meal plans by date (oldest to newest)
                    sortDates();
                }
            }
            // if there is an error complete the following
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // get all meal plans from Firebase Realtime database
    private void getAllMealPlans() {
        // clear the meal list array
        mealArrayList.clear();
        // database reference event listener
        databaseReference.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);// hide the loading bar
                mealPlanRVAdapter.notifyDataSetChanged();// notify the recycler view adapter the data has changed
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);// hide the loading bar
                mealPlanRVAdapter.notifyDataSetChanged();// notify the recycler view adapter the data has changed
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                loadingPB.setVisibility(View.GONE);// hide the loading bar
                mealPlanRVAdapter.notifyDataSetChanged();// notify the recycler view adapter the data has changed
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);// hide the loading bar
                mealPlanRVAdapter.notifyDataSetChanged();// notify the recycler view adapter the data has changed
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // @Reference - https://www.geeksforgeeks.org/java-program-to-sort-objects-in-arraylist-by-date/
    // Reference description - tutorial on how to sort an arraylist by date (descending order)
    // sort the meal plans by date (descending order)
    private void sortDates(){
        // sort meals using SortMeals() comparator class
        mealArrayList.sort(new SortMeals());
    }

    // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
    // Reference description - tutorial on how to display a bottom sheet dialog for a clicked item in Recycler View
    // if meal is clicked open the bottom sheet dialog for that meal
    @Override
    public void onMealPlanClick(int position) {
        displayBottomSheet(mealArrayList.get(position));
    }

    // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
    // Reference description - tutorial on how to display a bottom sheet dialog for a clicked item in Recycler View
    // meal bottom sheet dialog builder and functionality
    @SuppressLint("SetTextI18n")
    private void displayBottomSheet(Meal meal){
        // create a bottom sheet dialog object
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        // create a view object and set the layouts to be inflated
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog_meal_plan,bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        // get the bottom sheet dialog elements by ID and assign to local variables
        TextView recipeNameTV = layout.findViewById(R.id.idTVRecipeName);
        TextView recipeServesTV = layout.findViewById(R.id.idTVServes);
        TextView mealPlanDateTV = layout.findViewById(R.id.idTVDate);
        TextView recipeCookingTimeTV = layout.findViewById(R.id.idTVCookingTime);
        TextView recipeDescriptionTV = layout.findViewById(R.id.idTVDescription);
        TextView recipePrepTimeTV = layout.findViewById(R.id.idTVPreparationTime);
        ImageView recipeIV = layout.findViewById(R.id.idIVRecipe);
        Button deleteBtn = layout.findViewById(R.id.idBtnDelete);
        Button viewDetailsBtn = layout.findViewById(R.id.idBtnViewDetails);

        // set text in bottom sheet dialog using selected meal values
        recipeNameTV.setText(meal.getRecipeName());
        recipeServesTV.setText(meal.getRecipeServings());
        mealPlanDateTV.setText(meal.getDateShort());
        recipeCookingTimeTV.setText(meal.getRecipeCookingTime() + "m");
        recipePrepTimeTV.setText(meal.getRecipePrepTime() + "m");
        Picasso.get().load(meal.getRecipeImg()).into(recipeIV);

        // @Reference - https://developer.android.com/reference/android/text/style/StyleSpan
        // Reference description - Android guide/documentation on StyleSpan
        // @Reference 2 - https://developer.android.com/reference/android/text/SpannableString
        // Reference description - Android guide/documentation on Spannable
        // set meal description and use spannable to partially style the text view
        String DescriptionLabel = "Description: ";
        String Description = DescriptionLabel + meal.getRecipeDescription();
        SpannableString content = new SpannableString(Description);
        content.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0,
                DescriptionLabel.length(), 0);
        recipeDescriptionTV.setText(content);

        // get and store the meals ID
        String mealPlanID = meal.getMealPlanID();

        // get a reference to the meal using the meal ID
        databaseReferenceMealPlan = firebaseDatabase.getReference("Meal Plans").child(mealPlanID);

        // @Reference - https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
        // Reference description - tutorial on how to Create an Alert Dialog Box in Android
        // if the remove meal from meal plan button is clicked then execute the following
        deleteBtn.setOnClickListener(v -> {
            // Create an AlertDialog Builder class object
            AlertDialog.Builder builder = new AlertDialog.Builder(MealPlanActivity.this);
            // Set the alert message
            builder.setMessage("Do you want to delete this meal from the meal plan?");
            // Set Alert Title
            builder.setTitle("Delete Meal Confirmation");
            // set cancelable by clicking outside dialog to false
            builder.setCancelable(false);
            // set Yes button action
            builder.setPositiveButton("Yes", (dialog, which) -> {
                // delete the meal from the Firebase database using reference
                databaseReferenceMealPlan.removeValue();
                // When the user click yes button then alert will close
                dialog.dismiss();
                // show toast
                Toast.makeText(MealPlanActivity.this, "Meal Deleted", Toast.LENGTH_SHORT).show();
                // close the bottom sheet dialog
                bottomSheetDialog.cancel();
                // retrieve meals from database again
                getQueriedList();
            });
            // set No button action
            builder.setNegativeButton("No", (dialog, which) -> {
                // If user clicks no then dialog box is canceled
                dialog.cancel();
            });
            // Create Alert dialog
            AlertDialog alertDialog = builder.create();
            // Show Alert Dialog box
            alertDialog.show();
        });

        // if the bottom sheet dialog view details meal button is clicked then direct user to
        // view meal page and pass selected meal object
        viewDetailsBtn.setOnClickListener(v -> {
            Intent i = new Intent(MealPlanActivity.this, ViewMealPlanActivity.class);
            i.putExtra("MealPlan", meal);
            startActivity(i);// start View meal activity
        });
    }
}

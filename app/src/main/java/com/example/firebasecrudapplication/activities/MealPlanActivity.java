package com.example.firebasecrudapplication.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 1 July 2023
 * MealPlanActivity.java
 * Description - Meal Plan list activity to view list of meals in meal plan
 */

//@REF 1 - https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
//@REF 2 - https://www.geeksforgeeks.org/java-program-to-sort-objects-in-arraylist-by-date/
//@REF 3 - https://stackoverflow.com/questions/45359822/filter-data-in-firebase-functions

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import com.example.firebasecrudapplication.R;
import com.example.firebasecrudapplication.adapters.MealPlanRVAdapter;
import com.example.firebasecrudapplication.interfaces.SortMeals;
import com.example.firebasecrudapplication.models.Meal;
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

public class MealPlanActivity extends AppCompatActivity implements MealPlanRVAdapter.MealPlanClickInterface {
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference,
            databaseReferenceMealPlan;
    private Query query;
    private ArrayList<Meal> mealArrayList;
    private RelativeLayout bottomSheetRL;
    private MealPlanRVAdapter mealPlanRVAdapter;
    private FirebaseAuth mAuth;

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
        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Meal Plans");
        mAuth = FirebaseAuth.getInstance();
        bottomSheetRL = findViewById(R.id.idRLBSheetMealPlan);

        // initiate new meal plan arraylist
        mealArrayList = new ArrayList<>();

        // update the meal plan recycler view adapter to display meal plans
        mealPlanRVAdapter = new MealPlanRVAdapter(mealArrayList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(mealPlanRVAdapter);
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

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingPB.setVisibility(View.GONE);

                if (dataSnapshot.exists()) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        // add meal plan to arraylist
                        mealArrayList.add(ds.getValue(Meal.class));
                        mealPlanRVAdapter.notifyDataSetChanged();
                    }

                    // sort the meal plans by date (oldest to newest)
                    sortDates();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // get all meal plans from Firebase Realtime database
    private void getAllMealPlans() {

        mealArrayList.clear();

        databaseReference.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                mealPlanRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                mealPlanRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                loadingPB.setVisibility(View.GONE);
                mealPlanRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
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

    // sort the meal plans by date (ascending order)
    private void sortDates(){
        // sort meals using SortMeals() comparator class
        mealArrayList.sort(new SortMeals());
    }

    // if meal is clicked open the bottom sheet dialog for that meal
    @Override
    public void onMealPlanClick(int position) {
        displayBottomSheet(mealArrayList.get(position));
    }

    // meal bottom sheet dialog builder and functionality
    @SuppressLint("SetTextI18n")
    private void displayBottomSheet(Meal meal){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog_meal_plan,bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        TextView recipeNameTV = layout.findViewById(R.id.idTVRecipeName);
        TextView recipeServesTV = layout.findViewById(R.id.idTVServes);
        TextView mealPlanDateTV = layout.findViewById(R.id.idTVDate);
        TextView recipeCookingTimeTV = layout.findViewById(R.id.idTVCookingTime);
        TextView recipeDescriptionTV = layout.findViewById(R.id.idTVDescription);
        TextView recipePrepTimeTV = layout.findViewById(R.id.idTVPreparationTime);
        ImageView recipeIV = layout.findViewById(R.id.idIVRecipe);
        Button deleteBtn = layout.findViewById(R.id.idBtnDelete);
        Button viewDetailsBtn = layout.findViewById(R.id.idBtnViewDetails);

        recipeNameTV.setText(meal.getRecipeName());
        recipeServesTV.setText(meal.getRecipeServings());
        String s = getString(R.string.styled_welcome_message);
        // recipeDescriptionTV.setText(Html.fromHtml(s,Html.FROM_HTML_MODE_LEGACY) + meal.getRecipeDescription());
        String Description = "Description: " + meal.getRecipeDescription();
        SpannableString content = new SpannableString(Description);
        content.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, Description.length(), 0);

        recipeDescriptionTV.setText(content);

        mealPlanDateTV.setText(meal.getDateShort());
        recipeCookingTimeTV.setText(meal.getRecipeCookingTime() + "m");
        recipePrepTimeTV.setText(meal.getRecipePrepTime() + "m");
        Picasso.get().load(meal.getRecipeImg()).into(recipeIV);

        String mealPlanID = meal.getMealPlanID();

        databaseReferenceMealPlan = firebaseDatabase.getReference("Meal Plans").child(mealPlanID);

        // if the remove meal from meal plan button is clicked then execute the following
        deleteBtn.setOnClickListener(v -> {

            // Create the object of AlertDialog Builder class
            AlertDialog.Builder builder = new AlertDialog.Builder(MealPlanActivity.this);

            // Set the message show for the Alert time
            builder.setMessage("Do you want to delete this meal from the meal plan?");

            // Set Alert Title
            builder.setTitle("Delete Meal Confirmation");

            // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
            builder.setCancelable(false);

            // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
            builder.setPositiveButton("Yes", (dialog, which) -> {
                databaseReferenceMealPlan.removeValue();

                // When the user click yes button then app will close
                dialog.dismiss();

                Toast.makeText(MealPlanActivity.this, "Meal Deleted", Toast.LENGTH_SHORT).show();

                // close the bottom sheet dialog
                bottomSheetDialog.cancel();

                // retrieve meals from database again
                getQueriedList();

            });

            // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
            builder.setNegativeButton("No", (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();
            // Show the Alert Dialog box
            alertDialog.show();
        });

        // if the meal plan view details button is clicked then redirect user to view meal plan page
        viewDetailsBtn.setOnClickListener(v -> {
            Intent i = new Intent(MealPlanActivity.this, ViewMealPlanActivity.class);
            i.putExtra("MealPlan", meal);
            startActivity(i);
        });

    }

    // settings menu code start
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_main,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case R.id.idAddRecipe:
                Intent i1 = new Intent(MealPlanActivity.this, AddRecipeActivity.class);
                startActivity(i1);
                return true;
            case R.id.idMyRecipes:
                Intent i2 = new Intent(MealPlanActivity.this, MainActivity.class);
                startActivity(i2);
                return true;
            case R.id.idPublicRecipes:
                Intent i3 = new Intent(MealPlanActivity.this, PublicRecipesActivity.class);
                startActivity(i3);
                return true;
            case R.id.idScan:
                Intent i4 = new Intent(MealPlanActivity.this, IngredientsScannerActivity.class);
                startActivity(i4);
                return true;
            case R.id.idSearch:
                Intent i5 = new Intent(MealPlanActivity.this, RecipeSearchActivity.class);
                startActivity(i5);
                return true;
            case R.id.idMealPlan:
                Intent i6 = new Intent(MealPlanActivity.this, MealPlanActivity.class);
                startActivity(i6);
                return true;
            case R.id.idEditAccount:
                Intent i7 = new Intent(MealPlanActivity.this, EditAccountActivity.class);
                startActivity(i7);
                return true;
            case R.id.idLogout:
                Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent i8 = new Intent(MealPlanActivity.this, LoginActivity.class);
                startActivity(i8);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // settings menu code end

}

package com.example.firebasecrudapplication;

//@REF 1 - https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
//@REF 2: https://www.geeksforgeeks.org/java-program-to-sort-objects-in-arraylist-by-date/

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class MealPlanActivity extends AppCompatActivity implements MealPlanRVAdapter.MealPlanClickInterface {
    private RecyclerView recipeRV;
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceFiltered;
    private DatabaseReference databaseReferenceMealPlan;
    private Query query;
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
        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Meal Plans");

        mealPlanRVModalArrayList = new ArrayList<>();
        bottomSheetRL = findViewById(R.id.idRLBSheetMealPlan);
        mealPlanRVAdapter = new MealPlanRVAdapter(mealPlanRVModalArrayList, this, this);
        recipeRV.setLayoutManager(new LinearLayoutManager(this));
        recipeRV.setAdapter(mealPlanRVAdapter);

        query = databaseReference.orderByChild("userID").equalTo(userID);

        getQueriedList();

        //getAllMealPlans();



    }

    private void getQueriedList(){

        mealPlanRVModalArrayList.clear();
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
                        mealPlanRVModalArrayList.add(issue.getValue(MealPlanRVModal.class));
                        mealPlanRVAdapter.notifyDataSetChanged();
                    }

                    sortDates();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAllMealPlans() {

        mealPlanRVModalArrayList.clear();

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("snapshot1", String.valueOf(snapshot));

                loadingPB.setVisibility(View.GONE);
                // mealPlanRVModalArrayList.add(snapshot.getValue(MealPlanRVModal.class));
                mealPlanRVAdapter.notifyDataSetChanged();
                // sortDates();
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

        //mealPlanRVModalArrayList
    }

    private void sortDates(){
        for(MealPlanRVModal meal : mealPlanRVModalArrayList){
            Log.d("mealPlanRVModalArrayList unsorted", meal.getDate());
        }

        Collections.sort(mealPlanRVModalArrayList, new sortMeals());

        for(MealPlanRVModal meal : mealPlanRVModalArrayList){
            Log.d("mealPlanRVModalArrayList sorted", meal.getDate());
        }

//        DateTimeFormatter formatter = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            DateTimeFormatter finalFormatter = formatter;
//            mealPlanRVModalArrayList.sort(Comparator.comparing(s -> LocalDateTime.parse((CharSequence) s, finalFormatter)));
//        }
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
        Button deleteBtn = layout.findViewById(R.id.idBtnDelete);
        Button viewDetailsBtn = layout.findViewById(R.id.idBtnViewDetails);

        recipeNameTV.setText(mealPlanRVModal.getRecipeName());
        recipeDescTV.setText(mealPlanRVModal.getRecipeDescription());
        recipeSuitedForTV.setText("Suitable For: " + mealPlanRVModal.getRecipeSuitedFor());
        recipeCookingTimeTV.setText("Cooking Time: " + mealPlanRVModal.getRecipeCookingTime());
        Picasso.get().load(mealPlanRVModal.getRecipeImg()).into(recipeIV);

        String mealPlanID = mealPlanRVModal.getMealPlanID();

        databaseReferenceMealPlan = firebaseDatabase.getReference("Meal Plans").child(mealPlanID);

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
            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                databaseReferenceMealPlan.removeValue();

                // When the user click yes button then app will close
                dialog.dismiss();

                Toast.makeText(MealPlanActivity.this, "Meal Deleted", Toast.LENGTH_SHORT).show();

                bottomSheetDialog.cancel();

                getAllMealPlans();

            });

            // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
            builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();
            // Show the Alert Dialog box
            alertDialog.show();
        });

        viewDetailsBtn.setOnClickListener(v -> {
            Intent i = new Intent(MealPlanActivity.this, ViewMealPlanActivity.class);
            i.putExtra("MealPlan", mealPlanRVModal);
            startActivity(i);
        });

    }

}

package com.example.firebasecrudapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class ViewMealPlanActivity extends AppCompatActivity {
    private TextInputEditText recipeNameEdt,
            recipeCookingTimeEdt,
            recipeServingsEdt,
            recipeSuitedForEdt,
            recipeCuisineEdt,
            recipeDescEdt,
            recipeMethodEdt,
            recipeIngredientsEdt;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch recipePublicEdt;
    private Button viewSourceRecipe;
    private ProgressBar loadingPB;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceIngredients;
    private DatabaseReference ingredientsDBRef;
    private String recipeID;
    private String mealPlanID;
    private MealPlanRVModal mealPlanRVModal;
    private MealPlanIngredient mealPlanIngredient;
    private String[] ingredientsArray;
    private LinearLayout layout;
    AtomicReference<Boolean> initialLoad = new AtomicReference<>(true);
    private ArrayList<String> ingredientList = new ArrayList<>();
    private ArrayList<MealPlanIngredient> ingredientsList;
    private FirebaseAuth mAuth;

    public ViewMealPlanActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meal_plan);

        // set the actionbar title
        setTitle("Meal Details");

        // initialise variables
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        recipeNameEdt = findViewById(R.id.idEdtRecipeName);
        recipeCookingTimeEdt = findViewById(R.id.idEdtRecipeCookingTime);
        recipeServingsEdt = findViewById(R.id.idEdtRecipeServings);
        recipeSuitedForEdt = findViewById(R.id.idEdtRecipeSuitedFor);
        recipeCuisineEdt = findViewById(R.id.idEdtRecipeCuisine);
        recipeDescEdt = findViewById(R.id.idEdtRecipeDesc);
        recipeMethodEdt = findViewById(R.id.idEdtRecipeMethod);
        recipeIngredientsEdt = findViewById(R.id.idEdtRecipeIngredients);
        recipePublicEdt = findViewById(R.id.idPublicSwitch);
        viewSourceRecipe = findViewById(R.id.idBtnViewSourceRecipe);
        loadingPB = findViewById(R.id.idPBLoading);
        mealPlanRVModal = getIntent().getParcelableExtra("MealPlan");
        layout = findViewById(R.id.shopping_List_item);

        // populate the layout fields with the recipe details from the database
        if (mealPlanRVModal != null) {
            recipeNameEdt.setText(mealPlanRVModal.getRecipeName());
            recipeCookingTimeEdt.setText(mealPlanRVModal.getRecipeCookingTime());
            recipeServingsEdt.setText(mealPlanRVModal.getRecipeServings());
            recipeSuitedForEdt.setText(mealPlanRVModal.getRecipeSuitedFor());
            recipeCuisineEdt.setText(mealPlanRVModal.getRecipeCuisine());
            recipeDescEdt.setText(mealPlanRVModal.getRecipeDescription());
            recipeMethodEdt.setText(mealPlanRVModal.getRecipeMethod());
            recipeIngredientsEdt.setText(mealPlanRVModal.getRecipeIngredients());
            ingredientsArray = mealPlanRVModal.getRecipeIngredients().split(",");
            recipePublicEdt.setChecked(mealPlanRVModal.getRecipePublic().equals(true));
            recipeID = mealPlanRVModal.getRecipeID();
            mealPlanID = mealPlanRVModal.getMealPlanID();
        }

        // set input fields to non focusable
        recipeNameEdt.setFocusable(false);
        recipeCookingTimeEdt.setFocusable(false);
        recipeServingsEdt.setFocusable(false);
        recipeSuitedForEdt.setFocusable(false);
        recipeCuisineEdt.setFocusable(false);
        recipeDescEdt.setFocusable(false);
        recipeMethodEdt.setFocusable(false);
        recipeIngredientsEdt.setFocusable(false);

        // disable input fields
        recipeNameEdt.setEnabled(false);
        recipeCookingTimeEdt.setEnabled(false);
        recipeServingsEdt.setEnabled(false);
        recipeSuitedForEdt.setEnabled(false);
        recipeCuisineEdt.setEnabled(false);
        recipeDescEdt.setEnabled(false);
        recipeMethodEdt.setEnabled(false);
        recipeIngredientsEdt.setEnabled(false);

        // disable input fields cursor visibility
        recipeNameEdt.setCursorVisible(false);
        recipeCookingTimeEdt.setCursorVisible(false);
        recipeServingsEdt.setCursorVisible(false);
        recipeSuitedForEdt.setCursorVisible(false);
        recipeCuisineEdt.setCursorVisible(false);
        recipeDescEdt.setCursorVisible(false);
        recipeMethodEdt.setCursorVisible(false);
        recipeIngredientsEdt.setCursorVisible(false);

        // set switch button to non clickable
        recipePublicEdt.setClickable(false);

        // assign database reference to Recipes firebase realtime database reference
        databaseReference = firebaseDatabase.getReference("Meal Plans").child(mealPlanID);

        ingredientsDBRef = firebaseDatabase.getReference("Meal Plans").child(mealPlanID).child("ingredients");

//        // assign database reference to Recipes firebase realtime database reference
//        databaseReferenceIngredients = firebaseDatabase.getReference("Meal Plans");
//        databaseReferenceIngredients.child(mealPlanID).child("ingredients").child("Tomato").setValue(true);
//
//        databaseReferenceIngredients.child(mealPlanID).child("ingredients").child("Tomato").get();
//
//        Log.d("TEST", String.valueOf(databaseReferenceIngredients.child(mealPlanID).child("ingredients").child("Tomato").get()));

        // view recipe source page in browser using recipe link
//        viewSourceRecipe.setOnClickListener(v -> {
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(mealPlanRVModal.getRecipeLink()));
//            startActivity(i);
//        });

        // create ingredients db reference value events listener

//        for (MealPlanIngredient ingredient : ingredientsList) {
//            Log.d("ingredient", String.valueOf(ingredient));
//        }

        //    private void addCard(String name) {
//        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.ingredient_card_rounded, null);
//
//        // if the edit page is being loaded for the first time then add all previously added ingredients to selected list
//        if(editPageInitialLoad.get()){
//            ingredientsArray.add(name);
//        }
//
//        TextView nameView = view.findViewById(R.id.name);
//        Button delete = view.findViewById(R.id.delete);
//
//        nameView.setText(name);
//
//        // delete ingredient button action
//        delete.setOnClickListener(v -> {
//            ingredientsArray.remove(name);
//            Log.d("ingredientList", String.valueOf(ingredientsArray));
//            layout.removeView(view);
//        });
//
//        layout.addView(view);
//    }

        databaseReferenceIngredients = firebaseDatabase.getReference("Meal Plans").child(mealPlanID).child("ingredients");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(ingredientsDBRef != null){
            // create ingredients db reference value events listener
            ingredientsDBRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        ingredientsList = new ArrayList<>();
                        // store db ingredients to arraylist
                        for(DataSnapshot ds : dataSnapshot.getChildren()){

                            mealPlanIngredient = ds.getValue(MealPlanIngredient.class);
                            mealPlanIngredient.key = ds.getKey();
                            ingredientsList.add(mealPlanIngredient);
                            Log.d("ingredients", String.valueOf(ds));
                            Log.d("mealPlanIngredient.getKey()", mealPlanIngredient.getKey());
                            Log.d("mealPlanIngredient.getIngredient()", mealPlanIngredient.getIngredient());
                            Log.d("mealPlanIngredient.getPurchased()", mealPlanIngredient.getPurchased());
                        }

                        if(initialLoad.get()){
                            for(MealPlanIngredient ingredientListItem : ingredientsList){
                                addCardTest(ingredientListItem.getIngredient(), ingredientListItem.getPurchased(), ingredientListItem.getKey());
                            }
                        }

                        initialLoad.set(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewMealPlanActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        // SEARCH CODE END
    }


//    private void addCard(String name) {
//        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.shopping_list_item, null);
//
//        Log.d("addCard ingredientList", String.valueOf(ingredientList));
//
//        // if the edit page is being loaded for the first time then add all previously added ingredients to selected list
//        if(editPageInitialLoad.get()){
//            ingredientList.add(name);
//        }
//
//        TextView nameView = view.findViewById(R.id.name);
//        Button delete = view.findViewById(R.id.delete);
//        CheckBox checkBox = view.findViewById(R.id.checkBox);
//
//        nameView.setText(name);
//
//        // delete ingredient button action
//        delete.setOnClickListener(v -> {
//            ingredientList.remove(name);
//            Log.d("ingredientList", String.valueOf(ingredientList));
//            layout.removeView(view);
//        });
//
//        layout.addView(view);
//    }

    private void addCardTest(String ingredient, String purchased, String key) {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.shopping_list_item, null);

        TextView nameView = view.findViewById(R.id.name);
        CheckBox checkBox = view.findViewById(R.id.checkBox);

        nameView.setText(ingredient);

        if(purchased.equals("true")){
            checkBox.setChecked(true);
        }

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(checkBox.isChecked()){
                Toast.makeText(ViewMealPlanActivity.this, "Shopping List Updated", Toast.LENGTH_SHORT).show();
                Log.d("ingredient", ingredient);
                Log.d("purchased", purchased);
                Log.d("key", key);
                databaseReferenceIngredients.child(key).child("purchased").setValue("true");
            }else{
                Toast.makeText(ViewMealPlanActivity.this, "Shopping List Updated", Toast.LENGTH_SHORT).show();
                Log.d("ingredient", ingredient);
                Log.d("purchased", purchased);
                Log.d("key", key);
                databaseReferenceIngredients.child(key).child("purchased").setValue("false");
            }
        });

//        checkBox.setOnCheckedChangeListener(v -> {
//            if(checkBox.isChecked()){
//                Toast.makeText(this, "checkbox checked", Toast.LENGTH_SHORT).show();
//            }else{
//                Toast.makeText(this, "checkbox unchecked", Toast.LENGTH_SHORT).show();
//            }
//        });

        layout.addView(view);
    }

    // options menu code start
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case R.id.idEditAccount:
                Intent i = new Intent(ViewMealPlanActivity.this, EditAccountActivity.class);
                startActivity(i);
                return true;
            case R.id.idScan:
                Intent i2 = new Intent(ViewMealPlanActivity.this, IngredientsScannerActivity.class);
                startActivity(i2);
                return true;
            case R.id.idSearch:
                Intent i3 = new Intent(ViewMealPlanActivity.this, RecipeSearchActivity.class);
                startActivity(i3);
                return true;
            case R.id.idMealPlan:
                Intent i4 = new Intent(ViewMealPlanActivity.this, MealPlanActivity.class);
                startActivity(i4);
                return true;
            case R.id.idLogout:
                Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent i5 = new Intent(ViewMealPlanActivity.this, LoginActivity.class);
                startActivity(i5);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // options menu code end
}
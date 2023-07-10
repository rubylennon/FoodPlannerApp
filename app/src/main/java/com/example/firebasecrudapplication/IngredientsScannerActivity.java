package com.example.firebasecrudapplication;

/*
 * @Author: Ruby Lennon (x19128355)
 * 27th February 2023
 * IngredientsScannerActivity.java
 * Description - OCR Ingredients List Activity of Java Android App 'FoodPlannerApp'
 */

// @REF 1: select image from gallery and show it in ImageView - https://www.youtube.com/watch?v=i3-WL9Xv4hA
// @REF 2: Google MLKit Samples - https://github.com/googlesamples/mlkit/tree/master/android/vision-quickstart
// @REF 3: How to Capture Image And Display in ImageView in android Studio - https://www.youtube.com/watch?v=d7Nia9vKUDM
// @REF 4: Search Bar + RecyclerView+Firebase Realtime Database easy Steps - https://www.youtube.com/watch?v=PmqYd-AdmC0
// @REF 5: User Authentication and CRUD Operation with Firebase Realtime Database in Android | GeeksForGeeks - https://www.youtube.com/watch?v=-Gvpf8tXpbc

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IngredientsScannerActivity extends AppCompatActivity {
    private ImageView mImageView,
            noMatchingSearchResultsIcon;
    private Button mScanButton;
    private Bitmap mSelectedImage;
    private ProgressBar loadingPB;
    private DatabaseReference ingredientsDBRef;
    private ArrayList<Ingredient> ingredientsList;
    private RecyclerView ingredientsRV;
    private TextView noMatchingSearchTextOne,
            noMatchingSearchTextTwo;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the actionbar title to Recipes
        setTitle("Ingredients Scanner");

        // set activity_scan_ingredients as activity layout
        setContentView(R.layout.activity_scan_ingredients);

        // set firebase database reference to 'Ingredients' data
        ingredientsDBRef = FirebaseDatabase.getInstance().getReference().child("Ingredients");

        // find layout elements by id and assign to variables
        mImageView = findViewById(R.id.image_view);
        mScanButton = findViewById(R.id.detect_text);
        loadingPB = findViewById(R.id.idPBLoading);
        Button mCaptureButton = findViewById(R.id.capture_image);
        Button mSelectButton = findViewById(R.id.select_image);
        noMatchingSearchResultsIcon = findViewById(R.id.noSearchResultsIV);
        noMatchingSearchTextOne = findViewById(R.id.no_matching_results);
        noMatchingSearchTextTwo = findViewById(R.id.no_matching_results_help);
        mAuth = FirebaseAuth.getInstance();

        // hide the no ingredients alert
        hideNoIngredientsAlert();

        // set the recycler view to layout object
        ingredientsRV = findViewById(R.id.ingredientsRV);

        // disable scan ingredients button
        mScanButton.setEnabled(false);

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);

        // create on click listener for 'Scan Ingredients' button
        mScanButton.setOnClickListener(v -> {
            // show loading progress bar
            loadingPB.setVisibility(View.VISIBLE);

            // run text recognition method
            runTextRecognition();
        });

        // select image button event listener
        mSelectButton.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 1);
        });

        // take photo button event listener
        mCaptureButton.setOnClickListener(v -> {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, 2);
        });

        // request read external storage permission if it is not already available
        String[] permissionsStorage = {Manifest.permission.READ_EXTERNAL_STORAGE};
        int requestExternalStorage = 1;
        int externalStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (externalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionsStorage, requestExternalStorage);
        }

        // request camera permission if it is not already available
        String[] permissionsCamera = {Manifest.permission.CAMERA};
        int requestCamera = 1;
        int cameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionsCamera, requestCamera);
        }

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
                    Toast.makeText(IngredientsScannerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        // SEARCH CODE END
    }

    // method for searching for matching ingredients in database and displaying to user
    // accepts text from scanned image
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

        // if there are no matching ingredients show a notification on screen
        if(matchingIngredientsList.isEmpty()){
            showNoIngredientsAlert();
        }else{ // if there are matching ingredients in the database then execute the following
            // update the ingredients recyclerview with matching ingredients
            IngredientScannerRVAdapter ingredientScannerRVAdapter = new IngredientScannerRVAdapter(matchingIngredientsList);
            ingredientsRV.setLayoutManager(new LinearLayoutManager(IngredientsScannerActivity.this));
            ingredientsRV.setAdapter(ingredientScannerRVAdapter);
            hideNoIngredientsAlert();
        }

        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);
    }

    // method for running text recognition functionality
    private void runTextRecognition() {
        InputImage image = InputImage.fromBitmap(mSelectedImage, 0);
        TextRecognizer recognizer = TextRecognition.getClient();

        // disable scan ingredients button
        mScanButton.setEnabled(false);

        // text recognition functionality
        recognizer.process(image)
                .addOnSuccessListener(
                        texts -> {
                            // enable scan ingredients button
                            mScanButton.setEnabled(true);
                            // run method for processing text and displaying results
                            processTextRecognitionResult(texts);
                        })
                .addOnFailureListener(
                        e -> {
                            // enable scan ingredients button
                            mScanButton.setEnabled(true);
                            // Task failed with an exception
                            e.printStackTrace();
                        });
    }

    // method for processing text recognition result
    private void processTextRecognitionResult(Text texts) {
        // if no text is recognised execute the following
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            showToast();
            showNoIngredientsAlert();
            return;
        }

        // store the OCR text to variable
        String result_text = texts.getText();

        // update the stored OCR result text by formatting as below
        // remove new lines
        result_text = result_text.replaceAll("\\n", " ");
        // remove return carriages
        result_text = result_text.replaceAll("\\r", " ");
        // replace start bracket with comma
        result_text = result_text.replaceAll("\\[",",");
        // remove end brackets
        result_text = result_text.replaceAll("]","");

        // split OCR result string by comma to create an Array of Ingredients
        String[] ingredients_list = result_text.split("\\s*,\\s*");

        // add ingredients to string arraylist
        ArrayList<String> scannedIngredients = new ArrayList<>();
        Collections.addAll(scannedIngredients, ingredients_list);

        // pass the scanned ingredients to search method to find matching ingredients
        searchIngredients(scannedIngredients);

    }

    // if no ingredients are found in scanned image display the following toast
    private void showToast() {
        Toast.makeText(getApplicationContext(), "No text found", Toast.LENGTH_SHORT).show();
        // hide loading progress bar
        loadingPB.setVisibility(View.GONE);
    }

    // activity result handler including select and upload image functionality
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if activity result is 1 (Gallery Image Selection Activity)
        if (requestCode == 1 && resultCode == RESULT_OK && null != data){
            Uri selectedImage = data.getData();
            String[] filepath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filepath, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filepath[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // update the image view with the selected photo
            mImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            mImageView.buildDrawingCache();
            mSelectedImage = mImageView.getDrawingCache();

            // re-enable scan ingredients button
            mScanButton.setEnabled(true);

            clearIngredientsList();

        // if activity result is 1 (Capture Photo Activity)
        } else if (requestCode == 2 && resultCode == RESULT_OK && null != data){
            Bitmap capturedImage = (Bitmap)data.getExtras().get("data");

            // update the image view with the captured photo
            mImageView.setImageBitmap(capturedImage);
            mImageView.buildDrawingCache();
            mSelectedImage = mImageView.getDrawingCache();

            // re-enable scan ingredients button
            mScanButton.setEnabled(true);

            clearIngredientsList();
        }
    }

    public void clearIngredientsList(){
        hideNoIngredientsAlert();

        ArrayList<Ingredient> matchingIngredientsList = new ArrayList<>();

        // update the ingredients recyclerview with matching ingredients
        IngredientScannerRVAdapter ingredientScannerRVAdapter = new IngredientScannerRVAdapter(matchingIngredientsList);
        ingredientsRV.setLayoutManager(new LinearLayoutManager(IngredientsScannerActivity.this));
        ingredientsRV.setAdapter(ingredientScannerRVAdapter);
    }

    // hide the no ingredients notification from screen
    private void hideNoIngredientsAlert(){
        noMatchingSearchResultsIcon.setVisibility(View.GONE);
        noMatchingSearchTextOne.setVisibility(View.GONE);
        noMatchingSearchTextTwo.setVisibility(View.GONE);
    }

    // show the no ingredients notification from screen
    private void showNoIngredientsAlert(){
        noMatchingSearchResultsIcon.setVisibility(View.VISIBLE);
        noMatchingSearchTextOne.setVisibility(View.VISIBLE);
        noMatchingSearchTextTwo.setVisibility(View.VISIBLE);
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
                Intent i1 = new Intent(IngredientsScannerActivity.this, AddRecipeActivity.class);
                startActivity(i1);
                return true;
            case R.id.idMyRecipes:
                Intent i2 = new Intent(IngredientsScannerActivity.this, MainActivity.class);
                startActivity(i2);
                return true;
            case R.id.idPublicRecipes:
                Intent i3 = new Intent(IngredientsScannerActivity.this, PublicRecipesActivity.class);
                startActivity(i3);
                return true;
            case R.id.idScan:
                Intent i4 = new Intent(IngredientsScannerActivity.this, IngredientsScannerActivity.class);
                startActivity(i4);
                return true;
            case R.id.idSearch:
                Intent i5 = new Intent(IngredientsScannerActivity.this, RecipeSearchActivity.class);
                startActivity(i5);
                return true;
            case R.id.idMealPlan:
                Intent i6 = new Intent(IngredientsScannerActivity.this, MealPlanActivity.class);
                startActivity(i6);
                return true;
            case R.id.idEditAccount:
                Intent i7 = new Intent(IngredientsScannerActivity.this, EditAccountActivity.class);
                startActivity(i7);
                return true;
            case R.id.idLogout:
                Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent i8 = new Intent(IngredientsScannerActivity.this, LoginActivity.class);
                startActivity(i8);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // settings menu code end
}

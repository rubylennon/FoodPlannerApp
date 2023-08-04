package com.example.foodplannerapp.activities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 27th February 2023
 * IngredientsScannerActivity.java
 * Description - OCR Ingredients List Activity of Java Android App 'FoodPlannerApp'
 */

// @REF 5 - User Authentication and CRUD Operation with Firebase Realtime Database in Android | GeeksForGeeks - https://www.youtube.com/watch?v=-Gvpf8tXpbc

// imports
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplannerapp.R;
import com.example.foodplannerapp.adapters.IngredientScannerRVAdapter;
import com.example.foodplannerapp.models.Ingredient;
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

public class IngredientsScannerActivity extends BaseMenuActivity {
    // declare variables
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

    // onCreate method executed when activity is launched
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
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
            // @Reference - https://developer.huawei.com/consumer/en/doc/development/connectivity-Guides/adding-permissions-0000001064141094
            // Reference description - Tutorial on how to request permissions
            // request gallery permission if it is not already available
            String[] permissionsStorage = {Manifest.permission.READ_MEDIA_IMAGES};
            int requestExternalStorage = 2;
            int externalStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
            if (externalStoragePermission != PackageManager.PERMISSION_GRANTED) {// if permission is not granted
                ActivityCompat.requestPermissions(this, permissionsStorage, requestExternalStorage);//request permission
            } else{//if permission is granted
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                selectImage.launch(intent);// launch select image activity
            }
        });

        // take photo button event listener
        mCaptureButton.setOnClickListener(v -> {
            // @Reference - https://developer.huawei.com/consumer/en/doc/development/connectivity-Guides/adding-permissions-0000001064141094
            // Reference description - Tutorial on how to request permissions
            // request camera permission if it is not already available
            String[] permissionsCamera = {Manifest.permission.CAMERA};
            int requestCamera = 1;
            int cameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {// if permission is not granted
                ActivityCompat.requestPermissions(this, permissionsCamera, requestCamera);//request permission
            } else{//if permission is granted
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureImage.launch(intent);// launch capture image activity
            }
        });
    }

    // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
    // Reference description - tutorial on how to retrieve data from Firebase database and display in Recycler View using adapter
    // onCreate method executed when activity is started
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
                    Toast.makeText(IngredientsScannerActivity.this, error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
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

    // @Reference - https://github.com/android/codelab-mlkit-android/blob/master/vision/final/app/src/main/java/com/google/codelab/mlkit/MainActivity.java
    // Reference description - Code Lab sample application on how to implement Google ML Kit Vision Text Recognition
    // method for running text recognition functionality
    private void runTextRecognition() {
        // create instance of input image using selected image bitmap
        InputImage image = InputImage.fromBitmap(mSelectedImage, 0);
        // create text recogniser
        TextRecognizer recognizer = TextRecognition.getClient();
        // disable scan ingredients button
        mScanButton.setEnabled(false);
        // pass the image to the process method
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

    // @Reference - https://github.com/android/codelab-mlkit-android/blob/master/vision/final/app/src/main/java/com/google/codelab/mlkit/MainActivity.java
    // Reference description - Code Lab sample application on how to implement Google ML Kit Vision Text Recognition
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

    // @Reference - https://www.youtube.com/watch?v=f2odwvwRTKo
    // Reference description - Tutorial on how to pick an image from a gallery using ActivityResultLauncher
    // activity result handler for select image functionality
    final ActivityResultLauncher<Intent> selectImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
               result -> {// if the activity launch is successful
                    if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
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

                        // clear any currently listed ingredients
                        clearIngredientsList();
                    }
                }
            );

    // @Reference - https://www.youtube.com/watch?v=JMdHMMEO8ZQ
    // Reference description - Tutorial on how to capture an image with camera using ActivityResultLauncher
    // activity result handler for capture image functionality
    final ActivityResultLauncher<Intent> captureImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {// if the activity launch is successful
                if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap capturedImage = (Bitmap) bundle.get("data");

                    // update the image view with the captured photo
                    mImageView.setImageBitmap(capturedImage);
                    mImageView.buildDrawingCache();
                    mSelectedImage = mImageView.getDrawingCache();

                    // re-enable scan ingredients button
                    mScanButton.setEnabled(true);

                    // clear any currently listed ingredients
                    clearIngredientsList();
                }
            }
    );

    // clear the ingredients listed in the RecyclerView
    public void clearIngredientsList(){
        hideNoIngredientsAlert();

        // create new empty matchingIngredientsList
        ArrayList<Ingredient> matchingIngredientsList = new ArrayList<>();

        // update the ingredients recyclerview with empty matching ingredients list
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
}

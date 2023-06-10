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
// @REF 5: User Authentication and CRUD Operation with Firebase Realtime Database in Android | GeeksforGeeks - https://www.youtube.com/watch?v=-Gvpf8tXpbc

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IngredientsScannerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, IngredientRVAdapter.IngredientClickInterface {
    private ImageView mImageView;
    private Button mTextButton;
    private Button mCaptureButton;
    private Button mSelectButton;
    private TextView textview_data;
    private Bitmap mSelectedImage;
    // Max width (portrait mode)
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;
    private RecyclerView ingredientRV;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<IngredientRVModal> ingredientRVModalArrayList;
    private IngredientRVAdapter ingredientRVAdapter;
    private FirebaseAuth mAuth;

//    SEARCH CODE START
    DatabaseReference ingredientsDBRef;
    ArrayList<Ingredient> ingredientsList;
    RecyclerView ingredientsRV;
//    SEARCH CODE END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the actionbar title to Recipes
        setTitle("Ingredients Scanner");

        // set activity_scan_ingredients as activity layout
        setContentView(R.layout.activity_scan_ingredients);

        // ingredientRV = findViewById(R.id.idRVIngredients);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Ingredients");
        ingredientRVModalArrayList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        ingredientRVAdapter = new IngredientRVAdapter(ingredientRVModalArrayList, this, this);
//        ingredientRV.setLayoutManager(new LinearLayoutManager(this));
//        ingredientRV.setAdapter(ingredientRVAdapter);

        // get all ingredients from
        getAllIngredients();

        // find layout elements by id and assign to variables
        // textview_data = findViewById(R.id.ocr_result);
        mImageView = findViewById(R.id.image_view);
        mTextButton = findViewById(R.id.detect_text);
        mCaptureButton = findViewById(R.id.capture_image);
        mSelectButton = findViewById(R.id.select_image);

        // create on click listener for 'Find Text' button
        mTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // run text recognition method
                runTextRecognition();
            }
        });

        // select image button event listener
        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
            }
        });

        // take photo button event listener
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, 2);
            }
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

        //    SEARCH CODE START TWO
        // set firebase database reference to 'Ingredients' data
        ingredientsDBRef = FirebaseDatabase.getInstance().getReference().child("Ingredients");
        // set the recycler view to layout object
        ingredientsRV = findViewById(R.id.rvTwo);
        // LATER - set to empty layout to stop ingredients loading on page load
        ingredientsRV.setLayoutManager(new LinearLayoutManager(this));
        ingredientsRV.setAdapter(ingredientRVAdapter);
        //    SEARCH CODE END

    }

    protected void onStart() {

        super.onStart();

        // SEARCH CODE START
        if(ingredientsDBRef != null){
            ingredientsDBRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        ingredientsList = new ArrayList<>();
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            ingredientsList.add(ds.getValue(Ingredient.class));
                        }
                        IngredientScannerRVAdapter ingredientScannerRVAdapter = new IngredientScannerRVAdapter(ingredientsList);
                        ingredientsRV.setLayoutManager(new LinearLayoutManager(IngredientsScannerActivity.this));
                        ingredientsRV.setAdapter(ingredientScannerRVAdapter);
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

    private void searchThree(ArrayList<String> scannedIngredientsList){
        ArrayList<Ingredient> allIngredientsList = new ArrayList<>();
        ArrayList<Ingredient> matchingIngredientsList = new ArrayList<>();

        // add all ingredients from database to arraylist
        for(Ingredient object : ingredientsList){
            allIngredientsList.add(object);
        }

        // print all ingredients in database
        for(Ingredient object : allIngredientsList){
            Log.d("allIngredientsList Item", object.getIngredientName());
        }

        // print scanned ingredients from processed image
        for(String object : scannedIngredientsList){
            Log.d("scannedIngredientsList Item", object);
        }

        // compare ingredients list and if they match add ingredient
        for(Ingredient object : allIngredientsList){
            if(scannedIngredientsList.toString().toLowerCase().contains(object.getIngredientName().toLowerCase())){
                matchingIngredientsList.add(object);
            }
        }

        // print all matching ingredients
        if (matchingIngredientsList.isEmpty()) {
            Log.d("matchingIngredientsList: ", "LIST EMPTY");
        }else{
            for(Ingredient object : matchingIngredientsList){
                Log.d("matchingIngredientsList Item", object.getIngredientName());
            }
        }

        IngredientScannerRVAdapter ingredientScannerRVAdapter = new IngredientScannerRVAdapter(matchingIngredientsList);
        ingredientsRV.setLayoutManager(new LinearLayoutManager(IngredientsScannerActivity.this));
        ingredientsRV.setAdapter(ingredientScannerRVAdapter);
    }

    private void getAllIngredients() {

        ingredientRVModalArrayList.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ingredientRVModalArrayList.add(snapshot.getValue(IngredientRVModal.class));
                ingredientRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ingredientRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                ingredientRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ingredientRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onIngredientClick(int position) {
        //add code here for ingredient click action
    }

    private void runTextRecognition() {
        InputImage image = InputImage.fromBitmap(mSelectedImage, 0);
        TextRecognizer recognizer = TextRecognition.getClient();
        mTextButton.setEnabled(false);
        recognizer.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                                mTextButton.setEnabled(true);
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                mTextButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }

    private void processTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();

        // print all of the text recognised to console
        Log.d("OCR TEXT FULL", texts.getText());

        // store the OCR text to variable
        String result_text = texts.getText();

        // if no text is recognised execute the following
        if (blocks.size() == 0) {
            showToast();
            return;
        }

        // update the result_text textView to display the OCR result
        // textview_data.setText(result_text);

        // update the stored OCR result text by formatting as below
        // remove new lines
        result_text = result_text.replaceAll("\\n", " ");
        // remove return carriages
        result_text = result_text.replaceAll("\\r", " ");
        // replace start bracket with comma
        result_text = result_text.replaceAll("\\[",",");
        // remove end brackets
        result_text = result_text.replaceAll("]","");

        String ingredients_text;

//        if (result_text.contains(".")){
//            // remove all text before full stop (ingredients list)
//            ingredients_text = result_text.substring(0, result_text.indexOf('.'));
//        } else {
//            ingredients_text = result_text;
//        }

        ingredients_text = result_text;

        Log.d("INGREDIENTS", ingredients_text);

        // split OCR result string by comma to create an Array of Ingredients
        String[] ingredients_list = ingredients_text.split("\\s*,\\s*");

        // print each of the ingredients in the list to the console
        for(String ingredient : ingredients_list){
            Log.d("INGREDIENT", ingredient);
        }

        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, ingredients_list);

        Log.d("INGREDIENT ArrayList", String.valueOf(arrayList));

        for(String object : arrayList){
            Log.d("INGREDIENT ArrayList Item", object);
        }

        searchThree(arrayList);

    }

    private void showToast() {
        Toast.makeText(getApplicationContext(), "No text found", Toast.LENGTH_SHORT).show();
    }

    // Functions for loading images from app assets.

    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxWidth() {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = mImageView.getWidth();
        }

        return mImageMaxWidth;
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxHeight() {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight =
                    mImageView.getHeight();
        }

        Log.i("mImageMaxHeight", Integer.toString(mImageMaxHeight));

        return mImageMaxHeight;
    }

    // Gets the targeted width / height.
    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth();
        int maxHeightForPortraitMode = getImageMaxHeight();
        targetWidth = maxWidthForPortraitMode;
        targetHeight = maxHeightForPortraitMode;

        Log.i("targetWidth", Integer.toString(targetWidth));
        Log.i("targetHeight", Integer.toString(targetHeight));

        return new Pair<>(targetWidth, targetHeight);
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                mSelectedImage = getBitmapFromAsset(this, "Please_walk_on_the_grass.jpg");
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                mSelectedImage = getBitmapFromAsset(this, "grace_hopper.jpg");
                break;
            case 2:
                // Whatever you want to happen when the third item gets selected
                mSelectedImage = getBitmapFromAsset(this, "food_label.jpg");
                break;
        }
        if (mSelectedImage != null) {
            // Get the dimensions of the View
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            int targetWidth = targetedSize.first;
            int maxHeight = targetedSize.second;

            Log.i("targetWidth", Integer.toString(targetWidth));
            Log.i("maxHeight", Integer.toString(maxHeight));

            Log.i("mSelectedImage.getWidth()", Integer.toString(mSelectedImage.getWidth()));
            Log.i("mSelectedImage.getWidth()", Integer.toString(mSelectedImage.getWidth()));

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) mSelectedImage.getWidth() / (float) targetWidth,
                            (float) mSelectedImage.getWidth() / (float) maxHeight);

            Log.i("scaleFactor", String.valueOf(scaleFactor));

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            mSelectedImage,
                            (int) (mSelectedImage.getWidth() / scaleFactor),
                            (int) (mSelectedImage.getHeight() / scaleFactor),
                            true);

            mImageView.setImageBitmap(resizedBitmap);

            mSelectedImage = resizedBitmap;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream is;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    // activity result handler for permissions
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

        // if activity result is 1 (Capture Photo Activity)
        } else if (requestCode == 2 && resultCode == RESULT_OK && null != data){
            Bitmap capturedImage = (Bitmap)data.getExtras().get("data");

            // update the image view with the captured photo
            mImageView.setImageBitmap(capturedImage);
            mImageView.buildDrawingCache();
            mSelectedImage = mImageView.getDrawingCache();
        }
    }
}

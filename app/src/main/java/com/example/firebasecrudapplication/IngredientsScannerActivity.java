package com.example.firebasecrudapplication;

/*
 * @Author: Ruby Lennon (x19128355)
 * 27th February 2023
 * IngredientsScannerActivity.java
 * Description - OCR Ingredients List Activity of Java Android App 'FoodPlannerApp'
 */

// @REF: select image from gallery and show it in ImageView - https://www.youtube.com/watch?v=i3-WL9Xv4hA
// @REF: Google MLKit Samples - https://github.com/googlesamples/mlkit/tree/master/android/vision-quickstart
// @REF: How to Capture Image And Display in ImageView in android Studio - https://www.youtube.com/watch?v=d7Nia9vKUDM

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
import android.Manifest;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class IngredientsScannerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ImageView mImageView;
    private Button mTextButton;
    private Button mCaptureButton;
    private Button mSelectButton;
    TextView textview_data;
    private Bitmap mSelectedImage;
    // Max width (portrait mode)
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        final int REQUEST_CAMERA_CODE = 100;
//        final int REQUEST_STORAGE_CODE = 100;

        // set the actionbar title to Recipes
        setTitle("Ingredients Scanner");

        // set activity_scan_ingredients as activity layout
        setContentView(R.layout.activity_scan_ingredients);

        // find layout elements by id and assign to variables
        textview_data = findViewById(R.id.ocr_result);
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

        // spinner setup
        Spinner dropdown = findViewById(R.id.spinner);
        String[] items = new String[]{"Test Image 1 (Text)", "Test Image 2 (Face)", "Test Image 3 (Label)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

//        // get camera permissions if they are not already enabled
//        if (ContextCompat.checkSelfPermission(IngredientsScannerActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(IngredientsScannerActivity.this, new String[]{
//                    Manifest.permission.CAMERA
//            }, REQUEST_CAMERA_CODE);
//        }

//        // get external permissions if they are not already enabled
//        if (ContextCompat.checkSelfPermission(IngredientsScannerActivity.this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(IngredientsScannerActivity.this, new String[]{
//                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
//            }, REQUEST_STORAGE_CODE);
//        }

        String[] permissionsStorage = {Manifest.permission.READ_EXTERNAL_STORAGE};
        int requestExternalStorage = 1;
        int externalStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (externalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionsStorage, requestExternalStorage);
        }

        String[] permissionsCamera = {Manifest.permission.CAMERA};
        int requestCamera = 1;
        int cameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionsCamera, requestCamera);
        }
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
        textview_data.setText(result_text);

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

        if (result_text.contains(".")){
            // remove all text before full stop (ingredients list)
            ingredients_text = result_text.substring(0, result_text.indexOf('.'));
        } else {
            ingredients_text = result_text;
        }

        Log.d("INGREDIENTS", ingredients_text);

        // split OCR result string by comma to create an Array of Ingredients
        String[] ingredients_list = ingredients_text.split("\\s*,\\s*");

        // print each of the ingredients in the list to the console
        for(String ingredient : ingredients_list){
            Log.d("INGREDIENT", ingredient);
        }

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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data){
            Uri selectedImage = data.getData();
            String[] filepath = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filepath, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filepath[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            mImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            mImageView.buildDrawingCache();
            mSelectedImage = mImageView.getDrawingCache();

        } else if (requestCode == 2 && resultCode == RESULT_OK && null != data){
            Bitmap capturedImage = (Bitmap)data.getExtras().get("data");

            mImageView.setImageBitmap(capturedImage);

            mImageView.buildDrawingCache();
            mSelectedImage = mImageView.getDrawingCache();
        }
    }
}

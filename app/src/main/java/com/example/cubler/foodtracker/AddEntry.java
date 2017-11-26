package com.example.cubler.foodtracker;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static android.app.Activity.RESULT_OK;


public class AddEntry extends AppCompatActivity {

//    private static final Logger LOGGER = new Logger();

    static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    static int CHOOSEITEM = 2;
    static int FULLCAMERA = 3;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static String TAG = "AddEntry";
    private static Boolean thumbnail = false;

    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";

//    private static final String OUTPUT_NAME = "output";
//    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
//    private static final String LABEL_FILE =
//            "file:///android_asset/imagenet_comp_graph_label_strings.txt";

    private static final String OUTPUT_NAME = "final_result";
        private static final String MODEL_FILE = "file:///android_asset/veg_101_graph.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/veg_101_labels.txt";
    private static final String USDAURL = "https://api.nal.usda.gov/ndb/reports/";

    private Bitmap currentBitmap;
    private Classifier classifier;
    private ListView resultsView;
    private ListView foodView;
    private List<String> foodList = new ArrayList<String>();
    private FoodEntry foodEntry = new FoodEntry();
    private String foodname = null;
    private String otherFoodName = null;

    String[] foods = {"Apple", "Banana", "Carrots", "Dates", "Eggplant"};
    ContentValues values;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    2);
        }

        resultsView = ((ListView) findViewById(R.id.resultsList));
        resultsView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        resultsView.setOnItemClickListener(listClickListener);

        foodView = (ListView) findViewById(R.id.foodList);

        values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        defaultDetectView();
        try {
            classifier = TensorFlowImageClassifier.create(
                    getAssets(),
                    MODEL_FILE,
                    LABEL_FILE,
                    INPUT_SIZE,
                    IMAGE_MEAN,
                    IMAGE_STD,
                    INPUT_NAME,
                    OUTPUT_NAME);
//            makeButtonVisible();
        } catch (final Exception e) {
            throw new RuntimeException("Error initializing TensorFlow!", e);
        }
    }


    public void camera(View v) {
        if(thumbnail){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, FULLCAMERA);
        }
    }

    public void detect(View v){
        if(currentBitmap ==null){return;}

        currentBitmap = Bitmap.createScaledBitmap(currentBitmap, INPUT_SIZE, INPUT_SIZE, false);
        final List<Classifier.Recognition> results = classifier.recognizeImage(currentBitmap);

        String[] resultsString = new String[results.size()+1];
        for(int i = 0; i<results.size(); i++){
            String item = results.get(i).toString();
            item = item.substring(item.indexOf(" ")).trim();

            resultsString[i] = item;
        }
        resultsString[results.size()] = "Other";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, resultsString);
        resultsView.setAdapter(adapter);
        Log.v("tag", "Detect: " + results.toString());

    }

    public void defaultDetectView(){
        String[] resultsString = {"Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, resultsString);
        resultsView.setAdapter(adapter);

    }

    public void addButton(View v){
        TextView entryName = (TextView) findViewById(R.id.entryNameText);
        foodEntry.setName(entryName.getText().toString());
        Intent resultIntent = new Intent(AddEntry.this, FoodEntry.class);
        resultIntent.putExtra("foodEntry", foodEntry);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void launchSelectionHelper(){
        if(foodname =="Other"){
            getOtherInput();
        }else if(foodname == null || foodname == ""){
            return;
        }else {
            launchChooseItemActivity(foodname);
        }
    }

    public void launchChooseItemActivity(String fname){
        Intent intent = new Intent(getApplicationContext(), ChooseItem.class);
        intent.putExtra("itemName",fname);
        startActivityForResult(intent, CHOOSEITEM);
    }

    public void getOtherInput(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Food");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                otherFoodName = input.getText().toString();
                launchChooseItemActivity(otherFoodName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int rc, int resc, Intent data) {
        ImageView iv = null;
        String imageurl;
        Bitmap bm;
        iv = ((ImageView)findViewById(R.id.imageView));

        if (rc == REQUEST_IMAGE_CAPTURE && resc == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            iv.setImageBitmap(imageBitmap);
        }else if(rc == FULLCAMERA){
            try {
                currentBitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                iv.setImageBitmap(currentBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(rc == CHOOSEITEM && resc == RESULT_OK){
            Bundle extras = data.getExtras();
            FoodItem foodItem = (FoodItem) extras.getParcelable("foodItem");
            addFoodItemToList(foodItem);
        }


    }

//    Listener gets notified when an item in the list of predicted possible items is choosen
    private AdapterView.OnItemClickListener listClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            adapterView.requestFocusFromTouch();
            adapterView.setSelection(i);
            foodname = adapterView.getItemAtPosition(i).toString().split(" ")[0];
            launchSelectionHelper();
        }
    };

    public void addFoodItemToList(FoodItem foodItem){
        foodEntry.addFoodItem(foodItem);
        foodList.add(String.format("%s: %.2f %s (Calories: %.0f)",
                foodItem.getName(), foodItem.getServingQuantity(), foodItem.getServingLabel(), foodItem.getCalories()));
        updateFoodView();
    }

    public void updateFoodView(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, foodList);
        foodView.setAdapter(adapter);
    }





}

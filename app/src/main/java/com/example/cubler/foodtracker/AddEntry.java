package com.example.cubler.foodtracker;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;

public class AddEntry extends AppCompatActivity {

//    private static final Logger LOGGER = new Logger();

    static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private Bitmap currentBitmap;

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

        values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

    }


    public void camera(View v) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 1);
    }

    public void addFood(View v){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, foods);
        ListView foodList = (ListView) findViewById(R.id.foodList);
        foodList.setAdapter(adapter);
    }

    public void detect(View v){

//        final List<Classifier.Recognition> results = classifier.recognizeImage(currentBitmap);
//        Log.v("tag", "Detect: " + results.toString());

    }



    @Override
    protected void onActivityResult(int rc, int resc, Intent data) {
        ImageView iv = null;
        String imageurl;
        Bitmap bm;
        iv = ((ImageView)findViewById(R.id.imageView));

        try {
            currentBitmap = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), imageUri);
            iv.setImageBitmap(currentBitmap);
            imageurl = getRealPathFromURI(imageUri);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}

package com.example.cubler.foodtracker;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class DiaryActivity extends AppCompatActivity {

    static int ADDENTRY = 4;
    static int EDITENTRY = 5;
    private List<FoodEntry> foodEntryList = new ArrayList<>();
    private EditText calorieText = null;
    private EditText fatText = null;
    private EditText carbsText = null;
    private EditText sugarText = null;
    private EditText proteinText = null;
    private EditText dateText = null;
    private ListView foodEntryListView = null;
    private DateDBHelper dateDBHelper =null;
    private int selectedFoodEntry = -1;
    private String date = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        calorieText = (EditText) findViewById(R.id.caloriesText);
        fatText = (EditText) findViewById(R.id.fatText);
        carbsText = (EditText) findViewById(R.id.carbsText);
        sugarText = (EditText) findViewById(R.id.sugarText);
        proteinText = (EditText) findViewById(R.id.proteinText);
        foodEntryListView = (ListView) findViewById(R.id.foodEntryList);
        foodEntryListView.setOnItemClickListener(listClickListener);
        dateText = (EditText) findViewById(R.id.dateText);
        dateDBHelper = new DateDBHelper(getApplicationContext());

        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        if(date != null){
            dateText.setText(date);
            loadDate(date);
        }

    }

    public void goToAddEntry(View view){
        Intent intent = new Intent(this, AddEntry.class);
        startActivityForResult(intent, ADDENTRY);
    }
    public void editFoodEntry(View v){
        Intent intent = new Intent(this, AddEntry.class);
        if(selectedFoodEntry == -1){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please Select a Food Entry").setTitle("Invalid Food Entry");
            AlertDialog dialog = builder.create();
            return;
        }
        intent.putExtra("foodEntry", foodEntryList.get(selectedFoodEntry));
        startActivityForResult(intent, EDITENTRY);
    }
    public void deleteEntry(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Delete this Food Entry?").setTitle("Delete Food Entry");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase dbW = dateDBHelper.getWritableDatabase();
                dbW.delete(DateEntryContract.DateEntry.TABLE_NAME, DateEntryContract.DateEntry.DATE + "=" + date, null);
                finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ADDENTRY && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            FoodEntry foodEntry = (FoodEntry) extras.getParcelable("foodEntry");
            foodEntryList.add(foodEntry);
            updateFoodEntryView();
        }if(requestCode == EDITENTRY && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            FoodEntry foodEntry = (FoodEntry) extras.getParcelable("foodEntry");
            foodEntryList.remove(selectedFoodEntry);
            if(!foodEntry.isEmpty()) {
                foodEntryList.add(foodEntry);
                updateFoodEntryView();
            }
        }
    }

    public void saveDate(View v){
        SQLiteDatabase dbW = dateDBHelper.getWritableDatabase();
        String date = dateText.getText().toString();
        for(FoodEntry foodEntry: foodEntryList){
            ContentValues values = new ContentValues();
            values.put(DateEntryContract.DateEntry.DATE, date);
            values.put(DateEntryContract.DateEntry.FOODENTRIESJSON, foodEntry.toJSON().toString());
            dbW.insert(DateEntryContract.DateEntry.TABLE_NAME, null, values);
        }
        finish();
    }

    public void loadDate(String data){
        SQLiteDatabase dbR = dateDBHelper.getReadableDatabase();
        List itemIds = null;
        String queryString = "SELECT " + DateEntryContract.DateEntry.TABLE_NAME + "." + DateEntryContract.DateEntry.FOODENTRIESJSON +
                " FROM " + DateEntryContract.DateEntry.TABLE_NAME + " ";

        String selection = DateEntryContract.DateEntry.DATE +" = ?";
        ArrayList<String> selectionArgs = new ArrayList<String>();
        selectionArgs.add(data);
        queryString += "WHERE " + selection;
        Cursor cursor = dbR.rawQuery(queryString, selectionArgs.toArray(new String[selectionArgs.size()]));

        try {
            while (cursor.moveToNext()) {
                JSONArray jsonArray = new JSONArray(cursor.getString(0));
                FoodEntry foodEntry = new FoodEntry(jsonArray);
                foodEntryList.add(foodEntry);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        updateFoodEntryView();
    }

    private AdapterView.OnItemClickListener listClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            adapterView.requestFocusFromTouch();
            adapterView.setSelection(i);
            selectedFoodEntry = i;
            String entryClicked = adapterView.getItemAtPosition(i).toString();
            if(entryClicked.equals("Total")){
                FoodEntry[] foodEntryArray = foodEntryList.toArray(new FoodEntry[foodEntryList.size()]);
                updateSummery(foodEntryArray);
            }else {
                updateSummery(foodEntryList.get(i));
            }
        }
    };

    public void updateFoodEntryView() {
        List<String> nameList = new ArrayList<>();
        for(FoodEntry foodEntry: foodEntryList){
            nameList.add(foodEntry.getName());
        }
        nameList.add("Total");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, nameList);
        foodEntryListView.setAdapter(adapter);
    }

    public void updateSummery(FoodEntry foodEntry){
        FoodEntry[] foodEntries = new FoodEntry[]{foodEntry};
        updateSummery(foodEntries);
    }

    public void updateSummery(FoodEntry[] foodEntries){
        double calories = 0;
        double fat = 0;
        double carbs = 0;
        double sugar = 0;
        double protein = 0;

        for(FoodEntry foodEntry: foodEntries){
            calories += foodEntry.getCalories();
            fat += foodEntry.getFat();
            carbs += foodEntry.getCarbs();
            sugar += foodEntry.getSugar();
            protein += foodEntry.getProtein();
        }

        calorieText.setText(String.format("%.2f",calories));
        fatText.setText(String.format("%.2f",fat));
        carbsText.setText(String.format("%.2f",carbs));
        sugarText.setText(String.format("%.2f",sugar));
        proteinText.setText(String.format("%.2f",protein));
    }
}

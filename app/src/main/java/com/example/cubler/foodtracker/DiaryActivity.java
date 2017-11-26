package com.example.cubler.foodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class DiaryActivity extends AppCompatActivity {

    static int ADDENTRY = 4;
    private List<FoodEntry> foodEntryList = new ArrayList<>();
    private EditText calorieText = null;
    private EditText fatText = null;
    private EditText carbsText = null;
    private EditText sugarText = null;
    private EditText proteinText = null;
    private ListView foodEntryListView = null;

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
    }

    public void goToAddEntry(View view){
        Intent intent = new Intent(this, AddEntry.class);
        startActivityForResult(intent, ADDENTRY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ADDENTRY && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            FoodEntry foodEntry = (FoodEntry) extras.getParcelable("foodEntry");
            foodEntryList.add(foodEntry);
            updateFoodEntryView();
        }
    }

    private AdapterView.OnItemClickListener listClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            adapterView.requestFocusFromTouch();
            adapterView.setSelection(i);
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

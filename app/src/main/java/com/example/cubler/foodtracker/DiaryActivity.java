package com.example.cubler.foodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DiaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
    }

    public void goToAddEntry(View view){
        Intent intent = new Intent(this, AddEntry.class);
        startActivity(intent);
    }

}

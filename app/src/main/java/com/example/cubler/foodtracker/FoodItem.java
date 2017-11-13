package com.example.cubler.foodtracker;

/**
 * Created by cubler on 11/13/17.
 */

public class FoodItem {
    public String name;
    public double calories;
    public double protein;
    public double fat;
    public double carbs;
    public double sugar;
    public String serving;

    public FoodItem(String name, double calories, double protein, double fat, double carbs, double sugar, String serving){
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.sugar = sugar;
        this.serving = serving;
    }


}

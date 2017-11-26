package com.example.cubler.foodtracker;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by cubler on 11/13/17.
 */

public class FoodItem implements Parcelable{
    public String name;
    public double calories;
    public double protein;
    public double fat;
    public double carbs;
    public double sugar;
    public String servingLabel;
    public double servingQuantity;

    public FoodItem(){
        this.name = null;
        this.calories = -1.0;
        this.protein = -1.0;
        this.fat = -1.0;
        this.carbs = -1.0;
        this.sugar = -1.0;
        this.servingLabel = null;
        this.servingQuantity =-1.0;
    }
    public FoodItem(Parcel in){
        this.name = in.readString();
        this.calories = in.readDouble();
        this.protein = in.readDouble();
        this.fat = in.readDouble();
        this.carbs = in.readDouble();
        this.sugar = in.readDouble();
        this.servingLabel = in.readString();
        this.servingQuantity =in.readDouble();
    }

    public FoodItem(String name, double calories, double protein, double fat, double carbs, double sugar, String serving, double quantity){
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.sugar = sugar;
        this.servingLabel = serving;
        this.servingQuantity = quantity;
    }

    public FoodItem(JSONObject jsonObject){

        try {
            this.name = jsonObject.getString("name");
            this.calories = jsonObject.getDouble("calories");
            this.carbs = jsonObject.getDouble("carbs");
            this.fat = jsonObject.getDouble("fat");
            this.protein = jsonObject.getDouble("protein");
            this.sugar = jsonObject.getDouble("sugar");
            this.servingLabel = jsonObject.getString("servingLabel");
            this.servingQuantity = jsonObject.getDouble("servingQuantity");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public double getCalories(){
        return calories*servingQuantity;
    }

    public double getCarbs() {
        return carbs*servingQuantity;
    }

    public double getFat() {
        return fat*servingQuantity;
    }

    public double getProtein() {
        return protein*servingQuantity;
    }

    public double getSugar() {
        return sugar*servingQuantity;
    }

    public String getName() {
        return name;
    }

    public String getServingLabel() {
        return servingLabel;
    }

    public double getServingQuantity() {
        return servingQuantity;
    }

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("calories", calories);
            jsonObject.put("carbs", carbs);
            jsonObject.put("fat", fat);
            jsonObject.put("protein", protein);
            jsonObject.put("sugar", sugar);
            jsonObject.put("servingLabel", servingLabel);
            jsonObject.put("servingQuantity", servingQuantity);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeDouble(this.calories);
        parcel.writeDouble(this.protein);
        parcel.writeDouble(this.fat);
        parcel.writeDouble(this.carbs);
        parcel.writeDouble(this.sugar);
        parcel.writeString(this.servingLabel);
        parcel.writeDouble(this.servingQuantity);
    }

    public int describeContents() {
        return 0;
    }

    public static Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public FoodItem createFromParcel(Parcel in) {
            return new FoodItem(in);
        }
        public FoodItem[] newArray(int size){
            return new FoodItem[size];
        }
    };
}

package com.example.cubler.foodtracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cubler on 11/22/17.
 */

public class FoodEntry implements Parcelable {

    private List<FoodItem> foodItemList = new ArrayList<FoodItem>();
    private String name = null;

    public FoodEntry(){
    }
    public FoodEntry(FoodItem foodItem){
        foodItemList.add(foodItem);
    }
    public FoodEntry(FoodItem[] foodItems){
        for(FoodItem foodItem: foodItems){
            foodItemList.add(foodItem);
        }
    }
    public FoodEntry(Parcel in){
        foodItemList = new ArrayList<FoodItem>();
        setName(in.readString());
        in.readTypedList(foodItemList, FoodItem.CREATOR);

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addFoodItem(FoodItem foodItem){
        foodItemList.add(foodItem);
    }

    public double getCalories(){
        double total = 0;
        for (FoodItem foodItem: foodItemList) {
            total += foodItem.getCalories();
        }
        return total;
    }
    public double getCarbs(){
        double total = 0;
        for (FoodItem foodItem: foodItemList) {
            total += foodItem.getCarbs();
        }
        return total;
    }
    public double getFat(){
        double total = 0;
        for (FoodItem foodItem: foodItemList) {
            total += foodItem.getFat();
        }
        return total;
    }
    public double getProtein(){
        double total = 0;
        for (FoodItem foodItem: foodItemList) {
            total += foodItem.getProtein();
        }
        return total;
    }
    public double getSugar(){
        double total = 0;
        for (FoodItem foodItem: foodItemList) {
            total += foodItem.getSugar();
        }
        return total;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        FoodItem[] foodItemArray = foodItemList.toArray(new FoodItem[foodItemList.size()]);
        parcel.writeString(getName());
        parcel.writeTypedArray(foodItemArray, i);
    }

    public static Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public FoodEntry createFromParcel(Parcel in) {
            return new FoodEntry(in);
        }
        public FoodEntry[] newArray(int size){
            return new FoodEntry[size];
        }
    };
}

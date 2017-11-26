package com.example.cubler.foodtracker;

import android.provider.BaseColumns;

/**
 * Created by cubler on 11/26/17.
 */

public class DateEntryContract {
    private DateEntryContract(){}

    public static class DateEntry implements BaseColumns{
        public static final String TABLE_NAME = "DateEntries";
        public static final String DATE = "Data";
        public static final String FOODENTRIESJSON = "FoodEntriesJson";
    }

}

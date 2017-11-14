package com.example.cubler.foodtracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

public class ChooseItem extends AppCompatActivity{

    private static final String USDAURL = "https://api.nal.usda.gov/ndb/";
    private static String TAG = "ChooseItem";
    private ListView itemSelectList = null;
    private ListView servingSizeSelectList = null;
    private List<String> itemList = new ArrayList<String>();
    private List<String> servingSizeList = new ArrayList<String>();
    private List<String> searchndbnoList = new ArrayList<>();
    private String ndbno = null;
    private JSONArray nutrientList =null;
    private HashMap<String, Integer> nutrientNametoID =  new HashMap<>();
    private HashMap<Integer, String> nutrientIDtoName =  new HashMap<>();
    private FoodItem foodItem = new FoodItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_item);
        Intent intent = getIntent();
        String itemName = intent.getStringExtra("itemName");
        if(itemName == null){
            //Error
        }
        initalizeNutrientMap();
        itemSelectList = (ListView) findViewById(R.id.itemSelectList);
        servingSizeSelectList = (ListView) findViewById(R.id.servingSizeSelectList);
        itemSelectList.setOnItemClickListener(ItemChoosenListener);
        servingSizeSelectList.setOnItemClickListener(ServingSizeChoosenListener);
        GetItemsAsyncTask getItemsAsyncTask = new GetItemsAsyncTask();
        getItemsAsyncTask.execute("butter");

    }

    public void submitButton(View v){
        double quanity = Double.parseDouble(((EditText) findViewById(R.id.sizeQuantity)).getText().toString());
        foodItem.servingQuantity = quanity;
        Intent resultIntent = new Intent(ChooseItem.this, FoodItem.class);
        resultIntent.putExtra("foodItem", foodItem);
        setResult(RESULT_OK,resultIntent);
        finish();

    }

    public void initalizeNutrientMap(){
        nutrientNametoID.put("Calories",208);
        nutrientIDtoName.put(208,"Calories");
        nutrientNametoID.put("Protein",203);
        nutrientIDtoName.put(203,"Protein");
        nutrientNametoID.put("Fat",204);
        nutrientIDtoName.put(204,"Fat");
        nutrientNametoID.put("Carbs",205);
        nutrientIDtoName.put(205,"Carbs");
        nutrientNametoID.put("Fiber",291);
        nutrientIDtoName.put(291,"Fiber");
        nutrientNametoID.put("Sugar",269);
        nutrientIDtoName.put(269,"Sugar");

        itemSelectList = null;
        servingSizeSelectList = null;
        itemList = new ArrayList<String>();
        servingSizeList = new ArrayList<String>();
        searchndbnoList = new ArrayList<>();
        ndbno = null;
        nutrientList =null;
        foodItem = new FoodItem();

    }

    public void populateItemList(JSONArray items){
        try {
            for (int i = 0; i < items.length(); i++) {
                String itemName = items.getJSONObject(i).getString("name");
                itemList.add(itemName);
                searchndbnoList.add( items.getJSONObject(i).getString("ndbno"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, itemList);
        itemSelectList.setAdapter(adapter);
    }

    public void populateServingSizeList(JSONArray measures){
        servingSizeList = new ArrayList<String>();
        try {
            for (int i = 0; i < measures.length(); i++) {
                String label = measures.getJSONObject(i).getString("label");
                String qty = measures.getJSONObject(i).getString("qty");
                String sizeUnit = measures.getJSONObject(i).getString("eunit"); // typically grams
                String sizeAmount = measures.getJSONObject(i).getString("eqv"); // what the label equals in the sizeUnit ex. 1oz = 28.35
                servingSizeList.add(String.format("%s %s (%s %s)",qty, label,sizeAmount, sizeUnit));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, servingSizeList);
        servingSizeSelectList.setAdapter(adapter);
    }

    private AdapterView.OnItemClickListener ItemChoosenListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String itemChoosen = adapterView.getItemAtPosition(i).toString();
            foodItem.name = itemChoosen;
            ndbno = searchndbnoList.get(i);
            GetNutrientsAsyncTask getNutrientsAsyncTask = new GetNutrientsAsyncTask();
            getNutrientsAsyncTask.execute(ndbno);

        }
    };

    private AdapterView.OnItemClickListener ServingSizeChoosenListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String ServingSize = adapterView.getItemAtPosition(i).toString();
            parseNutrientInfo(i);
        }
    };

    public void parseNutrientInfo(int measureIndex){
        try {
            JSONObject measurements = nutrientList.getJSONObject(0).getJSONArray("measures").getJSONObject(measureIndex);
            foodItem.servingLabel = measurements.getString("label");

            for (int j = 0; j < nutrientList.length(); j++) {
                JSONObject nutrientJSON = nutrientList.getJSONObject(j);
                int nutrient_id = nutrientJSON.getInt("nutrient_id");
                if(nutrientIDtoName.containsKey(nutrient_id)){
                    String nutrientName = nutrientIDtoName.get(nutrient_id);

                    switch (nutrientName) {
                        case "Calories":
                            foodItem.calories = nutrientJSON.getJSONArray("measures").getJSONObject(measureIndex).getDouble("value");
                            break;
                        case "Protein":
                            foodItem.protein = nutrientJSON.getJSONArray("measures").getJSONObject(measureIndex).getDouble("value");
                            break;
                        case "Fat":
                            foodItem.fat = nutrientJSON.getJSONArray("measures").getJSONObject(measureIndex).getDouble("value");
                            break;
                        case "Carbs":
                            foodItem.carbs = nutrientJSON.getJSONArray("measures").getJSONObject(measureIndex).getDouble("value");
                            break;
                        case "Sugar":
                            foodItem.sugar = nutrientJSON.getJSONArray("measures").getJSONObject(measureIndex).getDouble("value");
                            break;
                        default:
                            Log.v(TAG,"NutrientName in Map but not in switch case");
                    }
                }else{
                    continue;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public JSONArray getItemNutrient(String input_ndbno){
        // API call following format at https://ndb.nal.usda.gov/ndb/doc/apilist/API-FOOD-REPORT.md
        JSONObject jsonHead = null;
        String type = "b";
        String format = "json";
        JSONArray nutrients = null;

        String queryResult = getHttpRequest(USDAURL, false,
                new String[]{"ndbno","type", "format"}, new String[]{input_ndbno, type, format});

        try {
            jsonHead = new JSONObject(queryResult);
            nutrients = jsonHead.getJSONObject("report").getJSONObject("food").getJSONArray("nutrients");

        }catch(Exception e){
            e.printStackTrace();
        }

        return nutrients;
    }
    public JSONArray getItems(String name){
        // API call following format at https://ndb.nal.usda.gov/ndb/doc/apilist/API-SEARCH.md
        String ndbno = null;
        String ds = "Standard Reference";
        String format = "JSON";
        String q = name;
        JSONObject jsonHead = null;
        JSONArray items = null;

        String queryResult = getHttpRequest(USDAURL, true, new String[]{"q","ds","format"}, new String[]{q,ds,format});

        try {
            jsonHead = new JSONObject(queryResult);
            items = jsonHead.getJSONObject("list").getJSONArray("item");

        }catch(Exception e){
            e.printStackTrace();
        }

        return items;
    }
    public String getHttpRequest(String _url, boolean search, String[] params, String[] args){

        if(params.length != args.length){
            Log.v(TAG, "getHTTP params != args");
        }

        String output = null;
        int timeout = 1000*5;
        String url = _url;
        if(search){
            url +="search/";
        }else{
            url += "reports/";
        }
        String charset = "UTF-8";
        String api_key = "ptRh3ZAZw9sWdrPUbFdPviakzn0zSPjk37LR4fi8";
        String query = "";

        try {
            for(int i = 0; i < params.length; i++){
                query = query + params[i] +"=" + URLEncoder.encode(args[i], charset) + "&";
            }
            query = query + "api_key" +"=" + URLEncoder.encode(api_key, charset);

            HttpURLConnection connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.connect();
            int status = connection.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    output = sb.toString();
                    break;
                default:
                    Log.v(TAG, "Bad HTTP Request");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    private class GetItemsAsyncTask extends AsyncTask<String, Void, JSONArray>{
        protected JSONArray doInBackground(String... args){
            JSONArray JSONitems = getItems(args[0]);
            return JSONitems;
        }
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            populateItemList(jsonArray);
        }
    }
    private class GetNutrientsAsyncTask extends AsyncTask<String, Void, JSONArray>{
        protected JSONArray doInBackground(String... args){
            JSONArray nutrients = getItemNutrient(args[0]);
            return nutrients;
        }
        @Override
        protected void onPostExecute(JSONArray nutrients){
            try {
                nutrientList = nutrients;
                populateServingSizeList(nutrients.getJSONObject(0).getJSONArray("measures"));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}

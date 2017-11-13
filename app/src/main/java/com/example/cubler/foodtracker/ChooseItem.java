package com.example.cubler.foodtracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ChooseItem extends AppCompatActivity{

    private static final String USDAURL = "https://api.nal.usda.gov/ndb/";
    private static String TAG = "ChooseItem";
    private ListView itemSelectList = null;
    private ListView servingSizeSelectList = null;
    private List<String> itemList = new ArrayList<String>();
    JSONArray JSONitems = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_item);
        Intent intent = getIntent();
        final String itemName = intent.getStringExtra("itemName");
        if(itemName == null){
            //Error
        }
        itemSelectList = (ListView) findViewById(R.id.itemSelectList);
        servingSizeSelectList = (ListView) findViewById(R.id.servingSizeSelectList);
        itemSelectList.setOnItemClickListener(ItemChoosenListener);
        servingSizeSelectList.setOnItemClickListener(ServingSizeChoosenListener);
        GetItemsAsyncTask asyncTask = new GetItemsAsyncTask();
        asyncTask.execute("butter");
        

    }

    public void populateItemList(JSONArray items){
        try {
            for (int i = 0; i < items.length(); i++) {
                String itemName = items.getJSONObject(i).getString("name");
                itemList.add(itemName);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, itemList);
        itemSelectList.setAdapter(adapter);
    }

    private AdapterView.OnItemClickListener ItemChoosenListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String itemChoosen = adapterView.getItemAtPosition(i).toString();

//            addFoodItemToList(foodItem);
        }
    };

    private AdapterView.OnItemClickListener ServingSizeChoosenListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String ServingSize = adapterView.getItemAtPosition(i).toString();

//            addFoodItemToList(foodItem);
        }
    };

    public JSONObject getItemNutrient(String input_ndbno){
        // API call following format at https://ndb.nal.usda.gov/ndb/doc/apilist/API-FOOD-REPORT.md
        JSONObject jsonHead = null;
        String type = "b";
        String format = "json";
        JSONObject nutrients = null;

        String queryResult = getHttpRequest(USDAURL, false,
                new String[]{"ndbno","type", "format"}, new String[]{input_ndbno, type, format});

        try {
            jsonHead = new JSONObject(queryResult);
            nutrients = jsonHead.getJSONObject("report").getJSONObject("food").getJSONObject("nutrients");

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
        protected JSONArray doInBackground(String... foodName){
            JSONArray JSONitems = getItems(foodName[0]);
            return JSONitems;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            populateItemList(jsonArray);
        }
    }
}

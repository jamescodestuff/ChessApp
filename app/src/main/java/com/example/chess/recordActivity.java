package com.example.chess;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class recordActivity extends AppCompatActivity {

    private ListView list;
    private Spinner spinner;
    private List<String> sortalpha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);
        list = findViewById(R.id.list);
        spinner = findViewById(R.id.spinner);

        //Deserialize
        SharedPreferences sharedNames = getSharedPreferences("gameNames", MODE_PRIVATE);
        String serializedList = sharedNames.getString("nameList", null);
        List<String> games = new Gson().fromJson(serializedList, new TypeToken<List<String>>(){}.getType());

        if(games == null) games = new ArrayList<>();
        sortalpha = new ArrayList<String>(games);
        Collections.sort(sortalpha);

        //Set spinner
        String[] items = {"Sort By: Date", "Sort By: Title"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Spinner event listener
        List<String> finalGames = games;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // This will be called whenever an item in the spinner is selected
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("Sort By: Title")) sortList(sortalpha);
                else sortList(finalGames);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // This will be called if the user doesn't select any item
            }
        });


//        Populate listView
        ArrayAdapter<String> lists = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, games);
        list.setAdapter(lists);

        //Click on list
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = (String) parent.getItemAtPosition(position);
                Intent popup = new Intent(recordActivity.this, playbackActivity.class);
                popup.putExtra("STRING_KEY", clickedItem);
                startActivity(popup);

            }
        });
    }

    public void sortList(List<String> arr){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);
        list.setAdapter(adapter);
    }

}




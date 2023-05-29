package com.example.chess;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class saveActivity extends AppCompatActivity {
    public List<String> nameList = new ArrayList();

    @Override
    public void onBackPressed() {
        // Do nothing to disable the back button
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save);
        Intent intent = getIntent();

        //Display who wins
        String message = intent.getStringExtra("STRING_KEY");
        TextView title = findViewById(R.id.resignMsg);
        title.setText(message);

        String json = getIntent().getStringExtra("history_list");
        Type listType = new TypeToken<List<Pair<Integer, Integer>>>(){}.getType();
        List<Pair<Integer, Integer>> history = new Gson().fromJson(json, listType);

        Button btn = findViewById(R.id.save);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText textInputLayout = findViewById(R.id.input);
                String text = textInputLayout.getText().toString();
                if(text.length() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Input a game title", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                SharedPreferences sharedNames = getSharedPreferences("gameNames", MODE_PRIVATE);
                String serializedList = sharedNames.getString("nameList", null);
                List<String> nameList = new Gson().fromJson(serializedList, new TypeToken<List<String>>() {}.getType());
                // Add new game title to the list
                if(nameList == null) nameList = new ArrayList<>();
                nameList.add(text);
                //serialized history
                String serializedPairs = new Gson().toJson(history);
                SharedPreferences sharedPrefs = getSharedPreferences(text, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("pairs", serializedPairs);
                editor.apply();
                //serialized text
                SharedPreferences.Editor nameEditor = sharedNames.edit();
                serializedList = new Gson().toJson(nameList);
                nameEditor.putString("nameList", serializedList);
                nameEditor.apply();
                Intent intent = new Intent(saveActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button discard = findViewById(R.id.donotsave);
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(saveActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
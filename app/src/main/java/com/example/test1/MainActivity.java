package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> actorsArray = new ArrayList<String>();
        ListView listView = findViewById(R.id.listView);

        actorsArray.add("Leonardo dicaprio");
        actorsArray.add("Brad Pitt");
        actorsArray.add("Christian bale");
        actorsArray.add("Ryan gosling");
        actorsArray.add("Matthew mcConaughey");
        actorsArray.add("Robert de niro");
        actorsArray.add("Tom cruise");


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,actorsArray);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("actor selected:- ",String.valueOf(position));
                String actorSelected = (String) parent.getItemAtPosition(position);
                Log.i("actor selected:- ",actorSelected);

                Intent intent = new Intent(getApplicationContext(), ActorsActivity.class);
                intent.putExtra("actorIndex",position);
                intent.putExtra("actorSelected",actorSelected);
                startActivity(intent);
            }
        });
    }
}
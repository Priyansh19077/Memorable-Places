package com.example.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView heading;
    Button new_place;
    static ListView listView;
    static ArrayAdapter arrayAdapter;
    static ArrayList<LatLng> locations;
    static ArrayList<String> places;
    static TextView t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        heading=(TextView)findViewById(R.id.heading);
        t1=(TextView)findViewById(R.id.txt1);
        heading.setAlpha(0);
        heading.animate().alpha(1).setDuration(1400);
        new_place=(Button)findViewById(R.id.new_place);
        locations=new ArrayList<LatLng>();
        listView=(ListView)findViewById(R.id.places);
        places=new ArrayList<String>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.memorableplaces", Context.MODE_PRIVATE);
        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();
        try{
            places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places", ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lats", ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longs", ObjectSerializer.serialize(new ArrayList<String>())));
        }catch (Exception e){
            e.printStackTrace();
        }
        if(places.size() > 0 && latitudes.size() > 0 && longitudes.size() > 0){
            if(places.size() == latitudes.size() && places.size() == longitudes.size()){
                for(int i = 0; i < latitudes.size(); i++) {
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(longitudes.get(i))));
                    Log.i("Places", locations.toString());
                    new_place.setVisibility(View.INVISIBLE);
                }
            }
        }
        arrayAdapter=new ArrayAdapter(this,R.layout.listrow,R.id.txtView2,places);
        listView.setAdapter(arrayAdapter);
        new_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //moving to maps activity with position -1
                Intent in=new Intent(MainActivity.this,MapsActivity.class);
                in.putExtra("index",-1);
                startActivity(in);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //move to maps activity with the position
                Intent in=new Intent(MainActivity.this,MapsActivity.class);
                in.putExtra("index",i);
                startActivity(in);
            }
        });

    }
}
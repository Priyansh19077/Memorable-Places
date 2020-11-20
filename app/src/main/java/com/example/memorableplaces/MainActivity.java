package com.example.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Bundle;
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
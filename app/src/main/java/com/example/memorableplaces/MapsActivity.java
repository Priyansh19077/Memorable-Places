package com.example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    Toast t1;
    int index;
    boolean possible;
    LocationManager locationManager;
    LocationListener locationListener;
    Marker loc=null;
    public void center_on_location(LatLng location,String title) {
        LatLng userlocation=new LatLng(location.latitude,location.longitude);
        Log.i("userlocation",userlocation.toString());
        if(loc!=null) {
            loc.remove();
            loc=mMap.addMarker(new MarkerOptions().position(userlocation).title(title));

        }
        else {
            mMap.clear();
            loc=mMap.addMarker(new MarkerOptions().position(userlocation).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation,18));
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        possible=false;

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        Object latlng;
        mMap.setOnMapLongClickListener(this);
        Intent in=getIntent();
        locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                center_on_location(new LatLng(location.getLatitude(),location.getLongitude()),"You are Here");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        index=in.getIntExtra("index",-1);
        if(index==-1) {
            //zoom in on user location
            Toast.makeText(getApplicationContext(),"LONG PRESS ANYWHERE TO ADD THAT LOCATION",Toast.LENGTH_SHORT).show();
            possible=true;
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastKnownLocation!=null)
                center_on_location(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()),"You are Here");
                else
                {
                    t1=Toast.makeText(getApplicationContext(),"Locating... \n Make sure that your location is ON and you are connected to network",Toast.LENGTH_LONG);
                    t1.show();
                }
            }
        }
        else
        {
            LatLng location=MainActivity.locations.get(index);
            String title=MainActivity.places.get(index);
            center_on_location(location,title);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(!possible) {
            Toast.makeText(getApplicationContext(),"YOU CANNOT ADD A PLACE WHILE VIEWING",Toast.LENGTH_SHORT).show();
            return;
        }
        String title="";
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try{
            List<Address> addressList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            Log.i("New Address",addressList.toString());
            String address = addressList.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addressList.get(0).getLocality();
            String state = addressList.get(0).getAdminArea();
            String country = addressList.get(0).getCountryName();
            String postalCode = addressList.get(0).getPostalCode();
            String knownName = addressList.get(0).getFeatureName();
            String thoroughFare=addressList.get(0).getThoroughfare();
            String SubThoroughFare=addressList.get(0).getSubThoroughfare();
            int count=0;
            if(SubThoroughFare!=null && count<3) {
                count++;
                title += SubThoroughFare + " ";
            }
            if(thoroughFare!=null && count<3) {
                count++;
                title += thoroughFare+" ";
            }
            if(city!=null && count<3) {
                count++;
                title += city + " ";
            }
            if(state!=null && count<3) {
                count++;
                title += state + " ";
            }
            if(country!=null && count<3) {
                count++;
                title += country + " ";
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        if(title=="") {
            Toast.makeText(getApplicationContext(),"Not a Recognized Place!!!",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"New Place Added",Toast.LENGTH_SHORT).show();
            MainActivity.locations.add(latLng);
            MainActivity.places.add(title);
            MainActivity.arrayAdapter.notifyDataSetChanged();
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.memorableplaces", Context.MODE_PRIVATE);
            try{
                ArrayList<String> latitudes = new ArrayList<>();
                ArrayList<String> longitudes = new ArrayList<>();
                for(LatLng coords : MainActivity.locations){
                    latitudes.add(Double.toString(coords.latitude));
                    longitudes.add(Double.toString(coords.longitude));
                }
                sharedPreferences.edit().putString("places", ObjectSerializer.serialize((MainActivity.places))).apply();
                sharedPreferences.edit().putString("lats", ObjectSerializer.serialize((latitudes))).apply();
                sharedPreferences.edit().putString("longs", ObjectSerializer.serialize((longitudes))).apply();
            }catch(Exception e){
                e.printStackTrace();;
            }
            if(MainActivity.t1.getText().equals("LIST IS EMPTY\n ADD A PLACE NOW"))
                MainActivity.t1.setAlpha(0);
            mMap.addMarker(new MarkerOptions().position(latLng).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
    }
}
package com.example.lab4;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LocationManager lm;
    private LocationListener listener;
    private TextView currentLocation;
    private TextView prevLocationTxt;
    private TextView direction;
    private TextView savedPoints;
    private TextView closestPoint;
    private Button save;
    private List<Location> locations;
    private Location lastLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkPermission()) requestPermission();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new MyListener();
        locations = new ArrayList<Location>();
        currentLocation = (TextView) findViewById(R.id.loctv);
        prevLocationTxt= (TextView) findViewById(R.id.prevloctv);
        direction = (TextView) findViewById(R.id.direction);
        closestPoint = (TextView) findViewById(R.id.closest);
        savedPoints = (TextView) findViewById(R.id.saved);
        registerListener();
        save = (Button) findViewById(R.id.button);
        save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                if(lastLoc != null) {
                    locations.add(lastLoc);
                    savedPoints.append("\n"+lastLoc.getLatitude() + " " + lastLoc.getLongitude());
                }
            }
        });
    }

    private void registerListener(){
        if (checkPermission()) lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, listener);
        else requestPermission();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_FINE_LOCATION}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public class MyListener implements LocationListener {
        private Location prevLocation = null;
        private float bearing;
        private float distance;
        private float spped;

        @Override
        public void onLocationChanged(Location location){
            closestLocation(location);
            if(prevLocation!=null){
                bearing = prevLocation.bearingTo(location);
                distance = prevLocation.distanceTo(location);
                spped = location.getSpeed();
                prevLocationTxt.setText("Previous location: " + prevLocation.getLatitude() + " " + prevLocation.getLongitude());
                direction.setText("Bearing: " + String.valueOf(bearing) + " Speed: " + String.valueOf(spped));
            }
            currentLocation.setText("Current location: " + location.getLatitude() + " " + location.getLongitude());
            prevLocation = location;
            lastLoc = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){

        }

        @Override
        public void onProviderEnabled(String provider){

        }

        @Override
        public void onProviderDisabled(String provider){

        }

        public void closestLocation(Location location){
            Location closest;
            float distance;
            //if (locations.size() == 0) return;
            //if (locations.size() == 1) closest = locations.get(0);
            if (locations.size() > 0){
                closest = locations.get(0);
                distance = location.distanceTo(closest);
                for (int i = 1; i < locations.size(); i++) {
                    float distTmp = location.distanceTo(locations.get(i));
                    if(distTmp<distance){
                        distance = distTmp;
                        closest = locations.get(i);
                    }
                }
                closestPoint.setText("Closest point: " + closest.getLatitude() + " " + closest.getLongitude() + "\nDistance: " + location.distanceTo(closest) + " Bearing" + location.bearingTo(closest));
            }
        }
    }

}
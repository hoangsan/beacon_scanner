package com.sanvo.beaconscanerdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import com.sanvo.beacon.LocationManager;
import com.sanvo.beacon.LocationManagerDelegate;
import com.sanvo.beacon.object.Beacon;
import com.sanvo.beacon.object.BeaconRegion;
import com.sanvo.beacon.object.Error;
import com.sanvo.beacon.object.Region;
import com.sanvo.beaconscanerdemo.R;

public class MainActivity extends AppCompatActivity implements LocationManagerDelegate {
    //------------------------------
    LocationManager lm;
    //------------------------------

    ListView listView ;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list);
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(adapter);

        //------------------------------
        lm = LocationManager.getInstance(getApplicationContext());
        lm.setLocationManagerDelegate(this);
        lm.setMonitoringInterval(5000);
        lm.setScanMode(LocationManager.ScanMode.LOW_LATENCY);
        //------------------------------

        checkPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //------------------------------
                    UUID towerUUID = UUID.fromString("b9407f30-f5f8-466e-aff9-25556b57fe6d");
                    int stage1Major = 1;
                    int stage2Major = 2;

                    int door1Stage1Minor = 1;   //bc1
                    int door2Stage1Minor = 2;   //bc2
                    int door1Stage2Minor = 1;   //bc3

                    BeaconRegion stage1Region = new BeaconRegion(towerUUID,stage1Major,"stage1");
                    BeaconRegion stage2Region = new BeaconRegion(towerUUID,stage2Major,"stage2");

                    BeaconRegion beaconRegion1 = new BeaconRegion(towerUUID,stage1Major,door1Stage1Minor,"beacon1");
                    BeaconRegion beaconRegion2 = new BeaconRegion(towerUUID,stage1Major,door2Stage1Minor,"beacon2");
                    BeaconRegion beaconRegion3 = new BeaconRegion(towerUUID,stage2Major,door1Stage2Minor,"beacon3");

                    lm.startRangingBeacons(stage1Region);
                    lm.startRangingBeacons(stage2Region);

                    lm.startMonitoring(beaconRegion1);
                    lm.startMonitoring(beaconRegion2);
                    lm.startMonitoring(beaconRegion3);

                    //lm.startMonitoring(new BeaconRegion(UUID.fromString("cd5e5fbe-4838-49e4-97d3-b18c6fd5f763"),2,"test1"));
                    //lm.startRangingBeacons(new BeaconRegion(UUID.fromString("cd5e5fbe-4838-49e4-97d3-b18c6fd5f763"),2,"test1"));

                    //lm.stopRangingBeacons(new BeaconRegion(UUID.fromString("cd5e5fbe-4838-49e4-97d3-b18c6fd5f763"),2,"test1"));
                    //lm.stopMonitoring(new BeaconRegion(UUID.fromString("cd5e5fbe-4838-49e4-97d3-b18c6fd5f763"),7,"test2"));
                    //------------------------------
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to use ACCESS_COARSE_LOCATION", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void checkPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                1);
    }

    public void clearAll(View view) {
        adapter.clear();
    }

    //------------------------
    @Override
    public void didRangeBeacons(List<Beacon> beacons, BeaconRegion region) {
        String s = "Region "+region.getIdentifier()+" has "+beacons.size()+" beacon(s)";
        for(Beacon bc : beacons) {
            s += "\n--Beacon("+bc.getProximity().toString()+")"+String.format("%.3f m", bc.getAccuracy());
        }
        adapter.add(s);
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() - 1);
    }

    @Override
    public void rangingBeaconsDidFailFor(BeaconRegion region, Error error) {
        adapter.add("rangingBeaconsDidFailFor: Region: "+region.getIdentifier()+" Error: "+error.getErrorMessage());
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() - 1);
    }

    @Override
    public void didEnterRegion(Region region) {
        adapter.add("<<<<<Enter: "+region.getIdentifier());
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() - 1);
    }

    @Override
    public void didExitRegion(Region region) {
        adapter.add(">>>>>Exit: "+region.getIdentifier());
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() - 1);
    }

    @Override
    public void monitoringDidFailFor(Region region, Error error) {
        adapter.add("monitoringDidFailFor: Region: "+region.getIdentifier()+" Error: "+error.getErrorMessage());
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() - 1);
    }

    @Override
    public void didFailWithError(Error error) {
        adapter.add("didFailWithError: "+error.getErrorMessage());
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() - 1);
    }
    //------------------------
}

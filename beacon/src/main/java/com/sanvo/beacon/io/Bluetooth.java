package com.sanvo.beacon.io;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanSettings;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import com.sanvo.beacon.LocationManager;

/*
  Created by San Vo on 12/12/2017.
 */

public class Bluetooth {
    public static ScanSettings getScanSettings(LocationManager.ScanMode scanMode) {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setReportDelay(0);
        switch (scanMode) {
            case LOW_LATENCY:
                builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
                break;
            case BALANCED:
                builder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
                break;
            case LOW_POWER:
                builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
                break;
        }
        return builder.build();
    }

    public static BluetoothAdapter getBluetoothAdapter() {
        try {
            return BluetoothAdapter.getDefaultAdapter();
        }
        catch (SecurityException se) {
            return null;
        }
    }

    public static BluetoothLeScanner getBluetoothLeScanner(BluetoothAdapter bluetoothAdapter) {
        try {
            return bluetoothAdapter.getBluetoothLeScanner();
        }
        catch (SecurityException se) {
            return null;
        }
    }

    public static boolean hasLocationPermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isLocationOn(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        int mode = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);

        return mode != Settings.Secure.LOCATION_MODE_OFF;
    }

    public static boolean hasBLEFeature(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public static boolean isEnabled(BluetoothAdapter bluetoothAdapter) {
        return bluetoothAdapter.isEnabled();
    }
}

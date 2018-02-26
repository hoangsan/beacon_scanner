package com.sanvo.beacon.handler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.sanvo.beacon.LocationManager;
import com.sanvo.beacon.io.Bluetooth;
import com.sanvo.beacon.object.Beacon;
import com.sanvo.beacon.object.BeaconRegion;
import com.sanvo.beacon.util.ScanFilterParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
  Created by San Vo on 12/12/2017.
 */

public class BLEHandler {
    private static final int BLE_RESCAN_DELAY_TIME = 1000; //1s

    public interface BLEHandlerDelegate {
        void didFail(int errorCode);
        void didFailWithScanFilterParse(String identifier);
        void didFailWithNoBluetoothPermission();
        void didFailWithNoLocationPermission();
        void didFailWithNoBLEFeature();
        void didFailWithBluetoothDisabled();
        void didFailWithLocationOff();

        void didEnterBeacon(Beacon bc);
        void didExitBeacon(Beacon bc);

        void startRangingLoop();
        void stopRangingLoop();
    }

    private final BLEHandlerDelegate _delegate;
    private Context _context;
    private ScanSettings _scanSettings;
    private final ScanCallback _scanCallback;
    private HashMap<String, BeaconRegion> _scannedBeaconRegions;
    private Runnable _delayRescanHandler;
    private LocationManager.ScanMode _scanMode;

    public BLEHandler(Context context, BLEHandlerDelegate delegate) {
        _delegate = delegate;
        _context = context;
        _scanMode = LocationManager.ScanMode.LOW_POWER;
        _scanSettings = Bluetooth.getScanSettings(_scanMode);
        _scanCallback = new ScanResultHandler(_delegate);
    }

    public void setScanMode(LocationManager.ScanMode scanMode) {
        _scanMode = scanMode;
        _scanSettings = Bluetooth.getScanSettings(_scanMode);
    }

    public synchronized void changeScanRegions(final HashMap<String, BeaconRegion> scannedBeaconRegions) {
        _scannedBeaconRegions = scannedBeaconRegions;

        if(_delayRescanHandler==null) { //delay request rescan
            _delayRescanHandler = new Runnable() {
                @Override
                public void run() {
                    reScan();
                    _delayRescanHandler = null;
                }
            };

            new Handler().postDelayed(_delayRescanHandler, BLEHandler.BLE_RESCAN_DELAY_TIME);
        }

        //
        if (scannedBeaconRegions.size() > 0) {
            //listen bluetooth status
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            _registeringBluetoothStatus = true;
            _context.registerReceiver(_bluetoothStatusReceiver, filter);
        }
        else {
            //stop  listen bluetooth status
            if(_registeringBluetoothStatus) {
                _registeringBluetoothStatus = false;
                _context.unregisterReceiver(_bluetoothStatusReceiver);
            }
        }
    }

    private synchronized void reScan() {
        BluetoothAdapter bluetoothAdapter = Bluetooth.getBluetoothAdapter();
        if(bluetoothAdapter == null) {
            if(_delegate != null)
                _delegate.didFailWithNoBluetoothPermission();
            return;
        }

        BluetoothLeScanner bluetoothLeScanner = Bluetooth.getBluetoothLeScanner(bluetoothAdapter);
        if(bluetoothLeScanner == null) {
            if(_delegate != null)
                _delegate.didFailWithNoBluetoothPermission();
            return;
        }

        bluetoothLeScanner.stopScan(_scanCallback);

        if(!checkBLEScannable(bluetoothAdapter)) return;

        if(_scannedBeaconRegions.size() > 0) {
            List<ScanFilter> scanFilters = getScanFilters();
            bluetoothLeScanner.startScan(scanFilters, _scanSettings, _scanCallback);

            if(_delegate != null)
                _delegate.startRangingLoop();
        }
    }
    private List<ScanFilter> getScanFilters() {
        if(_scannedBeaconRegions.size() > 0) {
            ArrayList<ScanFilter> scanFilters = new ArrayList();

            for (String key : _scannedBeaconRegions.keySet()) {
                try {
                    scanFilters.add(ScanFilterParser.fromBeaconRegion(_scannedBeaconRegions.get(key)));
                } catch (IOException e) {
                    if (_delegate != null)
                        _delegate.didFailWithScanFilterParse(key);
                }
            }

            return scanFilters;
        }
        return null;
    }

    private boolean checkBLEScannable(BluetoothAdapter bluetoothAdapter) {
        if (!Bluetooth.hasLocationPermission(_context)) {
            if(_delegate != null)
                _delegate.didFailWithNoLocationPermission();
            return false;
        }

        //
        if (!Bluetooth.hasBLEFeature(_context)) {
            if(_delegate != null)
                _delegate.didFailWithNoBLEFeature();
            return false;
        }

        //
        if (!Bluetooth.isEnabled(bluetoothAdapter)) {
            if(_delegate != null)
                _delegate.didFailWithBluetoothDisabled();
            return false;
        }

        //
        if (!Bluetooth.isLocationOn(_context)) {
            if(_delegate != null)
                _delegate.didFailWithLocationOff();
            return false;
        }

        return true;
    }

    private boolean _registeringBluetoothStatus = false;
    private final BroadcastReceiver _bluetoothStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if(_delegate != null) {
                            _delegate.stopRangingLoop();
                            _delegate.didFailWithBluetoothDisabled();
                        }
                        break;
                    case BluetoothAdapter.STATE_ON:
                        BLEHandler.this.reScan();

                        if(_delegate != null)
                            _delegate.startRangingLoop();
                        break;
                }
            }
        }
    };
}

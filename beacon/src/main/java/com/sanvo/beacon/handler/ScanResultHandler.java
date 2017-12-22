package com.sanvo.beacon.handler;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.sanvo.beacon.io.BeaconPacket;
import com.sanvo.beacon.object.Beacon;

import java.util.List;

/*
  Created by San Vo on 12/12/2017.
 */

class ScanResultHandler extends ScanCallback {
    private final BeaconManager _beaconManager;
    private final BLEHandler.BLEHandlerDelegate _delegate;

    public ScanResultHandler(BLEHandler.BLEHandlerDelegate delegate) {
        super();
        _delegate = delegate;
        _beaconManager = BeaconManager.getInstance();
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        super.onBatchScanResults(results);
        Log.w("BLEHandler","onBatchScanResults : setReportDelay = 0, skip this method");
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
        Log.i("Beacon",result.toString());
        if(result.getScanRecord() != null) {
            Beacon bc = _beaconManager.isNewBeacon(new BeaconPacket(result.getScanRecord().getBytes(), result.getRssi()));
            if (bc != null && _delegate != null) {
                _delegate.didEnterBeacon(bc);
            }
        }
    }

    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);

        if(_delegate != null)
            _delegate.didFail(errorCode);
    }
}

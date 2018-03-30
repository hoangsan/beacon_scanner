package com.sanvo.beacon;

import android.content.Context;
import android.os.Handler;

import com.sanvo.beacon.object.Beacon;
import com.sanvo.beacon.object.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sanvo.beacon.handler.BLEHandler;
import com.sanvo.beacon.handler.BeaconManager;
import com.sanvo.beacon.object.BeaconRegion;
import com.sanvo.beacon.object.Error;

/*
  Created by San Vo on 12/12/2017.
 */

/**
 * Singleton object which you use to start and stop the delivery of location-related events to your app.
 */
public class LocationManager {
    public enum ScanMode {
        LOW_LATENCY,
        BALANCED,
        LOW_POWER
    }
    private static final int RANGING_LOOP_INTERVAL = 2000;//ms

    private static LocationManager _instance;
    private LocationManagerDelegate _delegate;
    private BLEHandler _bleHandler;
    private Handler _rangingHandler;
    private Runnable _rangingLooper;
    private BeaconManager _beaconManager;

    private ConcurrentHashMap<String,Region> _monitoredRegions;
    private ConcurrentHashMap <String,BeaconRegion> _rangingBeacons;

    private LocationManager(Context context) {
        _bleHandler = new BLEHandler(context, _bleResponseHandler);
        _monitoredRegions = new ConcurrentHashMap();
        _rangingBeacons = new ConcurrentHashMap();

        _beaconManager = BeaconManager.getInstance();
        _beaconManager.setBLEHandlerDelegate(_bleResponseHandler);

        _rangingHandler = new Handler();
        _rangingLooper = new Runnable() {
            @Override
            public void run() {
                if(_delegate != null) {
                    List<Beacon> beacons = _beaconManager.getCurrentBeacons();

                    Iterator iter = _rangingBeacons.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry pair = (Map.Entry)iter.next();
                        String key = (String) pair.getKey();
                        BeaconRegion bcr = (BeaconRegion) pair.getValue();

                        ArrayList<Beacon> beaconSet = new ArrayList();

                        for(Beacon bc : beacons) {
                            if (key.equals(BeaconManager.makeUniqueKeyOfBeaconInSpecifiedRegion(bc,bcr))
                                    && _delegate != null) {
                                beaconSet.add(bc);
                            }
                        }

                        _delegate.didRangeBeacons(beaconSet,bcr);
                    }
                }

                //loop
                if(_rangingBeacons != null && _rangingBeacons.size() != 0) {
                    _bleResponseHandler.startRangingLoop();
                }
            }
        };
    }

    private void handleOnRequestedRegionChanged() {
        HashMap<String,BeaconRegion> scannedBeaconRegions = new HashMap();

        Iterator iterator = _rangingBeacons.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            scannedBeaconRegions.put((String)pair.getKey(),(BeaconRegion)pair.getValue());
        }

        iterator = _monitoredRegions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            if(pair.getValue() instanceof BeaconRegion)
                scannedBeaconRegions.put((String)pair.getKey(),(BeaconRegion)pair.getValue());
        }

        _bleHandler.changeScanRegions(scannedBeaconRegions);
    }

    //----------------BLEHandler.BLEHandlerDelegate
    private BLEHandler.BLEHandlerDelegate _bleResponseHandler = new BLEHandler.BLEHandlerDelegate() {
        @Override
        public void didFail(int errorCode) {
            if(_delegate != null) {
                _delegate.didFailWithError(new Error("didFail"+errorCode));
            }
        }

        @Override
        public void didFailWithScanFilterParse(String uniqueKey) {
            if(_delegate != null) {
                Error error = new Error("didFailWithScanFilterParse");

                if(_monitoredRegions.containsKey(uniqueKey))
                    _delegate.monitoringDidFailFor(_monitoredRegions.get(uniqueKey),error);
                if(_rangingBeacons.containsKey(uniqueKey))
                    _delegate.rangingBeaconsDidFailFor(_rangingBeacons.get(uniqueKey),error);
            }
        }

        @Override
        public void didFailWithNoBluetoothPermission() {
            if(_delegate != null) {
                _delegate.didFailWithError(new Error("didFailWithNoBluetoothPermission"));
            }
        }

        @Override
        public void didFailWithNoLocationPermission() {
            if(_delegate != null) {
                _delegate.didFailWithError(new Error("didFailWithNoLocationPermission"));
            }
        }

        @Override
        public void didFailWithNoBLEFeature() {
            if(_delegate != null) {
                _delegate.didFailWithError(new Error("didFailWithNoBLEFeature"));
            }
        }

        @Override
        public void didFailWithBluetoothDisabled() {
            if(_delegate != null) {
                _delegate.didFailWithError(new Error("didFailWithBluetoothDisabled"));
            }
        }

        @Override
        public void didFailWithLocationOff() {
            if(_delegate != null) {
                _delegate.didFailWithError(new Error("didFailWithLocationOff"));
            }
        }

        @Override
        public void didEnterBeacon(Beacon bc) {
            Iterator iter = _monitoredRegions.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry pair = (Map.Entry)iter.next();
                String key = (String) pair.getKey();
                if(pair.getValue() instanceof BeaconRegion) {
                    BeaconRegion bcr = (BeaconRegion) pair.getValue();
                    if(key.equals(BeaconManager.makeUniqueKeyOfBeaconInSpecifiedRegion(bc,bcr))
                            && _delegate != null) {
                        _delegate.didEnterRegion(bcr);
                        return;
                    }
                }
            }
        }

        @Override
        public void didExitBeacon(Beacon bc) {
            Iterator iter = _monitoredRegions.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry pair = (Map.Entry)iter.next();
                String key = (String) pair.getKey();
                if(pair.getValue() instanceof BeaconRegion) {
                    BeaconRegion bcr = (BeaconRegion) pair.getValue();
                    if(key.equals(BeaconManager.makeUniqueKeyOfBeaconInSpecifiedRegion(bc,bcr))
                            && _delegate != null) {
                        _delegate.didExitRegion(bcr);
                        return;
                    }
                }
            }
        }

        @Override
        public void startRangingLoop() {
            _rangingHandler.removeCallbacks(_rangingLooper);
            _rangingHandler.postDelayed(_rangingLooper, RANGING_LOOP_INTERVAL);
        }

        @Override
        public void stopRangingLoop() {
            _rangingHandler.removeCallbacks(_rangingLooper);
        }
    };

    //----------------Public

    /**
     * Get singleton instance.
     *
     * @param context The context.
     * @return Instance of LocationManager.
     */
    public static synchronized LocationManager getInstance(Context context) {
        if(_instance == null) {
            _instance = new LocationManager(context);
        }

        return _instance;
    }

    /**
     * The methods that you use to receive events from an associated location manager object.
     *
     * @param delegate The inputted delegate.
     */
    public void setLocationManagerDelegate(LocationManagerDelegate delegate) {
        _delegate = delegate;
    }

    /**
     * Starts monitoring the specified region.
     *
     * @param region The region object that defines the boundary to monitor. This parameter must not be null.
     */
    public void startMonitoring(Region region) {
        if(region!= null && region instanceof BeaconRegion) {
            _monitoredRegions.put(BeaconManager.makeUniqueKeyOfBeaconRegion((BeaconRegion) region), region);

            this.handleOnRequestedRegionChanged();
        }
    }

    /**
     * Stops monitoring the specified region.
     *
     * @param region The region object currently being monitored. This parameter must not be null.
     */
    public void stopMonitoring(Region region) {
        if(region == null) return;

        Iterator iter = _monitoredRegions.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry)iter.next();
            String key = (String) pair.getKey();
            BeaconRegion bcr = (BeaconRegion) pair.getValue();
            if(bcr.getIdentifier().equals(region.getIdentifier())) {
                _monitoredRegions.remove(key);
            }
        }

        this.handleOnRequestedRegionChanged();
    }

    /**
     * Starts the delivery of notifications for the specified beacon region.
     *
     * @param region The region object that defines the identifying information for the targeted beacons. The number of beacons represented by this region object depends on which identifier values you use to initialize it. Beacons must match all of the identifiers you specify. This method copies the region information it needs from the object you provide.
     */
    public void startRangingBeacons(BeaconRegion region) {
        _rangingBeacons.put(BeaconManager.makeUniqueKeyOfBeaconRegion(region),region);

        this.handleOnRequestedRegionChanged();
    }

    /**
     * Stops the delivery of notifications for the specified beacon region.
     *
     * @param region The region that identifies the beacons. The object you specify need not be the exact same object that you registered but the beacon attributes should be the same.
     */
    public void stopRangingBeacons(BeaconRegion region) {
        Iterator iter = _rangingBeacons.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry)iter.next();
            String key = (String) pair.getKey();
            BeaconRegion bcr = (BeaconRegion) pair.getValue();
            if(bcr.getIdentifier().equals(region.getIdentifier())) {
                _rangingBeacons.remove(key);
            }
        }

        this.handleOnRequestedRegionChanged();
    }

    /**
     * Set interval for scanning exiting beacon region.
     *
     * @param monitoringInterval The interval for scanning exiting beacon region.
     */
    public void setMonitoringInterval(int monitoringInterval) {
        BeaconManager.getInstance().setMonitoringInterval(monitoringInterval);
    }

    /**
     * Stop all ranging, monitoring
     */
    public void stopAll() {
        _monitoredRegions.clear();
        _rangingBeacons.clear();

        this.handleOnRequestedRegionChanged();
    }

    /**
     * Set scan mode for scanning beacon. Just apply until request new ranging/monitoring.
     * LOW_LATENCY : Scan using highest duty cycle. It's recommended to only use this mode when the application is running in the foreground.
     * BALANCED : Perform Bluetooth LE scan in balanced power mode. Scan results are returned at a rate that provides a good trade-off between scan frequency and power consumption.
     * LOW_POWER : Perform Bluetooth LE scan in low power mode. This is the default scan mode as it consumes the least power.
     *
     * @param scanMode The mode for scanning beacon.
     */
    public void setScanMode(ScanMode scanMode) {
        _bleHandler.setScanMode(scanMode);
    }
}
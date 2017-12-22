package com.sanvo.beacon.handler;

import android.os.Handler;
import android.util.SparseArray;

import com.sanvo.beacon.io.BeaconPacket;
import com.sanvo.beacon.object.Beacon;
import com.sanvo.beacon.object.BeaconRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/*
  Created by San Vo on 12/12/2017.
 */

public class BeaconManager implements ExitRegionHandler.ExitRegionHandlerDelegate {
    private static final int DEFAULT_MONITORING_INTERVAL = 1000;//ms

    private static BeaconManager _instance;

    private BLEHandler.BLEHandlerDelegate _delegate;

    private HashMap<String,Beacon> _currentBeacons;
    private final Handler _exitRegionHandler;
    private SparseArray<Runnable> _exitRegionCheckers;

    private int _monitoringInterval;

    private BeaconManager() {
        _currentBeacons = new HashMap();
        _exitRegionHandler = new Handler();
        _exitRegionCheckers = new SparseArray();
        _monitoringInterval = DEFAULT_MONITORING_INTERVAL;
    }

    private static String makeUniqueKeyFromUUIDMajorMinor(UUID uuid,  int major, int minor) {
        return uuid.toString()+major+minor;
    }

    //------------
    public static String makeUniqueKeyOfBeaconRegion(BeaconRegion bcr) {
        return bcr.getProximityUUID().toString()+bcr.getMajor()+bcr.getMinor();
    }

    public static String makeUniqueKeyOfBeaconInSpecifiedRegion(Beacon bc, BeaconRegion bcr) {
        return BeaconManager.makeUniqueKeyFromUUIDMajorMinor(bc.getProximityUUID(), bcr.getMajor() == -1 ? -1 : bc.getMajor(), bcr.getMinor() == -1 ? -1 : bc.getMinor());
    }

    public static BeaconManager getInstance() {
        if(_instance == null) {
            _instance = new BeaconManager();
        }

        return _instance;
    }

    public Beacon isNewBeacon(BeaconPacket bcp) {
        if(bcp.getProximityUUID() == null) return null;

        String uniqueKey = makeUniqueKeyFromUUIDMajorMinor(bcp.getProximityUUID(),bcp.getMajor(),bcp.getMinor());
        int uniqueKeyNumber = uniqueKey.hashCode();
        if(!_currentBeacons.containsKey(uniqueKey)) {
            Beacon bc = new Beacon(bcp);
            _currentBeacons.put(uniqueKey,bc);

            //
            Runnable exitRegionChecker = new ExitRegionHandler(this, uniqueKey);
            _exitRegionCheckers.append(uniqueKeyNumber,exitRegionChecker);
            _exitRegionHandler.postDelayed(exitRegionChecker,_monitoringInterval);

            return bc;
        }
        else {
            _currentBeacons.get(uniqueKey).setRssi(bcp.getReceivedSignalPower(),bcp.getSignalPower());

            //
            Runnable exitRegionChecker = _exitRegionCheckers.get(uniqueKeyNumber);
            _exitRegionHandler.removeCallbacks(exitRegionChecker);
            _exitRegionHandler.postDelayed(exitRegionChecker, _monitoringInterval);

            return null;
        }
    }

    public List<Beacon> getCurrentBeacons() {
        return new ArrayList(_currentBeacons.values());
    }

    public void setBLEHandlerDelegate(BLEHandler.BLEHandlerDelegate delegate) {
        _delegate = delegate;
    }

    public void setMonitoringInterval(int monitoringInterval) {
        _monitoringInterval = monitoringInterval;
    }
    
    //---------ExitRegionHandlerDelegate
    @Override
    public void onExitRegion(String uniqueKey) {
        if(_delegate != null && _currentBeacons.containsKey(uniqueKey)) {
            _delegate.didExitBeacon(_currentBeacons.get(uniqueKey));
            _currentBeacons.remove(uniqueKey);
        }
    }
}

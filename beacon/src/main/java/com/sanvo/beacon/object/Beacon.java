package com.sanvo.beacon.object;

import java.util.UUID;

import com.sanvo.beacon.io.BeaconPacket;

/*
  Created by San Vo on 12/12/2017.
 */

/**
 * Information about a detected iBeacon and the relative distance to it.
 */
public class Beacon {
    /**
     * Constants that reflect the relative distance to a beacon.
     * Immediate: 0 - 0.2m
     * Near: 0.2m - 3m
     * Far: 3m - 70m
     */
    public enum Proximity {
          unknown, immediate, near, far
    }

    private final UUID _proximityUUID;
    private final int _major;
    private final int _minor;
    private Proximity _proximity;
    private int _rssi;
    private double _accuracy;

    /**
     * Create Beacon from {@link BeaconPacket}
     *
     * @param bcp The BeaconPacket object.
     */
    public Beacon(BeaconPacket bcp) {
        _proximityUUID = bcp.getProximityUUID();
        _major = bcp.getMajor();
        _minor = bcp.getMinor();

        setRssi(bcp.getReceivedSignalPower(),bcp.getSignalPower());
    }

    /**
     * Get proximity ID of the beacon.
     *
     * @return The proximity ID of the beacon.
     */
    public UUID getProximityUUID() {
        return _proximityUUID;
    }

    /**
     * Get most significant value in the beacon.
     *
     * @return The most significant value in the beacon.
     */
    public int getMajor() {
        return _major;
    }

    /**
     * Get least significant value in the beacon.
     *
     * @return The least significant value in the beacon.
     */
    public int getMinor() {
        return _minor;
    }

    /**
     * Get received signal strength of the beacon, measured in decibels.
     *
     * @return The received signal strength of the beacon, measured in decibels.
     */
    public int getRssi() {
        return _rssi;
    }

    /**
     * Set received signal strength of the beacon, measured in decibels.
     *
     * @param receivedRssi The received signal strength of the beacon, measured in decibels.
     */
    public void setRssi(int receivedRssi, int signalPower) {
        _rssi = receivedRssi;

        _accuracy = Math.pow(10, (signalPower - receivedRssi) / (20.0*2));

        if(_accuracy <= 0.2) {
            _proximity = Proximity.immediate;
        }
        else if(_accuracy > 0.2 && _accuracy <= 3) {
            _proximity = Proximity.near;
        }
        else if(_accuracy > 3 && _accuracy <= 70) {
            _proximity = Proximity.far;
        }
        else {
            _proximity = Proximity.unknown;
        }
    }

    /**
     * Get accuracy of the proximity value, measured in meters from the beacon.
     *
     * @return The accuracy of the proximity value, measured in meters from the beacon.
     */
    public Proximity getProximity() {
        return _proximity;
    }

    public double getAccuracy() {
        return _accuracy;
    }
}

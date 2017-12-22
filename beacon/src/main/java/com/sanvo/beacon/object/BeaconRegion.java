package com.sanvo.beacon.object;

import java.util.UUID;

/*
  Created by San Vo on 12/12/2017.
 */

/**
 * A region used to detect iBeacon hardware.
 */
public class BeaconRegion extends Region {
    private final UUID _proximityUUID;
    private final int _major;
    private final int _minor;

    /**
     * Constructor with proximityUUID, identifier.
     *
     * @param proximityUUID The unique ID of the beacons being targeted.
     * @param identifier The identifier for the region object.
     */
    public BeaconRegion(UUID proximityUUID, String identifier) {
        super(identifier);

        _proximityUUID = proximityUUID;
        _major = -1;
        _minor = -1;
    }

    /**
     * Constructor with proximityUUID, major, identifier.
     *
     * @param proximityUUID The unique ID of the beacons being targeted.
     * @param major The value identifying a group of beacons.
     * @param identifier The identifier for the region object.
     */
    public BeaconRegion(UUID proximityUUID, int major, String identifier) {
        super(identifier);

        _proximityUUID = proximityUUID;
        _major = major;
        _minor = -1;
    }

    /**
     * Constructor with proximityUUID, major, minor, identifier.
     *
     * @param proximityUUID The unique ID of the beacons being targeted.
     * @param major The value identifying a group of beacons.
     * @param minor The value identifying a specific beacon within a group.
     * @param identifier The identifier for the region object.
     */
    public BeaconRegion(UUID proximityUUID, int major, int minor, String identifier) {
        super(identifier);

        _proximityUUID = proximityUUID;
        _major = major;
        _minor = minor;
    }

    /**
     * Get unique ID of the beacons being targeted.
     *
     * @return The unique ID of the beacons being targeted.
     */
    public UUID getProximityUUID() {
        return _proximityUUID;
    }

    /**
     * Get value identifying a group of beacons.
     *
     * @return The value identifying a group of beacons.
     */
    public int getMajor() {
        return _major;
    }

    /**
     * Get value identifying a specific beacon within a group.
     *
     * @return The value identifying a specific beacon within a group.
     */
    public int getMinor() {
        return _minor;
    }

}

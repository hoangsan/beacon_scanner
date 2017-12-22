package com.sanvo.beacon;

import com.sanvo.beacon.object.Beacon;
import com.sanvo.beacon.object.Region;

import java.util.List;

import com.sanvo.beacon.object.BeaconRegion;
import com.sanvo.beacon.object.Error;

/*
  Created by San Vo on 12/12/2017.
 */

/**
 * The methods that you use to receive events from an associated location manager object.
 */
public interface LocationManagerDelegate {
    /**
     * Tells the delegate that one or more beacons are in range.
     *
     * @param beacons An array of {@link Beacon} objects representing the beacons currently in range. If beacons is empty, you can assume that no beacons matching the specified region are in range. When a specific beacon is no longer in beacons, that beacon is no longer received by the device. You can use the information in the {@link Beacon} objects to determine the range of each beacon and its identifying information.
     * @param region The region object containing the parameters that were used to locate the beacons.
     * @see Beacon
     */
    void didRangeBeacons(List<Beacon> beacons, BeaconRegion region);

    /**
     * Tells the delegate that an error occurred while gathering ranging information for a set of beacons.
     *
     * @param region The region object that encountered the error.
     * @param error An error object containing the error code that indicates why ranging failed.
     */
    void rangingBeaconsDidFailFor(BeaconRegion region, Error error);

    /**
     * Tells the delegate that the user entered the specified region.
     *
     * @param region An object containing information about the region that was entered.
     */
    void didEnterRegion(Region region);

    /**
     * Tells the delegate that the user left the specified region.
     *
     * @param region An object containing information about the region that was exited.
     */
    void didExitRegion(Region region);

    /**
     * Tells the delegate that a region monitoring error occurred.
     *
     * @param region The region for which the error occurred.
     * @param error An error object containing the error code that indicates why region monitoring failed.
     */
    void monitoringDidFailFor(Region region, Error error);

    /**
     * Tells the delegate that the location manager was unable to retrieve a location value.
     *
     * @param error The error object containing the reason the location or heading could not be retrieved.
     */
    void didFailWithError(Error error);
}

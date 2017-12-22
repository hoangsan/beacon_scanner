package com.sanvo.beacon.util;

import android.bluetooth.le.ScanFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.sanvo.beacon.object.BeaconRegion;

/*
  Created by San Vo on 12/12/2017.
 */

public class ScanFilterParser {

    public static ScanFilter fromBeaconRegion(BeaconRegion beaconRegion) throws IOException{
        //uuid:     bit#2,  length 16
        //major:    bit#18, length 2
        //minor:    bit#20, length 2

        ByteArrayOutputStream manufacturerDataOs = new ByteArrayOutputStream();
        ByteArrayOutputStream manufacturerDataMaskOs = new ByteArrayOutputStream();

        //0-1
        Binary.write16bits(manufacturerDataOs,0);
        Binary.write16bits(manufacturerDataMaskOs,0);

        //2-17
        if(beaconRegion.getProximityUUID() != null) {
            Binary.writeUUID(manufacturerDataOs,beaconRegion.getProximityUUID());
            Binary.fillIntValue(manufacturerDataMaskOs,1,16);
        }
        else {
            Binary.fillIntValue(manufacturerDataOs,0,16);
            Binary.fillIntValue(manufacturerDataMaskOs,0,16);
        }

        //18-19
        if(beaconRegion.getMajor() != -1) {
            Binary.write16bits(manufacturerDataOs,beaconRegion.getMajor());
            Binary.write16bits(manufacturerDataMaskOs,1);
        }
        else {
            Binary.write16bits(manufacturerDataOs,0);
            Binary.write16bits(manufacturerDataMaskOs,0);
        }

        //20-21
        if(beaconRegion.getMinor() != -1) {
            Binary.write16bits(manufacturerDataOs,beaconRegion.getMinor());
            Binary.write16bits(manufacturerDataMaskOs,1);
        }
        else {
            Binary.write16bits(manufacturerDataOs,0);
            Binary.write16bits(manufacturerDataMaskOs,0);
        }

        //22
        Binary.write8bits(manufacturerDataOs,0);
        Binary.write8bits(manufacturerDataMaskOs,0);

        ScanFilter.Builder builder = new ScanFilter.Builder();
        builder.setManufacturerData(
                76,
                manufacturerDataOs.toByteArray(),
                manufacturerDataMaskOs.toByteArray());

        return builder.build();
    }
}

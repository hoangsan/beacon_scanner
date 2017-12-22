package com.sanvo.beacon.io;

import java.util.Arrays;
import java.util.UUID;

import com.sanvo.beacon.util.Binary;

/*
  Created by San Vo on 12/12/2017.
 */

public class BeaconPacket {
    private UUID _proximityUUID;
    private int _major;
    private int _minor;
    private int _signalPower;

    private int _receivedSignalPower;

    public BeaconPacket(byte[] data, int receivedSignalPower) {
//Standard BLE Flags
//        Byte 0: Length :  0x02
//        Byte 1: Type: 0x01 (Flags)
//        Byte 2: Value: 0x06 (Typical Flags)
//Apple Defined iBeacon Data
//        Byte 3: Length: 0x1a
//        Byte 4: Type: 0xff (Custom Manufacturer Packet)
//        Byte 5-6: Manufacturer ID : 0x4c00 (Apple)
//        Byte 7: SubType: 0x2 (iBeacon)
//        Byte 8: SubType Length: 0x15
//        Byte 9-24: Proximity UUID
//        Byte 25-26: Major
//        Byte 27-28: Minor
//        Byte 29: Signal Power
        if(data == null || data.length < 1) return;

        int shiftStdBleFlgBit = 0;
        if(data[0] == 0x1a) //has no Standard BLE Flags
            shiftStdBleFlgBit = 3;

        if(data[3-shiftStdBleFlgBit] < 0x1a && data[7-shiftStdBleFlgBit] != 0x2) return;

        _proximityUUID = Binary.readUUID(Arrays.copyOfRange(data,9-shiftStdBleFlgBit,25-shiftStdBleFlgBit));
        _major = Binary.read2Bytes(data[25-shiftStdBleFlgBit],data[26-shiftStdBleFlgBit]);
        _minor = Binary.read2Bytes(data[27-shiftStdBleFlgBit],data[28-shiftStdBleFlgBit]);
        _signalPower = data[29-shiftStdBleFlgBit];

        _receivedSignalPower = receivedSignalPower;
    }

    public UUID getProximityUUID() {
        return _proximityUUID;
    }

    public int getMajor() {
        return _major;
    }

    public int getMinor() {
        return _minor;
    }

    public int getSignalPower() {
        return _signalPower;
    }

    public int getReceivedSignalPower() {
        return _receivedSignalPower;
    }
}

package com.sanvo.beacon.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

/*
  Created by San Vo on 12/12/2017.
 */

public class Binary {
    public static void write8bits(OutputStream out, int value) throws IOException {
        out.write((byte) value);
    }

    public static void write16bits(OutputStream out, int value) throws IOException {
        out.write((byte) (value >>> 8));
        out.write((byte) value);
    }

    public static void writeUUID(OutputStream out, UUID uuid) throws IOException {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        out.write(bb.array(),0,16);
    }

    public static void fillIntValue(OutputStream out, int value, int length) throws IOException {
        for(int i =0; i<length; i++) {
            out.write((byte) value);
        }
    }

    public static int read2Bytes(int hByte, int lByte) {
        return ((hByte & 0xff) << 8) | (lByte & 0xff);
    }

    public static UUID readUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        Long high = byteBuffer.getLong();
        Long low = byteBuffer.getLong();

        return new UUID(high, low);
    }
}

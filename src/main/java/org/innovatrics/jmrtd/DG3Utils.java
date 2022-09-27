package org.innovatrics.jmrtd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmrtd.cbeff.StandardBiometricHeader;
import org.jmrtd.lds.iso19794.FingerImageInfo;
import org.jmrtd.lds.iso19794.FingerInfo;

public class DG3Utils {

    public enum FingerprintPosition {
        UNKNOWN((byte) 0x00),
        RIGHT_THUMB((byte) 0x01),
        RIGHT_INDEX((byte) 0x02),
        RIGHT_MIDDLE((byte) 0x03),
        RIGHT_RING((byte) 0x04),
        RIGHT_LITTLE((byte) 0x05),
        LEFT_THUMB((byte) 0x06),
        LEFT_INDEX((byte) 0x07),
        LEFT_MIDDLE((byte) 0x08),
        LEFT_RING((byte) 0x09),
        LEFT_LITTLE((byte) 0x0A),
        ;

        private final byte value;

        FingerprintPosition(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

    }

    
  
    public static FingerInfo generateFingerInfo(FingerprintPosition fpos, File image) throws IOException {
        Map<Integer, byte[]> elements = new HashMap<Integer, byte[]>();
        // ICAO Haader
        {
            int key = Byte.toUnsignedInt((byte) 0x80);
            byte[] value = { (byte) 0x01, (byte) 0x01 };
            elements.put(key, value);
        }
        // Biometric type
        {
            int key = Byte.toUnsignedInt((byte) 0x81);
            byte[] value = { (byte) 0x08 };
            elements.put(key, value);
        }
        // Biometric sub-type (position)
        {
            int key = Byte.toUnsignedInt((byte) 0x82);
            byte[] value = { fpos.getValue() };
            elements.put(key, value);
        }

        // Format owner
        {
            int key = Byte.toUnsignedInt((byte) 0x87);
            byte[] value = { (byte) 0x01, (byte) 0x01 };
            elements.put(key, value);
        }
        // Format type
        {
            int key = Byte.toUnsignedInt((byte) 0x88);
            byte[] value = { (byte) 0x00, (byte) 0x07 };
            elements.put(key, value);
        }

        StandardBiometricHeader sbh = new StandardBiometricHeader(elements);
        int captureDeviceId = 0;
        int acquisitionLevel = 31;
        int scaleUnits = 1;
        int scanResolutionHorizontal = 500;
        int scanResolutionVertical = 500;
        int imageResolutionHorizontal = 500;
        int imageResolutionVertical = 500;
        int depth = 8;
        int compressionAlgorithm = 2;

        List<FingerImageInfo> fingerImageInfos = new ArrayList<FingerImageInfo>();
        int position = fpos.getValue();
        int viewCount = 1;
        int viewNumber = 1;
        int quality = 50;
        int impressionType = 0;
        int width = 512;
        int height = 512;
        InputStream imageBytes = new FileInputStream(image);
        int imageLength = imageBytes.available();
        FingerImageInfo fii = new FingerImageInfo(position, viewCount, viewNumber, quality, impressionType, width,
                height, imageBytes, imageLength, compressionAlgorithm);
        fingerImageInfos.add(fii);

        return new FingerInfo(sbh, captureDeviceId, acquisitionLevel, scaleUnits, scanResolutionHorizontal,
                scanResolutionVertical, imageResolutionHorizontal, imageResolutionVertical, depth,
                compressionAlgorithm, fingerImageInfos);

    }

}

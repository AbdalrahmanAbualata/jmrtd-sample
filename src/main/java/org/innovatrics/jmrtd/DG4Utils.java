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
import org.jmrtd.lds.iso19794.IrisImageInfo;
import org.jmrtd.lds.iso19794.IrisInfo;
import org.jmrtd.lds.iso19794.IrisBiometricSubtypeInfo;

public class DG4Utils {

    public enum IrisPosition {
        UNKNOWN((byte) 0x00),
        RIGHT((byte) 0x01),
        LEFT((byte) 0x02),

        ;

        private final byte value;

        IrisPosition(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

    }

    public static IrisInfo generateIrisInfo(IrisPosition ipos, File image) throws IOException {
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
            byte[] value = { (byte) 0x10 };
            elements.put(key, value);
        }
        // Biometric sub-type (position)
        {
            int key = Byte.toUnsignedInt((byte) 0x82);
            byte[] value = { ipos.getValue() };
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
            byte[] value = { (byte) 0x00, (byte) 0x09 };
            elements.put(key, value);
        }

        StandardBiometricHeader sbh = new StandardBiometricHeader(elements);
        int captureDeviceId = IrisInfo.CAPTURE_DEVICE_UNDEF;
        int horizontalOrientation = IrisInfo.ORIENTATION_UNDEF;
        int verticalOrientation=IrisInfo.ORIENTATION_UNDEF;
        int scanType= IrisInfo.SCAN_TYPE_UNDEF;
        int irisOcclusion=IrisInfo.IROCC_UNDEF;
        int occlusionFilling=0;
        int boundaryExtraction=0;
        int irisDiameter=0;
        int imageFormat =16;
        int rawImageWidth=640;
        int rawImageHeight=480;
        int intensityDepth=IrisInfo.INTENSITY_DEPTH_UNDEF;
        byte[] deviceUniqueId = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int imageTransformation=IrisInfo.TRANS_UNDEF;

        List<IrisImageInfo> irisImageInfos = new ArrayList<IrisImageInfo>();
        int imageNumber = 1;
        int width = 640;
        int height = 480;
        int position =(int)ipos.getValue();

        InputStream imageBytes = new FileInputStream(image);

        int imageLength = imageBytes.available();

        IrisImageInfo fii = new IrisImageInfo(imageNumber, width, height,imageBytes,imageLength,imageFormat);
                irisImageInfos.add(fii);

                List<IrisBiometricSubtypeInfo> irisBiometricSubtypeInfos = new ArrayList<IrisBiometricSubtypeInfo>();
        IrisBiometricSubtypeInfo biometricSubtypeInfo = new IrisBiometricSubtypeInfo(position,imageFormat,irisImageInfos);
        irisBiometricSubtypeInfos.add(biometricSubtypeInfo);       

        return new IrisInfo(sbh, captureDeviceId,horizontalOrientation,verticalOrientation,scanType,irisOcclusion,occlusionFilling,boundaryExtraction
        ,irisDiameter,imageFormat,rawImageWidth,rawImageHeight,intensityDepth,imageTransformation,deviceUniqueId,irisBiometricSubtypeInfos);

    }

}

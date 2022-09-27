package org.innovatrics.jmrtd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import net.sf.scuba.data.Gender;
import org.jmrtd.cbeff.StandardBiometricHeader;
import org.jmrtd.lds.iso19794.FaceInfo;
import org.jmrtd.lds.iso19794.FaceImageInfo.EyeColor;
import org.jmrtd.lds.iso19794.FaceImageInfo.FeaturePoint;
import org.jmrtd.lds.iso19794.FaceImageInfo;

public class DG2Utils {
      
    public static FaceInfo generateFaceInfo( File image) throws IOException {
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
            byte[] value = { (byte) 0x02 };
            elements.put(key, value);
        }
        // Biometric sub-type (position)
        {
            int key = Byte.toUnsignedInt((byte) 0x82);
            byte[] value = { (byte) 0x00 };
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
            byte[] value = { (byte) 0x00, (byte) 0x08 };
            elements.put(key, value);
        }

        StandardBiometricHeader sbh = new StandardBiometricHeader(elements);
   

        List<FaceImageInfo> faceImageInfos = new ArrayList<FaceImageInfo>();
        Gender gender = Gender.UNSPECIFIED;
        EyeColor eyeColor = EyeColor.UNSPECIFIED;
        int hairColor = FaceImageInfo.HAIR_COLOR_UNSPECIFIED;
        int featureMask = 0;
        int expression = FaceImageInfo.EXPRESSION_UNSPECIFIED;
        int[] poseAngle = { 0, 0, 0 };
        int[] poseAngleUncertainty = { 0, 0, 0 };
        int faceImageType = FaceImageInfo.FACE_IMAGE_TYPE_TOKEN_FRONTAL;
        int colorSpace = 0x00;
        int sourceType = FaceImageInfo.SOURCE_TYPE_UNSPECIFIED;
        int deviceType = 0x0000;
        int quality = 0x0000;
        int imageDataType = FaceImageInfo.IMAGE_DATA_TYPE_JPEG2000;	
        FeaturePoint[] featurePoint = {new FeaturePoint(0, 0,0, 0, 0)};
        int width = 512;
        int height = 512;
        InputStream imageBytes = new FileInputStream(image);
        int imageLength = imageBytes.available();
        FaceImageInfo fii = new FaceImageInfo(gender,eyeColor,featureMask,hairColor,expression,poseAngle,poseAngleUncertainty,faceImageType,colorSpace,
        sourceType,deviceType,quality,featurePoint,width,height,imageBytes,imageLength,imageDataType);
        faceImageInfos.add(fii);

        return new FaceInfo(sbh,faceImageInfos);

    }

}

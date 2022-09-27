package org.innovatrics.jmrtd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;

import org.jmrtd.lds.icao.DG2File;

import org.jmrtd.lds.iso19794.FaceInfo;


public class App2 {

    public static String generateASNfromDG2(byte[] dg2File) throws IOException {
        ASN1InputStream input = new ASN1InputStream(dg2File);

        ASN1Primitive p = input.readObject();

        input.close();

        return StringUtils.serializeASN1Primitive(p, true);

    }

    public static DG2File generateDG2fromImages(File directory) throws IOException {
        List<FaceInfo> faceInfos = new ArrayList<FaceInfo>();

        {
            faceInfos.add(
                    DG2Utils.generateFaceInfo(new File(directory, "faceImage.jpg")));
            // fingerInfos.add(
            //         DG3Utils.generateFingerInfo(FingerprintPosition.LEFT_LITTLE, new File(directory, "fp2.wsq")));
        }
        return new DG2File(faceInfos);

    }

    public static void main(String[] args) throws IOException {

       System.out.println(generateASNfromDG2(Files.readAllBytes(new File("face25-9-Our.bin").toPath()))); 

        System.out.println(generateASNfromDG2(generateDG2fromImages(new File(".")).getEncoded()));
        File outputFile;
        outputFile  = new File("test.txt");
        outputFile.createNewFile();
        FileWriter fileWriter;
        fileWriter = new FileWriter(outputFile, false);
        // String everything = generateASNfromDG2(generateDG2fromImages(new File(".")).getEncoded());
         String everything = generateASNfromDG2(Files.readAllBytes(new File("face25-9-Our.bin").toPath()));
        fileWriter.write(everything);
        fileWriter.close();
       

        // DG2File abd = generateDG2fromImages(new File("."));
        // byte[]abd1 = abd.getEncoded();
        // Path path = Path.of("./face25-9-Our.bin");
        // Files.write(path, abd1);

    }
}

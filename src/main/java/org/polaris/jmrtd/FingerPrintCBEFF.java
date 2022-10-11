package org.polaris.jmrtd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.jmrtd.lds.icao.DG3File;
import org.jmrtd.lds.iso19794.FingerInfo;
import org.polaris.jmrtd.DG3Utils.FingerprintPosition;
import org.polaris.jmrtd.DG3Utils.FingerprintPositionInIso;

public class FingerPrintCBEFF {

    public static String generateASNfromDG3(byte[] dg3File) throws IOException {
        ASN1InputStream input = new ASN1InputStream(dg3File);

        ASN1Primitive p = input.readObject();

        input.close();

        return StringUtils.serializeASN1Primitive(p, true);

    }

    public static DG3File generateDG3fromImages(File directory) throws IOException {
        List<FingerInfo> fingerInfos = new ArrayList<FingerInfo>();

        {
            fingerInfos.add(
                    DG3Utils.generateFingerInfo(FingerprintPosition.LEFT_RING,FingerprintPositionInIso.LEFT_RING, new File(directory, "fp1.wsq")));
            fingerInfos.add(
                    DG3Utils.generateFingerInfo(FingerprintPosition.LEFT_LITTLE,FingerprintPositionInIso.LEFT_LITTLE, new File(directory, "fp2.wsq")));
        }
        return new DG3File(fingerInfos, true);

    }

    public static void main(String[] args) throws IOException {

    //    System.out.println(generateASNfromDG3(Files.readAllBytes(new File("fase.bin").toPath())));

        System.out.println(generateASNfromDG3(generateDG3fromImages(new File(".")).getEncoded()));


        File outputFile;
        outputFile  = new File("Dg3.txt");
        outputFile.createNewFile();
        FileWriter fileWriter;
        fileWriter = new FileWriter(outputFile, false);
        // String everything = generateASNfromDG2(generateDG2fromImages(new File(".")).getEncoded());
         String everything = generateASNfromDG3(Files.readAllBytes(new File("Dg3New.bin").toPath()));
        fileWriter.write(everything);
        fileWriter.close();
       
        // DG3File abd = generateDG3fromImages(new File("."));
        // byte[]abd1 = abd.getEncoded();
        // Path path = Path.of("./abd.bin");
        // Files.write(path, abd1);

    }
}

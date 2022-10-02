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
import org.jmrtd.lds.icao.DG4File;
import org.jmrtd.lds.iso19794.IrisInfo;
import org.polaris.jmrtd.DG4Utils.IrisPosition;

public class App3 {

    public static String generateASNfromDG4(byte[] dg3File) throws IOException {
        ASN1InputStream input = new ASN1InputStream(dg3File);

        ASN1Primitive p = input.readObject();

        input.close();

        return StringUtils.serializeASN1Primitive(p, true);

    }

    public static DG4File generateDG3fromImages(File directory) throws IOException {
        List<IrisInfo> irisInfos = new ArrayList<IrisInfo>();

        {
            irisInfos.add(
                    DG4Utils.generateIrisInfo(IrisPosition.RIGHT, new File(directory, "irisR.jp2")));
            // irisInfos.add(
            //         DG4Utils.generateIrisInfo(IrisPosition.LEFT, new File(directory, "irisL.jp2")));
        }
        return new DG4File(irisInfos, true);

    }

    public static void main(String[] args) throws IOException {

       System.out.println(generateASNfromDG4(Files.readAllBytes(new File("Iris.bin").toPath())));

        // System.out.println(generateASNfromDG4(generateDG3fromImages(new File(".")).getEncoded()));
        DG4File abd = generateDG3fromImages(new File("."));
        byte[]abd1 = abd.getEncoded();
        Path path = Path.of("./Iris.bin");
        Files.write(path, abd1);


        File outputFile;
        outputFile  = new File("DG3.txt");
        outputFile.createNewFile();
        FileWriter fileWriter;
        fileWriter = new FileWriter(outputFile, false);
        // String everything = generateASNfromDG2(generateDG2fromImages(new File(".")).getEncoded());
         String everything = generateASNfromDG4(Files.readAllBytes(new File("Iris.bin").toPath()));
        fileWriter.write(everything);
        fileWriter.close();
    }
}

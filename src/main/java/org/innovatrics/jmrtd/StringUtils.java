package org.innovatrics.jmrtd;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERTags;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class StringUtils {

    private static final String TAB = "  ";
    private static final int SAMPLE_SIZE = 16;

    private static void serialize(
            String indent,
            boolean verbose,
            ASN1Primitive primitive,
            StringBuffer buf) {
        String nl = Strings.lineSeparator();

        if (primitive instanceof ASN1TaggedObject) {
            buf.append(indent);

            ASN1TaggedObject taggedObject = (ASN1TaggedObject) primitive;

            // TAG TEXT
            buf.append(getTagText(taggedObject));

            ASN1Object baseObject = taggedObject.getBaseObject();

            if (baseObject instanceof ASN1Sequence) {

                ASN1Sequence sequence = (ASN1Sequence) baseObject;

                if (!taggedObject.isExplicit()) {
                    buf.append(" IMPLICIT ");
                }
                buf.append("SEQUENCE");

                buf.append(getSize(sequence));

                buf.append(nl);

                for (int i = 0, count = sequence.size(); i < count; ++i) {
                    serialize(indent + TAB, verbose, sequence.getObjectAt(i).toASN1Primitive(),
                            buf);
                }
            } else if (baseObject instanceof ASN1OctetString) {
                ASN1OctetString oct = (ASN1OctetString) baseObject;
                buf.append(getSize(oct));
                buf.append(serializeBinaryData(indent + TAB, oct.getOctets()));
            } else if (taggedObject.getTagClass() == BERTags.APPLICATION) {
                buf.append(" IMPLICIT SEQUENCE");
                buf.append(getSize(baseObject.toASN1Primitive()));
                buf.append(nl);

                serialize(indent + TAB, verbose, baseObject.toASN1Primitive(),
                        buf);

            } else {
                buf.append(getSize(baseObject.toASN1Primitive()));
                buf.append(nl);
                serialize(indent + TAB, verbose, baseObject.toASN1Primitive(), buf);
            }
        } else if (primitive instanceof ASN1Integer) {
            byte[] value = ((ASN1Integer) primitive).getValue().toByteArray();
            buf.append(indent + "INTEGER" + getSizeString(value.length));
            buf.append(serializeBinaryData(indent + TAB, value));
        }

    }

    private static Object getSize(ASN1OctetString obj) {
        return getSizeString(obj.getOctets().length);
    }

    private static Object getSize(ASN1Object obj) {
        try {
            return getSizeString(obj.getEncoded().length);
        } catch (IOException e) {
            e.printStackTrace();
            return " ERROR ";
        }
    }

    private static Object getSize(ASN1Sequence seq) {
        try {
            int result = 0;
            for (int i = 0; i < seq.size(); i++) {
                result += seq.getObjectAt(i).toASN1Primitive().getEncoded().length;
            }
            return getSizeString(result);
        } catch (IOException e) {
            e.printStackTrace();
            return " ERROR ";
        }
    }

    private static Object getSizeString(int size) {
        return " SIZE( " + size + " )";

    }

    public static String serializeASN1Primitive(
            ASN1Primitive obj,
            boolean verbose) {

        StringBuffer buf = new StringBuffer();
        serialize("", verbose, obj, buf);
        return buf.toString();
    }

    private static String serializeBinaryData(String indent, byte[] bytes) {
        String nl = Strings.lineSeparator();
        StringBuffer buf = new StringBuffer();

        indent += TAB;

        buf.append(nl);
        for (int i = 0; i < bytes.length; i += SAMPLE_SIZE) {
            buf.append(indent);
            buf.append(String.format("%04x", i).toUpperCase());
            buf.append("  ");
            if (bytes.length - i > SAMPLE_SIZE) {

                buf.append(fromByteArray(Hex.encode(bytes, i, SAMPLE_SIZE), true));
                buf.append(TAB);
                buf.append(calculateAscString(bytes, i, SAMPLE_SIZE));
            } else {
                buf.append(fromByteArray(Hex.encode(bytes, i, bytes.length - i), true));
                for (int j = bytes.length - i; j != SAMPLE_SIZE; j++) {
                    buf.append("   ");
                }
                buf.append(TAB);
                buf.append(calculateAscString(bytes, i, bytes.length - i));
            }
            buf.append(nl);
        }

        return buf.toString();
    }

    public static String fromByteArray(byte[] bytes, boolean spaces) {
        String byteArrayString = Strings.fromByteArray(bytes).toUpperCase();
        return spaces ? String.join(" ", byteArrayString.split("(?<=\\G.{2})")) : byteArrayString;
    }

    private static String calculateAscString(byte[] bytes, int off, int len) {
        StringBuffer buf = new StringBuffer();

        for (int i = off; i != off + len; i++) {
            if (bytes[i] >= ' ' && bytes[i] <= '~') {
                buf.append((char) bytes[i]);
            } else {
                buf.append('.');
            }
        }

        return buf.toString();
    }

    public static String getTagText(ASN1TaggedObject taggedObject) {
        String result = "";
        try {
            result += (fromByteArray(Hex.encode(taggedObject.getEncoded(), 0, taggedObject.getTagNo() < 32 ? 1 : 2),
                    false))
                    .toUpperCase();
            result += " ";
            result += getTagText(taggedObject.getTagClass(), taggedObject.getTagNo());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getTagText(int tagClass, int tagNo) {
        switch (tagClass) {
            case BERTags.APPLICATION:
                return "[ APPLICATION " + tagNo + " ]";
            case BERTags.CONTEXT_SPECIFIC:
                return "[ CONTEXT " + tagNo + " ]";
            case BERTags.PRIVATE:
                return "[ PRIVATE " + tagNo + " ]";
            default:
                return "[ UNIVERSAL " + tagNo + " ]";
        }
    }
}

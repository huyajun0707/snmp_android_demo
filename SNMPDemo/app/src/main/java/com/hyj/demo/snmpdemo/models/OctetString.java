package com.hyj.demo.snmpdemo.models;

/**
 * =========================================================
 *
 * @author :   HuYajun     <13426236872@163.com>
 * @version :
 * @date :   2018/9/11 17:29
 * @description :
 * =========================================================
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BER.MutableByte;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.smi.AbstractVariable;
import org.snmp4j.smi.AssignableFromByteArray;
import org.snmp4j.smi.AssignableFromString;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class OctetString extends AbstractVariable implements AssignableFromByteArray, AssignableFromString {
    private static final long serialVersionUID = 4125661211046256289L;
    private static final char DEFAULT_HEX_DELIMITER = ':';
    private byte[] value;

    public OctetString() {
        this.value = new byte[0];
    }

    public OctetString(byte[] rawValue) {
        this(rawValue, 0, rawValue.length);
    }

    public OctetString(byte[] rawValue, int offset, int length) {
        this.value = new byte[0];
        this.value = new byte[length];
        System.arraycopy(rawValue, offset, this.value, 0, length);
    }

    public OctetString(String stringValue) {
        this.value = new byte[0];
        this.value = stringValue.getBytes();
    }

    public OctetString(OctetString other) {
        this.value = new byte[0];
        this.value = new byte[0];
        this.append(other);
    }

    public void append(byte b) {
        byte[] newValue = new byte[this.value.length + 1];
        System.arraycopy(this.value, 0, newValue, 0, this.value.length);
        newValue[this.value.length] = b;
        this.value = newValue;
    }

    public void append(byte[] bytes) {
        byte[] newValue = new byte[this.value.length + bytes.length];
        System.arraycopy(this.value, 0, newValue, 0, this.value.length);
        System.arraycopy(bytes, 0, newValue, this.value.length, bytes.length);
        this.value = newValue;
    }

    public void append(OctetString octetString) {
        this.append(octetString.getValue());
    }

    public void append(String string) {
        this.append(string.getBytes());
    }

    public void clear() {
        this.value = new byte[0];
    }

    public void encodeBER(OutputStream outputStream) throws IOException {
        BER.encodeString(outputStream, (byte) 4, this.getValue());
    }

    public void decodeBER(BERInputStream inputStream) throws IOException {
        MutableByte type = new MutableByte();
        byte[] v = BER.decodeString(inputStream, type);
        if (type.getValue() != 4) {
            throw new IOException("Wrong type encountered when decoding OctetString: " + type.getValue());
        } else {
            this.setValue(v);
        }
    }

    public int getBERLength() {
        return this.value.length + BER.getBERLengthOfLength(this.value.length) + 1;
    }

    public int getSyntax() {
        return 4;
    }

    public final byte get(int index) {
        return this.value[index];
    }

    public final void set(int index, byte b) {
        this.value[index] = b;
    }

    public int hashCode() {
        int hash = 0;

        for (int i = 0; i < this.value.length; ++i) {
            hash += this.value[i] * 31 ^ this.value.length - 1 - i;
        }

        return hash;
    }

    public boolean equals(Object o) {
        if (o instanceof OctetString) {
            OctetString other = (OctetString) o;
            return Arrays.equals(this.value, other.value);
        } else {
            return false;
        }
    }

    public boolean equalsValue(byte[] v) {
        return Arrays.equals(this.value, v);
    }

    public int compareTo(Variable o) {
        if (o instanceof OctetString) {
            OctetString other = (OctetString) o;
            int maxlen = Math.min(this.value.length, other.value.length);

            for (int i = 0; i < maxlen; ++i) {
                if (this.value[i] != other.value[i]) {
                    if ((this.value[i] & 255) < (other.value[i] & 255)) {
                        return -1;
                    }

                    return 1;
                }
            }

            return this.value.length - other.value.length;
        } else {
            throw new ClassCastException(o.getClass().getName());
        }
    }

    public OctetString substring(int beginIndex, int endIndex) {
        if (beginIndex >= 0 && endIndex <= this.length()) {
            byte[] substring = new byte[endIndex - beginIndex];
            System.arraycopy(this.value, beginIndex, substring, 0, substring.length);
            return new OctetString(substring);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public boolean startsWith(OctetString prefix) {
        if (prefix != null && prefix.length() <= this.length()) {
            for (int i = 0; i < prefix.length(); ++i) {
                if (prefix.get(i) != this.value[i]) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isPrintable() {
        for (int i = 0; i < value.length; i++) {
            char c = (char) value[i];
            if ((Character.isISOControl(c) ||
                    ((value[i] & 0xFF) >= 0x80)) && (!Character.isWhitespace(c))) {
                // 判断其是否大于0x80 (即通常所说的大于128的ASCII码，汉字编码都在这个区间内）
                return false;
            }
        }
        return true;
    }

    public String toString() {
        if (isPrintable()) {
            return new String(value);
        }
        return toHexString();
        // 没通过isPrintable()判断的，如ASCII控制字符，汉字等，都以16进制显示
    }

    public String toHexString() {
        return this.toHexString(':');
    }

    public String toHexString(char separator) {
        return this.toString(separator, 16);
    }

    public static OctetString fromHexString(String hexString) {
        return fromHexString(hexString, ':');
    }

    public static OctetString fromHexString(String hexString, char delimiter) {
        return fromString(hexString, delimiter, 16);
    }

    public static OctetString fromString(String string, char delimiter, int radix) {
        String delim = "";
        delim = delim + delimiter;
        StringTokenizer st = new StringTokenizer(string, delim);
        byte[] value = new byte[st.countTokens()];

        for (int n = 0; st.hasMoreTokens(); ++n) {
            String s = st.nextToken();
            value[n] = (byte) Integer.parseInt(s, radix);
        }

        return new OctetString(value);
    }

    public static OctetString fromHexStringPairs(String hexString) {
        byte[] value = new byte[hexString.length() / 2];

        for (int i = 0; i < value.length; ++i) {
            int h = i * 2;
            value[i] = (byte) Integer.parseInt(hexString.substring(h, h + 2), 16);
        }

        return new OctetString(value);
    }

    public static OctetString fromString(String string, int radix) {
        int digits = (int) Math.round((double) ((float) Math.log(256.0D)) / Math.log((double) radix));
        byte[] value = new byte[string.length() / digits];

        for (int n = 0; n < string.length(); n += digits) {
            String s = string.substring(n, n + digits);
            value[n / digits] = (byte) Integer.parseInt(s, radix);
        }

        return new OctetString(value);
    }

    public String toString(char separator, int radix) {
        int digits = (int) Math.round((double) ((float) Math.log(256.0D)) / Math.log((double) radix));
        StringBuffer buf = new StringBuffer(this.value.length * (digits + 1));

        for (int i = 0; i < this.value.length; ++i) {
            if (i > 0) {
                buf.append(separator);
            }

            int v = this.value[i] & 255;
            String val = Integer.toString(v, radix);

            for (int j = 0; j < digits - val.length(); ++j) {
                buf.append('0');
            }

            buf.append(val);
        }

        return buf.toString();
    }

    public String toString(int radix) {
        int digits = (int) Math.round((double) ((float) Math.log(256.0D)) / Math.log((double) radix));
        StringBuffer buf = new StringBuffer(this.value.length * (digits + 1));
        byte[] arr$ = this.value;
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            byte aValue = arr$[i$];
            int v = aValue & 255;
            String val = Integer.toString(v, radix);

            for (int j = 0; j < digits - val.length(); ++j) {
                buf.append('0');
            }

            buf.append(val);
        }

        return buf.toString();
    }

    public String toASCII(char placeholder) {
        StringBuffer buf = new StringBuffer(this.value.length);
        byte[] arr$ = this.value;
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            byte aValue = arr$[i$];
            if (!Character.isISOControl((char) aValue) && (aValue & 255) < 128) {
                buf.append((char) aValue);
            } else {
                buf.append(placeholder);
            }
        }

        return buf.toString();
    }

    public void setValue(String value) {
        this.setValue(value.getBytes());
    }

    public void setValue(byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("OctetString must not be assigned a null value");
        } else {
            this.value = value;
        }
    }

    public byte[] getValue() {
        return this.value;
    }

    public final int length() {
        return this.value.length;
    }

    public Object clone() {
        return new OctetString(this.value);
    }

    public int getBERPayloadLength() {
        return this.value.length;
    }

    public int toInt() {
        throw new UnsupportedOperationException();
    }

    public long toLong() {
        throw new UnsupportedOperationException();
    }

    public OctetString mask(OctetString mask) {
        byte[] masked = new byte[this.value.length];
        System.arraycopy(this.value, 0, masked, 0, this.value.length);

        for (int i = 0; i < mask.length() && i < masked.length; ++i) {
            masked[i] &= mask.get(i);
        }

        return new OctetString(masked);
    }

    public OID toSubIndex(boolean impliedLength) {
        int offset = 0;
        int[] subIndex;
        if (!impliedLength) {
            subIndex = new int[this.length() + 1];
            subIndex[offset++] = this.length();
        } else {
            subIndex = new int[this.length()];
        }

        for (int i = 0; i < this.length(); ++i) {
            subIndex[offset + i] = this.get(i) & 255;
        }

        return new OID(subIndex);
    }

    public void fromSubIndex(OID subIndex, boolean impliedLength) {
        if (impliedLength) {
            this.setValue(subIndex.toByteArray());
        } else {
            OID suffix = new OID(subIndex.getValue(), 1, subIndex.size() - 1);
            this.setValue(suffix.toByteArray());
        }

    }

    public static Collection<OctetString> split(OctetString octetString, OctetString delimOctets) {
        List<OctetString> parts = new LinkedList();
        int maxDelim = -1;

        int startPos;
        int i;
        for (startPos = 0; startPos < delimOctets.length(); ++startPos) {
            i = delimOctets.get(startPos) & 255;
            if (i > maxDelim) {
                maxDelim = i;
            }
        }

        startPos = 0;

        for (i = 0; i < octetString.length(); ++i) {
            int c = octetString.value[i] & 255;
            boolean isDelim = false;
            if (c <= maxDelim) {
                for (int j = 0; j < delimOctets.length(); ++j) {
                    if (c == (delimOctets.get(j) & 255)) {
                        if (startPos >= 0 && i > startPos) {
                            parts.add(new OctetString(octetString.value, startPos, i - startPos));
                        }

                        startPos = -1;
                        isDelim = true;
                    }
                }
            }

            if (!isDelim && startPos < 0) {
                startPos = i;
            }
        }

        if (startPos >= 0) {
            parts.add(new OctetString(octetString.value, startPos, octetString.length() - startPos));
        }

        return parts;
    }

    public static OctetString fromByteArray(byte[] value) {
        return value == null ? null : new OctetString(value);
    }

    public byte[] toByteArray() {
        return this.getValue();
    }
}

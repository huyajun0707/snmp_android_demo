package com.hyj.demo.snmpdemo.bf;

/**
 * 操作挂载工具类
 */

public class StringUtil {
    private static final String TAG = "StringUtil";

    private static StringUtil stringUtil;

    private StringUtil() {
        // cannot be instantiated
    }

    public static synchronized StringUtil getInstance() {
        if (stringUtil == null) {
            stringUtil = new StringUtil();
        }
        return stringUtil;
    }

    public static void releaseInstance() {
        if (stringUtil != null) {
            stringUtil = null;
        }
    }

//    public String toString(Variable value) {
//        if (isPrintable(value)) {
//            return new String(value);
//        }
//        return hexStr2Str(value);
//    }
//
//    public boolean isPrintable(String value) {
//        for (int i = 0; i < value.length; i++) {
//            char c = (char) value[i];
//            int codePoint = (int) value[i];
//            if (((codePoint > 0x0000 && codePoint <= 0x001F) ||
//                    (codePoint >= 0x007F && codePoint <= 0x009F)) && (!Character.isWhitespace(c))) {
//                return false;
//            }
//        }
//        return true;
//    }

    /**
     * 字符串转换成十六进制字符串
     *
     * @param str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 十六进制转换字符串
     *
     * @param hexStr str Byte字符串(Byte之间无分隔符 如:[616C6B])
     * @return String 对应的字符串
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * snmp4j遇到中文直接转成16进制字符串
     *
     * @param octetString
     * @return
     */
    public String getChinese(String octetString) {
        try {
            String[] temps = octetString.split(":");
            byte[] bs = new byte[temps.length];
            for (int i = 0; i < temps.length; i++)
                bs[i] = (byte) Integer.parseInt(temps[i], 16);

            return new String(bs, "GBK");
        } catch (Exception e) {
            return null;
        }
    }


}

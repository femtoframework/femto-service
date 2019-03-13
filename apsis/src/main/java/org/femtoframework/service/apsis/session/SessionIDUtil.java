package org.femtoframework.service.apsis.session;

/**
 * SessionIDUtil
 *
 * @author renex
 * @version 2006-7-28 11:09:27
 */
public class SessionIDUtil
{
    private static final char[][] ENCODER = {
            {'P', 'z', '7', 'L', 'o'}, {'i', 'Q', 'a', 'K', '9'}, //'0','1'
            {'J', 'W', 's', 'u', 'b'}, {'H', 'E', 'd', '5', 'y'}, //'2','3'
            {'R', 'G', 't', '2', 'f'}, {'T', 'g', 'F', 'V', 'r'}, //'4','5'
            {'8', 'Y', 'h', 'e', 'D'}, {'w', '1', 'j', 'S', 'U'}, //'6','7'
            {'I', 'k', '4', 'A', 'q'}, {'O', '0', 'Z', 'p', 'l'}, //'8','9'
            {'c', 'X', '3', 'N', 'm'}, {'n', 'C', '6', 'x', 'M'}, //':','.'
    };

    private static final char[] DECODER = {
            '8', '+', '.', '6', '3', '5', '4', '3', '8', '2', //"ABCDEFGHIJ"
            '1', '0', '.', ':', '9', '0', '1', '4', '7', '5', //"KLMNOPQRST"
            '7', '5', '2', ':', '6', '9', '1', '2', ':', '3', //"UVWXYZabcd"
            '6', '4', '5', '6', '1', '7', '8', '9', ':', '.', //"efghijklmn"
            '0', '9', '8', '5', '2', '4', '2', '+', '7', '.', //"opqrstuvwx"
            '3', '0', '9', '7', '4', ':', '8', '3', '.', '0', //"yz01234567"
            '6', '1'                                          //"89"
    };

    public static String encrypt(String sid)
    {
        if (sid == null) {
            return null;
        }
        int length = sid.length();
        if (length == 0) {
            return "";
        }

        int c = 0;
        int m = 0;
        StringBuilder sb = new StringBuilder(sid);
        long time = System.currentTimeMillis();
        for (int i = 0; i < length; i++) {
            int r = c;
            c = (sid.charAt(i) & 0xFF);
            int n = 0;
            if (c >= '0' && c <= '9') {
                n = c - '0';
            }
            else if (c == ':') {
                n = 10;
            }
            else if (c == '.') {
                n = 11;
            }
            else {
                sb.setCharAt(i, (char)c);
                continue;
            }
            r = r + c + m;
            m = (int)(((time * r) / 7) % 5);
            sb.setCharAt(i, ENCODER[n][m]);
        }
        return sb.toString();
    }


    /**
     * 对加密后的数据进行解密
     *
     * @param encoded 加密后的数据
     */
    public static String decrypt(String encoded)
    {
        if (encoded == null) {
            return null;
        }
        int length = encoded.length();
        if (length == 0) {
            return "";
        }

        int c;
        StringBuilder sb = new StringBuilder(encoded);
        for (int i = 0; i < length; i++) {
            c = (int)encoded.charAt(i) & 0xFF;
            int n = 0;
            if (c >= 'A' && c <= 'Z') {
                n = c - 'A';
            }
            else if (c >= 'a' && c <= 'z') {
                n = 26 + c - 'a';
            }
            else if (c >= '0' && c <= '9') {
                n = 52 + c - '0';
            }
            else { //ignore
                continue;
            }
            sb.setCharAt(i, DECODER[n]);
        }
        return sb.toString();
    }
}

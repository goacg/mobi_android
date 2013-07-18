package mobi.hubtech.goacg.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncodeUtils {
    
    public static String toMD5String(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] hash = md.digest(data);
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) hex.append("0");
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package net.sf.wowc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    public static String getMD5(byte []data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return e.toString();
        }
                                                                                
        //System.err.println("generating md5 for: "+buffer);
        try {
            md.reset();
            md.update(data);
            byte[] digest = md.digest();
                                                                                
            StringBuffer hexString = new StringBuffer();
            String pair = null;
            for (int i=0; i < digest.length; i++) {
                pair = Integer.toHexString(0xFF & digest[i]);
                if (pair.length() < 2) {
                    pair = "0" + pair;
                }
                hexString.append(pair);
            }
            return hexString.toString();
        } catch (Exception e) {
            return e.toString();
        }
    }
}

package com.bbs.util;

import java.security.MessageDigest;

public class MD5Util {
    public static String encode(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean verify(String input, String md5Hash) {
        return encode(input).equals(md5Hash);
    }
}
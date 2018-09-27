package org.dnsge.powerschoolapi;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Formatter;

public class PowerschoolAuth {

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    private static String hmacMD5(String data, String key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacMD5");
            Mac hmac = Mac.getInstance("HmacMD5");
            hmac.init(signingKey);

            return toHexString(hmac.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDBPWField(String contextData, String password) {
        return hmacMD5(password.toLowerCase(), contextData);
    }

    public static String getPWField(String contextData, String password) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] hashedPw = m.digest(password.getBytes());
            String b64Pw = Base64.getEncoder().encodeToString(hashedPw).replace("=", "");

            return hmacMD5(b64Pw, contextData);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}

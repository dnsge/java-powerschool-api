package org.dnsge.powerschoolapi.client;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Formatter;

/**
 * Class that holds authentication / cryptographic methods for logging into Powerschool
 *
 * @author Daniel Sage
 * @version 1.0
 */
public class PowerschoolAuth {

    /**
     * Returns a hex String from a byte[]
     *
     * @param bytes byte[] to convert
     * @return String with hex digits
     */
    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    /**
     * Calculates a HMAC MD5 from data and a key
     *
     * @param data Data to hash
     * @param key Key to use
     * @return Hashed data
     */
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

    /**
     * Creates the {@code dbpw} field needed for submitting a Powerschool login request
     *
     * @param contextData ContextData from a hidden form value
     * @param password Password of user
     * @return Desired dbpw value
     */
    public static String getDBPWField(String contextData, String password) {
        return hmacMD5(password.toLowerCase(), contextData);
    }

    /**
     * Creates the {@code pw} field (different from the raw password) needed for submitting a Powerschool login request
     *
     * @param contextData ContextData from a hidden form value
     * @param password Password of user
     * @return Desired pw value
     */
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

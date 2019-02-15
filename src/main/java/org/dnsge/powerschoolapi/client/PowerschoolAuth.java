/*
 * MIT License
 *
 * Copyright (c) 2019 Daniel Sage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.dnsge.powerschoolapi.client;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that holds authentication / cryptographic methods for logging into Powerschool
 *
 * @author Daniel Sage
 * @version 1.0.2
 */
final class PowerschoolAuth {

    private static final Logger LOGGER = Logger.getLogger(PowerschoolAuth.class.getName());

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
     * @param key  Key to use
     * @return Hashed data
     */
    private static String hmacMD5(String data, String key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacMD5");
            Mac hmac = Mac.getInstance("HmacMD5");
            hmac.init(signingKey);

            return toHexString(hmac.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.log(Level.SEVERE, "Fatal Error while performing HMAC", e);
            return null;
        }
    }

    /**
     * Creates the {@code dbpw} field needed for submitting a Powerschool login request
     *
     * @param contextData ContextData from a hidden form value
     * @param password    Password of user
     * @return Desired dbpw value
     */
    public static String getDBPWField(String contextData, String password) {
        return hmacMD5(password.toLowerCase(), contextData);
    }

    /**
     * Creates the {@code pw} field (different from the raw password) needed for submitting a Powerschool login request
     *
     * @param contextData ContextData from a hidden form value
     * @param password    Password of user
     * @return Desired pw value
     */
    public static String getPWField(String contextData, String password) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] hashedPw = m.digest(password.getBytes());
            String b64Pw = Base64.getEncoder().encodeToString(hashedPw).replace("=", "");

            return hmacMD5(b64Pw, contextData);

        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Fatal Error while performing MD5", e);
            return null;
        }
    }

}

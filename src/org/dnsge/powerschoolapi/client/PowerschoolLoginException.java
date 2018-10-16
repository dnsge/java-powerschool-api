package org.dnsge.powerschoolapi.client;

/**
 * RuntimeException that occurs if there is an issue logging in
 *
 * @author Daniel Sage
 * @version 0.1
 */
public class PowerschoolLoginException extends RuntimeException {

    public PowerschoolLoginException(String msg) {
        super(msg);
    }

}

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

import org.dnsge.powerschoolapi.user.User;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Interface that defines a client to interface with a Powerschool Portal
 */
public interface PowerschoolClient {


    /**
     * Generates a complete URL from the base install url
     *
     * @param extension Extension of the base install url
     * @return Extended URL
     */
    String urlify(String extension);

    /**
     * Logs in a user to a Powerschool Student Portal
     * <p>
     * Returns a new {@code User} object populated with course information
     *
     * @param username Username to login with
     * @param password Password to login with
     * @return {@code User} object
     * @throws IOException               if something goes wrong
     * @throws PowerschoolLoginException if invalid username/password
     */
    User authenticate(String username, String password) throws IOException;

    /**
     * Refreshes the information of a User
     *
     * @param user User to update
     * @throws IOException               if something goes wrong
     * @throws PowerschoolLoginException if invalid username/password
     */
    void refreshUser(User user) throws IOException;

    /**
     * Preforms a GET request with the authentication cookies of a {@code User} object
     *
     * @param user   User object to utilize
     * @param getUrl Non-URLified url to get
     * @return {@code Document} object from GET request
     * @see Document
     */
    Document getAs(User user, String getUrl);

}

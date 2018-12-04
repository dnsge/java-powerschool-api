/*
 * MIT License
 *
 * Copyright (c) 2018 Daniel Sage
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

package org.dnsge.powerschoolapi.user;

import org.dnsge.powerschoolapi.client.PowerschoolClient;
import org.jsoup.nodes.Document;

import java.util.Map;

/**
 * Class that represents a User's configuration
 *
 * @author Daniel Sage
 * @version 0.1
 * @see User
 */
public class UserConfig {

    final PowerschoolClient client;
    final String username;
    final String password;
    private final Document constructionDocument;
    final Map<String, String> authData;
    User user;

    /**
     * Basic Constructor for a new UserConfig
     *
     * @param client PowerschoolClient that is used by this config
     * @param username User username
     * @param password User password
     * @param constructionDocument JSoup Document to construct course information from
     * @param authData Authentication cookies for User
     */
    public UserConfig(PowerschoolClient client, String username, String password, Document constructionDocument, Map<String, String> authData) {
        this.client = client;
        this.username = username;
        this.password = password;
        this.constructionDocument = constructionDocument;
        this.authData = authData;
    }

    /**
     * @param user {@code User} that will own this {@code UserConfig}
     * @see User
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return {@code User} that owns this {@code UserConfig}
     * @see User
     */
    public User getUser() {
        return user;
    }

    /**
     * @return {@code UserConfig} stored username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return Authentication Cookies for this {@code UserConfig}
     */
    public Map<String, String> getAuthData() {
        return authData;
    }

    /**
     * @return PowerschoolClient of this {@code UserConfig}
     * @see PowerschoolClient
     */
    public PowerschoolClient getClient() {
        return client;
    }

    /**
     * @return {@code Document} that was used to generate this {@code UserConfig}
     * @see org.jsoup.nodes.Document
     */
    public Document getConstructionDocument() {
        return constructionDocument;
    }
}

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

package org.dnsge.powerschoolapi.client;

import org.dnsge.powerschoolapi.user.User;
import org.dnsge.powerschoolapi.user.UserConfig;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

/**
 * Client to interface with a Powerschool Student Portal
 *
 * @author Daniel Sage
 * @version 0.1
 */
public class PowerschoolClient {

    private final String psInstallURL;
    private ClientStorage storage;

    /**
     * Constructor for new PowerschoolClient
     *
     * @param psInstallURL Install URL of the Powerschool server
     */
    public PowerschoolClient(String psInstallURL) {
        this.psInstallURL = fixUrl(psInstallURL);
        this.storage = new ClientStorage();
    }

    /**
     * Ensures that a supplied URL is ended with a '/' and that it is using HTTPS
     *
     * @param initialURL URL to fix
     * @return Fixed URL
     * @throws RuntimeException if HTTPS Schema is missing
     */
    private String fixUrl(String initialURL) {
        // Make sure the URL is https and ends with a '/'
        String returnString = initialURL.toLowerCase();

        if (!returnString.substring(returnString.length() - 1).equals("/")) {
            returnString += "/";
        }
        if (!returnString.substring(0, 8).equals("https://")) {
            throw new RuntimeException("HTTPS schema missing from powerschool install URL");
        }

        return returnString;
    }

    /**
     * Generates a complete URL from the base install url
     *
     * @param extension Extension of the base install url
     * @return Extended URL
     */
    public String urlify(String extension) {
        // URL based off of the base install url
        return psInstallURL + (extension.charAt(0) == '/' ? extension.substring(1) : extension);
    }

    /**
     * Logs in a user to a Powerschool Student Portal
     * <p>
     * Returns a new {@code User} object populated with course information
     *
     * @param username Username to login with
     * @param password Password to login with
     * @return {@code User} object
     * @throws IOException if something goes wrong
     * @throws PowerschoolLoginException if invalid username/password
     */
    public User authenticate(String username, String password) throws IOException {
        // Authenticate a user
        // Get login page for the contextData and pstoken
        Document loginPage = Jsoup.connect(urlify("public/home.html")).timeout(2000).get();

        // Do hashing and other auth
        String contextData = loginPage.select("[name=contextData]").first().val();
        String pstoken = loginPage.select("[name=pstoken]").first().val();
        String dbpwField = PowerschoolAuth.getDBPWField(contextData, password);
        String pwField = PowerschoolAuth.getPWField(contextData, password);

        // Send login post request
        Response loginPostResp = Jsoup.connect(urlify("guardian/home.html"))
                .timeout(2000)
                .method(Method.POST)
                .data("pstoken", pstoken)
                .data("contextData", contextData)
                .data("dbpw", dbpwField)
                .data("serviceName", "PS Parent Portal")
                .data("pcasServerURL", "/")
                .data("credentialType", "User Id and Password Credential")
                .data("account", username)
                .data("pw", pwField)
                .data("ldappassword", password)
                .execute();

        // Make sure we logged in successfully
        if (!loginPostResp.body().contains("Grades and Attendance")) {
            throw new PowerschoolLoginException("Invalid login information");
        }

        Map<String, String> mapCookies = loginPostResp.cookies();

        // Get homepage info
        Document gradesPage = Jsoup.connect(urlify("guardian/home.html"))
                .timeout(2000)
                .cookies(mapCookies)
                .get();

        UserConfig config = new UserConfig(this, username, password, gradesPage, mapCookies);
        storage.register(config);
        return new User(config);

    }

    /**
     * Preforms a GET request with the authentication cookies of a {@code User} object
     *
     * @param user User object to utilize
     * @param getUrl Non-URLified url to get
     * @return {@code Document} object from GET request
     * @see Document
     */
    public Document getAs(User user, String getUrl) {
        // Get a url as a user
        try {
            return Jsoup.connect(urlify(getUrl))
                    .cookies(user.getAuth())
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return Powerschool Install URL
     */
    public String getPsInstallURL() {
        return psInstallURL;
    }

    /**
     * @return A {@code String} formatted like {@code "PowerschoolClient ({psInstallURL})"}
     */
    @Override
    public String toString() {
        return "PowerschoolClient (" + psInstallURL + ")";
    }
}

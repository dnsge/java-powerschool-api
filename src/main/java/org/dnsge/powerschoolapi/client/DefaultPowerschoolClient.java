/*
 * MIT License
 *
 * Copyright (c) 2020 Daniel Sage
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
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client to interface with a Powerschool Student Portal
 *
 * @author Daniel Sage
 * @version 1.1.0
 */
public final class DefaultPowerschoolClient implements PowerschoolClient {

    private static final Logger LOGGER = Logger.getLogger(DefaultPowerschoolClient.class.getName());
    /** Version string to be used in the default UserAgent */
    private static final String VERSION = "1.1.0";

    private final String psInstallURL;
    private final String userAgent;
    private ClientStorage storage;

    /**
     * Constructor for new PowerschoolClient with a default UserAgent in the format of
     * "powerschoolapi/{version identifier}"
     *
     * @param psInstallURL Install URL of the Powerschool server
     */
    public DefaultPowerschoolClient(String psInstallURL) {
        this(psInstallURL, "powerschoolapi/" + VERSION);
    }

    /**
     * Constructor for new PowerschoolClient with a UserAgent
     *
     * @param psInstallURL Install URL of the Powerschool server
     * @param userAgent UserAgent to use in requests
     */
    public DefaultPowerschoolClient(String psInstallURL, String userAgent) {
        this.userAgent = userAgent;
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
     * {@inheritDoc}
     */
    public String urlify(String extension) {
        // URL based off of the base install url
        return psInstallURL + (extension.charAt(0) == '/' ? extension.substring(1) : extension);
    }

    /**
     * Perform a POST request to log in
     *
     * @param username Username
     * @param password Password
     * @return {@code Response} object
     * @throws IOException if something goes wrong
     */
    private Response performLoginPost(String username, String password) throws IOException {
        // Authenticate a user
        // Get login page for the contextData and pstoken if used
        Document loginPage = Jsoup.connect(urlify("public/home.html"))
                .userAgent(userAgent)
                .timeout(2000)
                .get();

        LOGGER.fine("Performing authentication...");
        // Do hashing and other auth
        Elements contextDataE = loginPage.select("[name=contextData]");
        Elements pstokenE = loginPage.select("[name=pstoken]");

        if (contextDataE.isEmpty() || pstokenE.isEmpty()) {
            // Updated authentication
            LOGGER.fine("Performing login HTTP POST request");

            return Jsoup.connect(urlify("guardian/home.html"))
                    .timeout(2000)
                    .method(Method.POST)
                    .data("account", username)
                    .data("dbpw", password)
                    .data("pw", password)
                    .data("ldappassword", password)
                    .data("serviceName", "PS Parent Portal")
                    .data("credentialType", "User Id and Password Credential")
                    .data("pcasServerURL", "/")
                    .userAgent(userAgent)
                    .execute();
        } else {
            // Legacy authentication
            LOGGER.fine("Using legacy authentication");
            String contextData = contextDataE.first().val();
            String pstoken = pstokenE.first().val();
            String dbpwField = PowerschoolAuth.getDBPWField(contextData, password);
            String pwField = PowerschoolAuth.getPWField(contextData, password);
            LOGGER.fine("Performing legacy login HTTP POST request");
            // Send login post request

            return Jsoup.connect(urlify("guardian/home.html"))
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
                    .userAgent(userAgent)
                    .execute();
        }
    }

    /**
     * {@inheritDoc}
     */
    public User authenticate(String username, String password) throws IOException {
        Response loginPostResp = performLoginPost(username, password);

        // Make sure we logged in successfully
        if (!loginPostResp.body().contains("Grades and Attendance")) {
            throw new PowerschoolLoginException("Invalid login information");
        }

        Map<String, String> mapCookies = loginPostResp.cookies();
        LOGGER.fine("Requesting PowerSchool homepage");
        // Get homepage info
        Document gradesPage = Jsoup.connect(urlify("guardian/home.html"))
                .timeout(2000)
                .cookies(mapCookies)
                .userAgent(userAgent)
                .get();

        UserConfig config = new UserConfig(this, username, password, gradesPage, mapCookies);
        storage.register(config);
        return new User(config);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshUser(User user) throws IOException {
        String username = user.getConfig().getUsername();
        String password = user.getConfig().getPassword();
        Map<String, String> oldCookies = user.getConfig().getAuthCookies();

        // Try to access the page using the already used cookies
        Document gradesPage = Jsoup.connect(urlify("guardian/home.html"))
                .timeout(2000)
                .cookies(oldCookies)
                .userAgent(userAgent)
                .get();

        // The cookies were invalid, login again
        if (!gradesPage.body().html().contains("Grades and Attendance")) {
            Response loginPostResp = performLoginPost(username, password);

            if (!loginPostResp.body().contains("Grades and Attendance")) {
                throw new PowerschoolLoginException("Invalid login information");
            }

            Map<String, String> mapCookies = loginPostResp.cookies();
            gradesPage = Jsoup.connect(urlify("guardian/home.html"))
                    .timeout(2000)
                    .cookies(mapCookies)
                    .userAgent(userAgent)
                    .get();

            // Update
            user.update(new UserConfig(this, username, password, gradesPage, mapCookies));
            return;
        }

        // Otherwise, we can update with the new page
        user.update(new UserConfig(this, username, password, gradesPage, oldCookies));
    }

    /**
     * {@inheritDoc}
     */
    public Document getAs(User user, String getUrl) {
        // Get a url as a user
        try {
            return Jsoup.connect(urlify(getUrl))
                    .cookies(user.getAuth())
                    .userAgent(userAgent)
                    .get();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "There was a problem performing an HTTP GET request", e);
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

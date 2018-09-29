package org.dnsge.powerschoolapi.client;

import org.dnsge.powerschoolapi.PowerschoolAuth;
import org.dnsge.powerschoolapi.user.User;
import org.dnsge.powerschoolapi.user.UserConfig;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public class PowerschoolClient {

    private String psInstallURL;
    private ClientStorage storage;

    public PowerschoolClient(String psInstallURL) {
            this.psInstallURL = fixUrl(psInstallURL);
            this.storage = new ClientStorage();
    }

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

    public String urlify(String extension) {
        // URL based off of the base install url
        return psInstallURL + (extension.charAt(0) == '/' ? extension.substring(1) : extension);
    }

    public User authenticate(String username, String password) {
        // Authenticate a user
        try {
            // Get loginpage for the contextData and pstoken
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

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

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

    public String getPsInstallURL() {
        return psInstallURL;
    }

    @Override
    public String toString() {
        return "PowerschoolClient (" + psInstallURL + ")";
    }
}

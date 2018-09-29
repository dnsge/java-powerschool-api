package org.dnsge.powerschoolapi;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PowerschoolClient {

    private String psInstallURL;
    private HashMap<String, HashMap<String, Object>> dataStorage;

    public PowerschoolClient(String psInstallURL) {
            this.psInstallURL = fixUrl(psInstallURL);
            this.dataStorage = new HashMap<>();
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

    String urlify(String extension) {
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

            // Data storage (Will replace with better, custom object)
            if (dataStorage.containsKey(username)) {
                dataStorage.get(username).put("auth", mapCookies);
                dataStorage.get(username).put("password", password);
            } else {
                HashMap<String, Object> userData = new HashMap<>();
                userData.put("auth", mapCookies);
                userData.put("password", password);
                dataStorage.put(username, userData);
            }

            // Get homepage info
            Document gradesPage = Jsoup.connect(urlify("guardian/home.html"))
                    .timeout(2000)
                    .cookies(mapCookies)
                    .get();

            return new User(this, username, gradesPage);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Document getAs(User user, String getUrl) {
        // Get a url as a user
        try {
            return Jsoup.connect(urlify(getUrl))
                    .cookies((Map<String, String>)getUserData(user.username).get("auth"))
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPsInstallURL() {
        return psInstallURL;
    }

    public HashMap<String, Object> getUserData(String username) {
        return dataStorage.getOrDefault(username, null);
    }

    public HashMap<String, Object> getUserData(User user) {
        return dataStorage.getOrDefault(user.username, null);
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, String> getUserAuth(User user) {
        return (HashMap<String, String>)dataStorage.getOrDefault(user.username, null).get("auth");
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, String> getUserAuth(String username) {
        return (HashMap<String, String>)dataStorage.getOrDefault(username, null).get("auth");
    }
    // Todo: Special config object for storing information!

    public static void main(String[] args) {
        PowerschoolClient client = new PowerschoolClient("https://ps.ucfsd.org");
        Scanner inp = new Scanner(System.in);
        System.out.print("Username: ");
        String username = inp.nextLine();
        System.out.print("Password: ");
        String password = inp.nextLine();

        User me = client.authenticate(username, password);

        Course cl = me.courseList.get(3);
        System.out.println(cl.courseName);
        System.out.println(cl.getAssignments(GradeGroup.GradingPeriod.F1));
    }

}

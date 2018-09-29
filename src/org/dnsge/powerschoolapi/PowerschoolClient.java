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
        return psInstallURL + (extension.charAt(0) == '/' ? extension.substring(1) : extension);
    }

    public User authenticate(String username, String password) {
        try {
            Document loginPage = Jsoup.connect(urlify("public/home.html")).timeout(2000).get();

            String contextData = loginPage.select("[name=contextData]").first().val();
            String pstoken = loginPage.select("[name=pstoken]").first().val();
            String dbpwField = PowerschoolAuth.getDBPWField(contextData, password);
            String pwField = PowerschoolAuth.getPWField(contextData, password);

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

            if (!loginPostResp.body().contains("Grades and Attendance")) {
                throw new PowerschoolLoginException("Invalid login information");
            }

            Map<String, String> mapCookies = loginPostResp.cookies();

            if (dataStorage.containsKey(username)) {
                dataStorage.get(username).put("auth", mapCookies);
                dataStorage.get(username).put("password", password);
            } else {
                HashMap<String, Object> userData = new HashMap<>();
                userData.put("auth", mapCookies);
                userData.put("password", password);
                dataStorage.put(username, userData);
            }

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
        System.out.println(cl.getAssignments(cl.courseGrades.get(3)));
    }

}

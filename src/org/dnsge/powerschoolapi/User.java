package org.dnsge.powerschoolapi;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class User {

    final ArrayList<Course> courseList;
    final PowerschoolClient client;
    final Document constructionDoc;
    final String username;
    final String password;
    final String personName;

    public User(PowerschoolClient client, String username, Document doc) {
        this.client = client;
        this.constructionDoc = doc;
        this.username = username;
        this.password = (String)client.getUserData(username).get("password");
        this.courseList = new ArrayList<>();

        Node usernameContainer = doc.getElementById("userName").child(0).childNode(0);
        personName = usernameContainer.toString().trim();

        Element quickLookupDoc = doc.getElementById("quickLookup");
        Element mainContentContainer = quickLookupDoc.child(0).child(0);
        for (Element child : mainContentContainer.children()) {
            if (child.hasAttr("id")) {
                Course newPeriod = Course.generateCourseFromElement(child, this);
                courseList.add(newPeriod);
            }
        }
    }

    public HashMap<String, String> getAuth() {
        return client.getUserAuth(this);
    }

    public Document getAsSelf(String url) {
        return client.getAs(this, url);
    }

}

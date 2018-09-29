package org.dnsge.powerschoolapi.user;

import org.dnsge.powerschoolapi.client.PowerschoolClient;
import org.dnsge.powerschoolapi.detail.Course;
import org.dnsge.powerschoolapi.detail.CourseGetter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.Map;

public class User {

    final ArrayList<Course> courses;
    final String personName;
    final UserConfig config;
    final String username;

    public User(UserConfig config) {
        this.config = config;
        this.username = config.username;
        config.setUser(this);
        this.courses = new ArrayList<>();

        Document doc = this.config.constructionDocument;

        Node usernameContainer = doc.getElementById("userName").child(0).childNode(0);
        personName = usernameContainer.toString().trim();

        // Scan and find the content on user homepage, create courses from it
        Element quickLookupDoc = doc.getElementById("quickLookup");
        Element mainContentContainer = quickLookupDoc.child(0).child(0);
        for (Element child : mainContentContainer.children()) {
            if (child.hasAttr("id")) {
                courses.add(Course.generateCourseFromElement(child, this));
            }
        }
    }

    public Map<String, String> getAuth() {
        // Get the auth data for this user
        return config.authData;
    }

    public Document getAsSelf(String url) {
        // GET request as this user with its auth
        return config.client.getAs(this, url);
    }

    public CourseGetter newCourseGetter() {
        return new CourseGetter(courses);
    }

    public String getUsername() {
        return username;
    }

    public UserConfig getConfig() {
        return config;
    }

    public PowerschoolClient getClient() {
        return config.client;
    }
}

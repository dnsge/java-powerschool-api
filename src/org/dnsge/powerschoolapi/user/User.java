package org.dnsge.powerschoolapi.user;

import org.dnsge.powerschoolapi.client.PowerschoolClient;
import org.dnsge.powerschoolapi.detail.Course;
import org.dnsge.powerschoolapi.detail.CourseGetter;
import org.dnsge.powerschoolapi.util.ViewSpecification;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class that represents a logged in Powerschool user
 *
 * @author Daniel Sage
 */
public class User {

    private final ArrayList<Course> courses;
    private final String personName;
    final UserConfig config;
    final String username;

    /**
     * Constructor for a User based off of a {@code UserConfig} object
     *
     * @param config UserConfig to base the User off of
     * @see UserConfig
     */
    public User(UserConfig config) {
        this.config = config;
        this.username = config.username;
        config.setUser(this);
        this.courses = new ArrayList<>();

        Document doc = this.config.getConstructionDocument();

        Node usernameContainer = doc.getElementById("userName").child(0).childNode(0);
        personName = usernameContainer.toString().trim();

        // Scan and find the content on user homepage, create courses from it
        Element quickLookupDoc = doc.getElementById("quickLookup");
        Element mainContentContainer = quickLookupDoc.child(0).child(0); // <tbody> element
        Element rowSpecification = mainContentContainer.child(1);
        ViewSpecification viewSpecification = new ViewSpecification(rowSpecification);
        for (Element child : mainContentContainer.children()) {
            if (child.hasAttr("id")) {
                courses.add(Course.generateCourseFromElement(child, this, viewSpecification));
            }
        }
    }

    /**
     * @return Authentication Cookies for this User
     */
    public Map<String, String> getAuth() {
        // Get the auth data for this user
        return config.authData;
    }

    /**
     * Asks own {@code PowerschoolClient} to preform a GET reqeust with
     * its own authentication data
     *
     * @param url Partial url to GET
     * @return Document from GET request
     * @see PowerschoolClient
     */
    public Document getAsSelf(String url) {
        // GET request as this user with its auth
        return config.client.getAs(this, url);
    }

    /**
     * @return New CourseGetter based off of this User's courses
     */
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

    public List<Course> getCourses() {
        return courses;
    }

    public String getPersonName() {
        return personName;
    }
}

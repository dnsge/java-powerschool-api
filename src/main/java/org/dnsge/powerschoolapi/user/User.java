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

package org.dnsge.powerschoolapi.user;

import org.dnsge.powerschoolapi.client.DefaultPowerschoolClient;
import org.dnsge.powerschoolapi.client.PowerschoolClient;
import org.dnsge.powerschoolapi.detail.Course;
import org.dnsge.powerschoolapi.detail.CourseGetter;
import org.dnsge.powerschoolapi.util.DocumentFetcher;
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
 * @version 1.0.3
 */
public class User {

    private ArrayList<Course> courses;
    private String personName;
    private UserConfig config;
    private String username;

    /**
     * Constructor for a User based off of a {@code UserConfig} object
     *
     * @param config UserConfig to base the User off of
     * @see UserConfig
     */
    public User(UserConfig config) {
        this.config = config;
        this.username = config.username;
        this.courses = new ArrayList<>();

        loadInfoFromDoc(this.config.getConstructionDocument());
    }

    /**
     * Update fields based off of a config
     *
     * @param config UserConfig to update from
     */
    public void update(UserConfig config) {
        this.config = config;
        this.username = config.username;
        this.courses = new ArrayList<>();

        loadInfoFromDoc(this.config.getConstructionDocument());
    }

    /**
     * Load information from a {@code Document}
     *
     * @param doc {@code Document} to load from
     */
    private void loadInfoFromDoc(Document doc) {
        Node usernameContainer = doc.getElementById("userName").child(0).childNode(0);
        this.personName = usernameContainer.toString().trim();

        // Scan and find the content on user homepage, createWithData courses from it
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
     * Returns a {@link DocumentFetcher} that will perform requests with this User's authentication headers
     *
     * @return Document from GET request
     * @see DefaultPowerschoolClient#getAs(User, String)
     */
    public DocumentFetcher documentFetcher() {
        // GET request as this user with its auth
        return url -> config.client.getAs(this, url);
    }

    /**
     * @return New CourseGetter based off of this User's courses
     */
    public CourseGetter newCourseGetter() {
        return new CourseGetter(courses);
    }

    /**
     * @return {@code User} username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return {@code User} {@code UserConfig}
     * @see UserConfig
     */
    public UserConfig getConfig() {
        return config;
    }

    /**
     * @return {@code User} {@code PowerschoolClient}
     * @see DefaultPowerschoolClient
     */
    public PowerschoolClient getClient() {
        return config.client;
    }

    /**
     * @return {@code User} courses
     */
    public List<Course> getCourses() {
        return courses;
    }

    /**
     * @return {@code User} name of the person in real life
     */
    public String getPersonName() {
        return personName;
    }
}

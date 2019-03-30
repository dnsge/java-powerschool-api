/*
 * MIT License
 *
 * Copyright (c) 2019 Daniel Sage
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

package org.dnsge.powerschoolapi.detail;

import org.dnsge.powerschoolapi.user.User;
import org.dnsge.powerschoolapi.util.ColumnMode;
import org.dnsge.powerschoolapi.util.Pair;
import org.dnsge.powerschoolapi.util.ViewSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Object that represents a Course in Powerschool
 *
 * @author Daniel Sage
 * @version 1.0.3
 */
public class Course {

    private static final Logger LOGGER = Logger.getLogger(Course.class.getName());
    private static final Pattern teacherNamePattern =
            Pattern.compile("^Details about (.*?), (.*?)$", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    private String courseName;
    private String courseFrequency;
    private String teacherFirstName;
    private String teacherLastName;
    private String teacherEmail;
    private String room;
    private final User user;
    private final List<GradeGroup> courseGrades;

    /**
     * Basic constructor for a Course
     *
     * @param courseName       Name of the Course
     * @param courseFrequency  Frequency 'code' of the Course
     * @param teacherFirstName Teacher's first name
     * @param teacherLastName  Teacher's last name
     * @param teacherEmail     Teacher's email
     * @param room             Course's room number
     * @param courseGrades     List of course's grades
     * @param user             User which the Course belongs to
     */
    public Course(String courseName, String courseFrequency, String teacherFirstName, String teacherLastName,
                  String teacherEmail, String room, List<GradeGroup> courseGrades, User user) {
        this.courseName = courseName;
        this.courseFrequency = courseFrequency;
        this.teacherFirstName = teacherFirstName;
        this.teacherLastName = teacherLastName;
        this.teacherEmail = teacherEmail;
        this.room = room;
        this.courseGrades = courseGrades;
        this.user = user;
    }

    /**
     * Constructor for a Course with incomplete information
     *
     * @param courseGrades List of grades
     * @param user         User which the Course belongs to
     */
    public Course(List<GradeGroup> courseGrades, User user) {
        this.courseGrades = courseGrades;
        this.user = user;
    }

    /**
     * Generates a new {@code Course} from a {@code <tr>} HTML element
     *
     * @param genElement        {@code <tr>} element to construct a class from
     * @param user              User that the new course belongs to
     * @param viewSpecification {@code ViewSpecification} that should be used to make the Course
     * @return The new course
     */
    public static Course generateCourseFromElement(Element genElement, User user, ViewSpecification viewSpecification) {
        String courseFrequency = genElement.child(0).html().trim();
        String courseName;
        String teacherFirstName;
        String teacherLastName;
        String teacherEmail;
        String room = "";

        // Create the basic course with a reference to the ArrayList that will later be populated
        ArrayList<GradeGroup> courseGrades = new ArrayList<>();
        Course returnCourse = new Course(courseGrades, user);

        int columnCounter = 0;

        HashMap<ColumnMode, Element> allElements = new HashMap<>();

        for (Element courseDetailElement : genElement.children()) {
            allElements.put(viewSpecification.getAt(columnCounter), courseDetailElement);
            columnCounter++;
        }
        LOGGER.finest("Populating basic Course information");
        // Populate information from the 'Course' header
        Element courseDescriptorElement = allElements.get(ColumnMode.COURSE);
        courseName = courseDescriptorElement.childNode(0).toString().replace("&nbsp;", "");
        String teacherDesc = courseDescriptorElement.childNode(2).attr("title");
        teacherEmail = courseDescriptorElement.childNode(4).attr("href").substring(7);
        try {
            room = courseDescriptorElement.childNode(5).toString().replace("&nbsp;", "").substring(5);
        } catch (IndexOutOfBoundsException ignored) {
        }

        Matcher teacherMatcher = teacherNamePattern.matcher(teacherDesc);
        teacherMatcher.matches();
        teacherLastName = teacherMatcher.group(1);
        teacherFirstName = teacherMatcher.group(2);

        LOGGER.finest("Parsing Course grades");
        ArrayList<Pair<Element, ColumnMode>> gradingElements = ColumnMode.allGradingElements(allElements);
        for (Pair<Element, ColumnMode> gradeElementPair : gradingElements) {
            Element gradeElement;
            try {
                gradeElement = gradeElementPair.getL().child(0);
            } catch (IndexOutOfBoundsException e) { // Sometimes instead of [ i ] it's just blank
                courseGrades.add(GradeGroup.emptyGrade(returnCourse, gradeElementPair.getR()));
                continue;
            }
            // If there is no grade yet
            if (gradeElement.childNode(0).toString().equals("[ i ]")) {
                courseGrades.add(GradeGroup.noGrade(returnCourse, gradeElementPair.getR()));
            } else {
                String letterGrade = gradeElement.childNode(0).toString();
                float numberGrade = Float.parseFloat(gradeElement.childNode(2).toString());
                courseGrades.add(new GradeGroup(returnCourse.getUser().documentFetcher(), letterGrade, numberGrade,
                        gradeElementPair.getR(), gradeElement.attr("href")));
            }
        }

        // Update the course with the data found above
        returnCourse.courseFrequency = courseFrequency;
        returnCourse.courseName = courseName;
        returnCourse.teacherFirstName = teacherFirstName;
        returnCourse.teacherLastName = teacherLastName;
        returnCourse.teacherEmail = teacherEmail;
        returnCourse.room = room;

        return returnCourse;
    }

    /**
     * Gets the {@code GradeGroup} object for this Course during a specific {@code GradingPeriod}
     *
     * @param gradingPeriod GradingPeriod which to get the GradeGroup from
     * @return GradeGroup found
     * @see GradingPeriod
     * @see GradeGroup
     */
    public GradeGroup getGradeGroup(GradingPeriod gradingPeriod) {
        for (GradeGroup gg : courseGrades) {
            if (gg.getGradingPeriod() == gradingPeriod)
                return gg;
        }
        return null;
    }

    /**
     * Gets all assignments for this Course
     *
     * @param gradingPeriod {@code GradingPeriod} which to get the assignments from
     * @return {@code List} of assignments found
     * @see Assignment
     */
    public List<Assignment> getAssignments(GradingPeriod gradingPeriod) {
        GradeGroup gradeGroup = getGradeGroup(gradingPeriod);

        if (gradeGroup == null || gradeGroup.isEmpty()) {
            return new ArrayList<>();
        }

        // JSON post data with start, end dates and section ids
        // todo: save the assignment data
        LOGGER.finest("Generating URL for Assignment data fetching from GradingPeriod");
        JSONObject postData = gradeGroup.getJsonPostForAssignments();

        try {
            LOGGER.fine("Performing HTTP request for Assignment JSON");
            Connection.Response assignmentResponse = Jsoup.connect(user.getClient().urlify("ws/xte/assignment/lookup"))
                    .timeout(2000)
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/json")
                    .requestBody(postData.toString())
                    .ignoreContentType(true)
                    .cookies(user.getAuth())
                    .execute();

            ArrayList<Assignment> rList = new ArrayList<>();
            // Populate the return list with new Assignments
            LOGGER.finest("Populating Assignment list from retrieved JSON data");
            (new JSONArray(assignmentResponse.body())).forEach(
                    jsonObject -> rList.add(Assignment.generateFromJsonObject((JSONObject) jsonObject))
            );

            return rList;


        } catch (HttpStatusException e) {
            LOGGER.log(Level.SEVERE, "There was a problem fetching assignments", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "There was a problem performing an HTTP request", e);
        }

        return null;
    }

    public String courseIdentifier() {
        return Integer.toHexString(Objects.hash(getCourseFrequency(), getCourseName(), getRoom(), getTeacherEmail()));
    }

    public String objectIdentifier() {
        return Integer.toHexString(hashCode());
    }

    /**
     * @return A {@code String} formatted like {@code "{Course Name} - {Teacher Last Name} ({Course Grades toString()})"}
     */
    @Override
    public String toString() {
        return String.format("%s (%s.%s)", courseName, courseIdentifier(), objectIdentifier());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(getCourseName(), course.getCourseName()) &&
                Objects.equals(getCourseFrequency(), course.getCourseFrequency()) &&
                Objects.equals(getTeacherFirstName(), course.getTeacherFirstName()) &&
                Objects.equals(getTeacherLastName(), course.getTeacherLastName()) &&
                Objects.equals(getTeacherEmail(), course.getTeacherEmail()) &&
                Objects.equals(getRoom(), course.getRoom()) &&
                getCourseGrades().equals(course.getCourseGrades());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCourseName(), getCourseFrequency(), getTeacherFirstName(), getTeacherLastName(), getTeacherEmail(), getRoom(), getCourseGrades());
    }

    /**
     * @return {@code Course} grades
     */
    public List<GradeGroup> getCourseGrades() {
        return courseGrades;
    }

    /**
     * @return {@code Course} frequency code
     */
    public String getCourseFrequency() {
        return courseFrequency;
    }

    /**
     * @return {@code Course} name
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * @return {@code Course} room number (might not exist)
     */
    public String getRoom() {
        return room;
    }

    /**
     * @return {@code Course} teacher's email address
     */
    public String getTeacherEmail() {
        return teacherEmail;
    }

    /**
     * @return {@code Course} teacher's first name
     */
    public String getTeacherFirstName() {
        return teacherFirstName;
    }

    /**
     * @return {@code Course} teacher's last name
     */
    public String getTeacherLastName() {
        return teacherLastName;
    }

    /**
     * @return {@code User} that owns this {@code Course}
     * @see User
     */
    public User getUser() {
        return user;
    }
}

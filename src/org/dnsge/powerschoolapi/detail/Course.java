package org.dnsge.powerschoolapi.detail;

import org.dnsge.powerschoolapi.user.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Course {

    private static final Pattern teacherNamePattern =
            Pattern.compile("^Details about (.*?), (.*?)$",Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    String courseName;
    String courseFrequency;
    String teacherFirstName;
    String teacherLastName;
    String teacherEmail;
    String room;
    final User user;
    final ArrayList<GradeGroup> courseGrades;

    public Course(String courseName, String courseFrequency, String teacherFirstName, String teacherLastName,
                  String teacherEmail, String room, ArrayList<GradeGroup> courseGrades, User user) {
        this.courseName = courseName;
        this.courseFrequency = courseFrequency;
        this.teacherFirstName = teacherFirstName;
        this.teacherLastName = teacherLastName;
        this.teacherEmail = teacherEmail;
        this.room = room;
        this.courseGrades = courseGrades;
        this.user = user;
    }

    public Course(ArrayList<GradeGroup> courseGrades, User user) {
        this.courseGrades = courseGrades;
        this.user = user;
    }

    public static Course generateCourseFromElement(Element genElement, User user) {
        String courseFrequency = genElement.child(0).html().trim();
        String courseName = "";
        String teacherFirstName = "";
        String teacherLastName = "";
        String teacherEmail = "";
        String room = "";

        // Create the basic course with a reference to the ArrayList that will later be populated
        ArrayList<GradeGroup> courseGrades = new ArrayList<>();
        Course returnCourse = new Course(courseGrades, user);

        int gradePeriodCounter = 0;

        for (Element courseDetailElement : genElement.children()) {
            // If element has the align attribute, it contains the course information
            if (courseDetailElement.hasAttr("align")) {
                courseName = courseDetailElement.childNode(0).toString().replace("&nbsp;", "");
                String teacherDesc = courseDetailElement.childNode(2).attr("title");
                teacherEmail = courseDetailElement.childNode(4).attr("href").substring(7);
                try {
                    room = courseDetailElement.childNode(5).toString().replace("&nbsp;", "").substring(5);
                } catch (IndexOutOfBoundsException ignored){}

                Matcher teacherMatcher = teacherNamePattern.matcher(teacherDesc);
                teacherMatcher.matches();
                teacherLastName = teacherMatcher.group(1);
                teacherFirstName = teacherMatcher.group(2);
            }

            // If the element has at least one child and that child is an <a> tag
            if (courseDetailElement.children().size() != 0) {
                if (courseDetailElement.child(0).tagName().equals("a")) {

                    Element gradeElementATag = courseDetailElement.child(0);

                    if (!gradeElementATag.childNode(0).toString().equals("[ i ]")) {
                        // Get the grade group information
                        String letterGrade = gradeElementATag.childNode(0).toString();
                        float numberGrade = Float.parseFloat(gradeElementATag.childNode(2).toString());
                        courseGrades.add(new GradeGroup(returnCourse, letterGrade, numberGrade, gradePeriodCounter, gradeElementATag.attr("href")));
                    } else {
                        // No grades exist in this specific group
                        courseGrades.add(GradeGroup.emptyGrade(returnCourse, gradePeriodCounter));
                    }
                    gradePeriodCounter++;

                }
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

    public GradeGroup getGradeGroup(GradingPeriod gradingPeriod) {
        for (GradeGroup gg : courseGrades) {
            if (gg.gradingPeriod == gradingPeriod)
                return gg;
        }
        return null;
    }

    public ArrayList<Assignment> getAssignments(GradingPeriod gradingPeriod) {
        GradeGroup gradeGroup = getGradeGroup(gradingPeriod);

        if (gradeGroup == null || gradeGroup.isEmpty()) {
            return new ArrayList<>();
        }

        // JSON post data with start, end dates and section ids
        JSONObject postData = gradeGroup.getJsonPostForAssignments();

        try {
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
            (new JSONArray(assignmentResponse.body())).forEach(
                    jsonObject -> rList.add(Assignment.generateFromJsonObject((JSONObject)jsonObject))
            );

            return rList;


        } catch (HttpStatusException e) {
            System.out.println("Error fetching assignments");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString() {
        return courseName + " - " + teacherLastName + " (" + courseGrades.toString() + ")";
    }

    public ArrayList<GradeGroup> getCourseGrades() {
        return courseGrades;
    }

    public static Pattern getTeacherNamePattern() {
        return teacherNamePattern;
    }

    public String getCourseFrequency() {
        return courseFrequency;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getRoom() {
        return room;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public String getTeacherFirstName() {
        return teacherFirstName;
    }

    public String getTeacherLastName() {
        return teacherLastName;
    }

    public User getUser() {
        return user;
    }
}

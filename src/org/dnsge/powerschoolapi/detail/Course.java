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


    /**
     * Generates a new {@code Course} from a {@code <tr>} HTML element
     *
     * @param genElement {@code <tr>} element to construct a class from
     * @param user User that the new course belongs to
     * @return The new course
     */
    public static Course generateCourseFromElement(Element genElement, User user, ViewSpecification viewSpecification) {
        String courseFrequency = genElement.child(0).html().trim();
        String courseName = "";
        String teacherFirstName = "";
        String teacherLastName = "";
        String teacherEmail = "";
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

        // Populate information from the 'Course' header
        Element courseDescriptorElement = allElements.get(ColumnMode.COURSE);
        courseName = courseDescriptorElement.childNode(0).toString().replace("&nbsp;", "");
        String teacherDesc = courseDescriptorElement.childNode(2).attr("title");
        teacherEmail = courseDescriptorElement.childNode(4).attr("href").substring(7);
        try {
            room = courseDescriptorElement.childNode(5).toString().replace("&nbsp;", "").substring(5);
        } catch (IndexOutOfBoundsException ignored){}

        Matcher teacherMatcher = teacherNamePattern.matcher(teacherDesc);
        teacherMatcher.matches();
        teacherLastName = teacherMatcher.group(1);
        teacherFirstName = teacherMatcher.group(2);

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
                courseGrades.add(GradeGroup.emptyGrade(returnCourse, gradeElementPair.getR()));
            } else {
                String letterGrade = gradeElement.childNode(0).toString();
                float numberGrade = Float.parseFloat(gradeElement.childNode(2).toString());
                courseGrades.add(new GradeGroup(returnCourse, letterGrade, numberGrade, gradeElementPair.getR(), gradeElement.attr("href")));
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

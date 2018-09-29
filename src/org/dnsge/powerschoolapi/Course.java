package org.dnsge.powerschoolapi;

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
    final User user;
    final ArrayList<GradeGroup> courseGrades;

    public Course(String courseName, String courseFrequency, String teacherFirstName, String teacherLastName,
                  ArrayList<GradeGroup> courseGrades, User user) {
        this.courseName = courseName;
        this.courseFrequency = courseFrequency;
        this.teacherFirstName = teacherFirstName;
        this.teacherLastName = teacherLastName;
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
        ArrayList<GradeGroup> courseGrades = new ArrayList<>();

        Course returnCourse = new Course(courseGrades, user);

        int gradePeriodCounter = 0;

        for (Element courseDetailElement : genElement.children()) {
            if (courseDetailElement.hasAttr("align")) {
                courseName = courseDetailElement.childNode(0).toString().replace("&nbsp;", "");
                String teacherDesc = courseDetailElement.childNode(2).attr("title");
                Matcher teacherMatcher = teacherNamePattern.matcher(teacherDesc);
                teacherMatcher.matches();
                teacherLastName = teacherMatcher.group(1);
                teacherFirstName = teacherMatcher.group(2);
            }
            if (courseDetailElement.children().size() != 0) {
                if (courseDetailElement.child(0).tagName().equals("a")) {
                    Element gradeElementATag = courseDetailElement.child(0);
                    if (!gradeElementATag.childNode(0).toString().equals("[ i ]")) {
                        String letterGrade = gradeElementATag.childNode(0).toString();
                        float numberGrade = Float.parseFloat(gradeElementATag.childNode(2).toString());
                        courseGrades.add(new GradeGroup(returnCourse, letterGrade, numberGrade, gradePeriodCounter, gradeElementATag.attr("href")));
                    } else {
                        courseGrades.add(GradeGroup.emptyGrade(returnCourse, gradePeriodCounter));
                    }
                    gradePeriodCounter++;
                }
            }
        }
        returnCourse.courseFrequency = courseFrequency;
        returnCourse.courseName = courseName;
        returnCourse.teacherFirstName = teacherFirstName;
        returnCourse.teacherLastName = teacherLastName;

        return returnCourse;
    }

    public GradeGroup getGradeGroup(GradeGroup.GradingPeriod gradingPeriod) {
        for (GradeGroup gg : courseGrades) {
            if (gg.gradingPeriod == gradingPeriod)
                return gg;
        }
        return null;
    }

    public ArrayList<Assignment> getAssignments(GradeGroup.GradingPeriod gradingPeriod) {
        GradeGroup gradeGroup = getGradeGroup(gradingPeriod);
        if (gradeGroup == null || gradeGroup.isEmpty()) {
            return new ArrayList<>();
        }

        JSONObject postData = gradeGroup.getJsonPostForAssignments();

        try {
            Connection.Response assignmentResponse = Jsoup.connect(user.client.urlify("ws/xte/assignment/lookup"))
                    .timeout(2000)
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/json")
                    .requestBody(postData.toString())
                    .ignoreContentType(true)
                    .cookies(user.getAuth())
                    .execute();

            ArrayList<Assignment> rList = new ArrayList<>();

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
}

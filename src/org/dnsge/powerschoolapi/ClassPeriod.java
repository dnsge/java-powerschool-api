package org.dnsge.powerschoolapi;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassPeriod {

    private static final Pattern teacherNamePattern =
            Pattern.compile("^Details about (.*?), (.*?)$",Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    private final String className;
    private final String classFrequency;
    private final String teacherFirstName;
    private final String teacherLastName;
    private final ArrayList<ClassSectionGrade> classGrades;

    public ClassPeriod(String className, String classFrequency, String teacherFirstName, String teacherLastName,
                       ArrayList<ClassSectionGrade> classGrades) {
        this.className = className;
        this.classFrequency = classFrequency;
        this.teacherFirstName = teacherFirstName;
        this.teacherLastName = teacherLastName;
        this.classGrades = classGrades;
    }

    public static ClassPeriod generateClassFromElement(Element genElement) {
        String classFrequency = genElement.child(0).html().trim();
        String className = "";
        String teacherFirstName = "";
        String teacherLastName = "";
        ArrayList<ClassSectionGrade> classGrades = new ArrayList<>();

        int gradePeriodCounter = 0;

        for (Element classDetail : genElement.children()) {
            if (classDetail.hasAttr("align")) {
                className = classDetail.childNode(0).toString().replace("&nbsp;", "");
                String teacherDesc = classDetail.childNode(2).attr("title");
                Matcher teacherMatcher = teacherNamePattern.matcher(teacherDesc);
                teacherMatcher.matches();
                teacherLastName = teacherMatcher.group(1);
                teacherFirstName = teacherMatcher.group(2);
            }
            if (classDetail.children().size() != 0) {
                if (classDetail.child(0).tagName().equals("a")) {
                    Element gradeElement = classDetail.child(0);
                    if (!gradeElement.childNode(0).toString().equals("[ i ]")) {
                        String letterGrade = gradeElement.childNode(0).toString();
                        float numberGrade = Float.parseFloat(gradeElement.childNode(2).toString());
                        classGrades.add(new ClassSectionGrade(letterGrade, numberGrade, gradePeriodCounter));
                    } else {
                        classGrades.add(ClassSectionGrade.emptyGrade(gradePeriodCounter));
                    }
                    gradePeriodCounter++;
                }
            }
        }

        return new ClassPeriod(className, classFrequency, teacherFirstName, teacherLastName, classGrades);
    }

    public String getClassName() {
        return className;
    }

    public String getClassFrequency() {
        return classFrequency;
    }

    public String getTeacherFirstName() {
        return teacherFirstName;
    }

    public String getTeacherLastName() {
        return teacherLastName;
    }

    public ArrayList<ClassSectionGrade> getClassGrades() {
        return classGrades;
    }

    @Override
    public String toString() {
        return className + " - " + teacherLastName;
    }
}

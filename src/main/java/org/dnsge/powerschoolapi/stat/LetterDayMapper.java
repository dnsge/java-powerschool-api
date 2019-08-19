package org.dnsge.powerschoolapi.stat;

import org.dnsge.powerschoolapi.detail.Course;
import org.dnsge.powerschoolapi.detail.GradingPeriod;

import java.util.Arrays;
import java.util.List;

/**
 * Default implementation of a DetailedCourseMapper for schools that use a F to A+ scale with academic, honors,
 * and AP classes.
 *
 * @author Daniel Sage
 * @version 1.1
 */
public class LetterDayMapper implements DetailedCourseMapper {

    private final boolean weighted;

    public LetterDayMapper(boolean weighted) {
        this.weighted = weighted;
    }

    @Override
    public DetailedCourse mapFrom(Course course) {
        double creditHours;
        double gradeValue;

        double classLevelBoost = 0;

        // Get name as lowercase
        String name = course.getCourseName().toLowerCase();
        // Split name by spaces
        List<String> nameWords = Arrays.asList(name.split(" "));

        if (weighted) {
            if (name.contains("honors")) { // Honors level class
                classLevelBoost = 0.5;
            } else if (nameWords.contains("ap") || nameWords.contains("a.p.")) { // AP level class
                classLevelBoost = 1;
            }
        }

        /*
         * Letter day credit hour analysis
         * A,C,E or B,D,F letter day courses (every other day) are 0.25 hours if they are for one semester, or 0.5 for
         * the whole year
         */
        if (course.getGradeGroup(GradingPeriod.F1).isEmpty()) { // Class doesn't get a grade (i.e. homeroom or lunch)
            creditHours = 0;
        } else if (course.getCourseFrequency().contains("A,C,E") || course.getCourseFrequency().contains("B,D,F")) {
            boolean noMidterm = course.getGradeGroup(GradingPeriod.E1).isUnused();
            boolean noFinal = course.getGradeGroup(GradingPeriod.E2).isUnused();
            boolean hasQ1 = !course.getGradeGroup(GradingPeriod.Q1).isUnused();
            boolean hasQ3 = !course.getGradeGroup(GradingPeriod.Q3).isUnused();

            if (noMidterm || noFinal) {
                if (hasQ1 && hasQ3) {
                    creditHours = 0.5;
                } else {
                    creditHours = 0.25;
                }
            } else {
                creditHours = 0.5;
            }
        } else {
            creditHours = 1;
        }

        if (name.contains("study hall")) {
            creditHours = 0; // study hall doesn't get any hours
        }

        // Get grade value from letter grade on a A+ = 4.3 scale
        switch (course.getGradeGroup(GradingPeriod.F1).getLetterGrade()) {
            case "A+":
                gradeValue = 4.3;
                break;
            case "A":
                gradeValue = 4.0;
                break;
            case "A-":
                gradeValue = 3.7;
                break;
            case "B+":
                gradeValue = 3.3;
                break;
            case "B":
                gradeValue = 3.0;
                break;
            case "B-":
                gradeValue = 2.7;
                break;
            case "C+":
                gradeValue = 2.3;
                break;
            case "C":
                gradeValue = 2.0;
                break;
            case "C-":
                gradeValue = 1.7;
                break;
            case "D+":
                gradeValue = 1.3;
                break;
            case "D":
                gradeValue = 1.0;
                break;
            case "D-":
                gradeValue = 0.7;
                break;
            case "F":
                gradeValue = 0;
                break;
            default:
                gradeValue = 0;
                break;
        }

        // Add the level boost onto the grade value
        gradeValue += classLevelBoost;

        return DetailedCourse.createWithData(course, creditHours, gradeValue);
    }
}

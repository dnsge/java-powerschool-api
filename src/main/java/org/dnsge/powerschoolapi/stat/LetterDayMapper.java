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
            boolean noMidterm = course.isGradeGroupUnused(GradingPeriod.E1);
            boolean noFinal = course.isGradeGroupUnused(GradingPeriod.E2);
            boolean hasQ1 = !course.isGradeGroupUnused(GradingPeriod.Q1);
            boolean hasQ3 = !course.isGradeGroupUnused(GradingPeriod.Q3);

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

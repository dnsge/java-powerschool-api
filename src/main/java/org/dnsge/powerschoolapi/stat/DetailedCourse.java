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

package org.dnsge.powerschoolapi.stat;

import org.dnsge.powerschoolapi.detail.Course;

import java.util.Objects;

/**
 * Extension of a {@link Course} with credit hours and grade values.
 * Can be instantiated with {@link #createWithData(Course, double, double)}
 *
 * @author Daniel Sage
 * @version 1.0.3
 */
public class DetailedCourse extends Course {

    private double creditHours;
    private double gradeValue;

    /**
     * Creates a DetailedCourse from a Course
     *
     * @param from {@link Course} to use
     */
    private DetailedCourse(Course from) {
        super(from.getCourseName(), from.getCourseFrequency(), from.getTeacherFirstName(), from.getTeacherLastName(),
                from.getTeacherEmail(), from.getRoom(), from.getCourseGrades(), from.getUser());
    }

    /**
     * Creates a DetailedCourse from a regular {@link Course}, the number of credit hours, and the grade value.
     *
     * @param from        Course to detail
     * @param creditHours Number of credit hours
     * @param gradeValue  Grade value
     * @return New DetailedCourse with the desired information
     */
    public static DetailedCourse createWithData(Course from, double creditHours, double gradeValue) {
        DetailedCourse d = new DetailedCourse(from);
        d.creditHours = creditHours;
        d.gradeValue = gradeValue;

        return d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DetailedCourse that = (DetailedCourse) o;
        return Double.compare(that.getCreditHours(), getCreditHours()) == 0 &&
                Double.compare(that.getGradeValue(), getGradeValue()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCreditHours(), getGradeValue());
    }

    /**
     * Returns the number of credit hours for this DetailedCourse.
     *
     * @return the number of credit hours for this DetailedCourse
     */
    public double getCreditHours() {
        return creditHours;
    }

    /**
     * Returns the grade value of this DetailedCourse.
     *
     * @return the grade value of this DetailedCourse
     */
    public double getGradeValue() {
        return gradeValue;
    }

}

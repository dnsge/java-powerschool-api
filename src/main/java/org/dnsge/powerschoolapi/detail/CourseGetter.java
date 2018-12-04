/*
 * MIT License
 *
 * Copyright (c) 2018 Daniel Sage
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

import java.util.ArrayList;
import java.util.List;

/**
 * Class for filtering / searching for courses in an ArrayList
 * by different attributes like name, frequency, or teacher last name
 * <p>
 * Allows the use of method chaining
 *
 * @author Daniel Sage
 * @version 0.1
 */
public class CourseGetter {
    private ArrayList<Course> currentCourses;

    /**
     * Creates the CourseGetter with a preliminary list of Courses
     *
     * @param courses ArrayList to search through
     */
    @SuppressWarnings("unchecked")
    public CourseGetter(ArrayList<Course> courses) {
        this.currentCourses = (ArrayList<Course>)courses.clone();
    }

    /**
     * Limits remaining courses to have the exact name
     *
     * @param name Exact name to find
     * @return this CourseGetter with updated information
     */
    public CourseGetter limitByName(String name) {
        ArrayList<Course> newLimited = new ArrayList<>();

        for (Course c : currentCourses)
            if (c.getCourseName().equals(name))
                newLimited.add(c);

        currentCourses = newLimited;
        return this;
    }

    /**
     * Limits remaining courses to contain the name
     *
     * @param name Name to find
     * @return this CourseGetter with updated information
     */
    public CourseGetter containsByName(String name) {
        ArrayList<Course> newLimited = new ArrayList<>();

        for (Course c : currentCourses)
            if (c.getCourseName().contains(name))
                newLimited.add(c);

        currentCourses = newLimited;
        return this;
    }

    /**
     * Limits remaining courses to have the exact frequency
     *
     * @param courseFrequency Exact frequency to find
     * @return this CourseGetter with updated information
     */
    public CourseGetter limitByFrequency(String courseFrequency) {
        ArrayList<Course> newLimited = new ArrayList<>();

        for (Course c : currentCourses)
            if (c.getCourseFrequency().equals(courseFrequency))
                newLimited.add(c);

        currentCourses = newLimited;
        return this;
    }

    /**
     * Limits remaining courses to contain the frequency
     *
     * @param courseFrequency Frequency to find
     * @return this CourseGetter with updated information
     */
    public CourseGetter containsByFrequency(String courseFrequency) {
        ArrayList<Course> newLimited = new ArrayList<>();

        for (Course c : currentCourses)
            if (c.getCourseFrequency().contains(courseFrequency))
                newLimited.add(c);

        currentCourses = newLimited;
        return this;
    }

    /**
     * Limits remaining courses to have the exact teacher last name
     *
     * @param teacherLastName Exact last name to find
     * @return this CourseGetter with updated information
     */
    public CourseGetter limitByTeacherLastName(String teacherLastName) {
        ArrayList<Course> newLimited = new ArrayList<>();

        for (Course c : currentCourses)
            if (c.getTeacherLastName().equals(teacherLastName))
                newLimited.add(c);

        currentCourses = newLimited;
        return this;
    }

    /**
     * Limits remaining courses to contain the teacher last name
     *
     * @param teacherLastName Last name to find
     * @return this CourseGetter with updated information
     */
    public CourseGetter containsByTeacherLastName(String teacherLastName) {
        ArrayList<Course> newLimited = new ArrayList<>();

        for (Course c : currentCourses)
            if (c.getTeacherLastName().contains(teacherLastName))
                newLimited.add(c);

        currentCourses = newLimited;
        return this;
    }

    /**
     * @return New ArrayList Object with the results of the filtering
     */
    @SuppressWarnings("unchecked")
    public List<Course> results() {
        return (ArrayList<Course>)currentCourses.clone();
    }

    /**
     * Gets the nth result
     *
     * @param index Result number to get
     * @return Result at the requested index
     */
    public Course get(int index) {
        try {
            return results().get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * @return First Course in the results
     */
    public Course first() {
        return get(0);
    }

}

package org.dnsge.powerschoolapi.detail;

import java.util.ArrayList;

/**
 * Class for filtering / searching for courses in an ArrayList
 * by different attributes like name, frequency, or teacher last name
 * <p>
 * Allows the use of method chaining
 *
 * @author Daniel Sage
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
            if (c.courseName.equals(name))
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
            if (c.courseName.contains(name))
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
            if (c.courseFrequency.equals(courseFrequency))
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
            if (c.courseFrequency.contains(courseFrequency))
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
            if (c.teacherLastName.equals(teacherLastName))
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
            if (c.teacherLastName.contains(teacherLastName))
                newLimited.add(c);

        currentCourses = newLimited;
        return this;
    }

    /**
     * @return New ArrayList Object with the results of the filtering
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Course> results() {
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

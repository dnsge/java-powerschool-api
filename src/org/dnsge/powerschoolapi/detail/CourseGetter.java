package org.dnsge.powerschoolapi.detail;

import java.util.ArrayList;

public class CourseGetter {
    private ArrayList<Course> currentCourses;

    @SuppressWarnings("unchecked")
    public CourseGetter(ArrayList<Course> courses) {
        this.currentCourses = (ArrayList<Course>)courses.clone();
    }

    @SuppressWarnings("unchecked")
    public CourseGetter limitByName(String name) {
        ArrayList<Course> newLimited = new ArrayList<>();

        for (Course c : currentCourses)
            if (c.courseName.equals(name))
                newLimited.add(c);


        currentCourses = newLimited;
        return this;
    }

    @SuppressWarnings("unchecked")
    public CourseGetter limitByFrequency(String courseFrequency) {
        ArrayList<Course> newLimited = new ArrayList<>();

        for (Course c : currentCourses)
            if (c.courseFrequency.equals(courseFrequency))
                newLimited.add(c);


        currentCourses = newLimited;
        return this;
    }

    @SuppressWarnings("unchecked")
    public CourseGetter limitByTeacherLastName(String teacherLastName) {
        ArrayList<Course> newLimited = new ArrayList<>();

        for (Course c : currentCourses)
            if (c.teacherLastName.equals(teacherLastName))
                newLimited.add(c);


        currentCourses = newLimited;
        return this;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Course> results() {
        return (ArrayList<Course>)currentCourses.clone();
    }

    public Course get(int index) {
        try {
            return results().get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Course first() {
        return get(0);
    }

}

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

import org.dnsge.powerschoolapi.user.User;

import java.util.List;

/**
 * Object that represents information about a User's GPA
 *
 * @author Daniel Sage
 * @version 1.0
 */
public class UserGpa {

    private final User user;
    private final List<DetailedCourse> courses;
    private final double qualityPoints;
    private final double totalCreditHours;

    private final double gpa;

    /**
     * Creates a UserGpa from information, usually provided by a {@link DetailedCourseMapper} implementation
     *
     * @param user             User object
     * @param courses          List of DetailedCourses
     * @param qualityPoints    Number of total quality points
     * @param totalCreditHours Number of total credit hours
     */
    public UserGpa(User user, List<DetailedCourse> courses, double qualityPoints, double totalCreditHours) {
        this.user = user;
        this.courses = courses;
        this.qualityPoints = qualityPoints;
        this.totalCreditHours = totalCreditHours;

        this.gpa = qualityPoints / totalCreditHours;
    }

    /**
     * Returns the User.
     *
     * @return the User
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the DetailedCourses
     *
     * @return the DetailedCourses
     */
    public List<DetailedCourse> getCourses() {
        return courses;
    }

    /**
     * Returns the GPA as a double.
     *
     * @return the GPA as a double
     */
    public double getGpa() {
        return gpa;
    }

    /**
     * Returns the number of quality points.
     *
     * @return the number of quality points
     */
    public double getQualityPoints() {
        return qualityPoints;
    }

    /**
     * Returns the total credit hours.
     *
     * @return the total credit hours
     */
    public double getTotalCreditHours() {
        return totalCreditHours;
    }

}

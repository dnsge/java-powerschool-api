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
import org.dnsge.powerschoolapi.user.User;

import java.util.List;

/**
 * Class that calculates some statistics about a {@link User User's} grades
 *
 * @author Daniel Sage
 * @version 1.0
 */
public class PowerschoolStatistics {

    private final User user;
    private final List<Course> courses;

    /**
     * Creates a new statistics object from a User
     *
     * @param user User to create from
     */
    public PowerschoolStatistics(User user) {
        this.user = user;
        this.courses = user.getCourses();
    }

    /**
     * Calculates this object's User's GPA from a {@link DetailedCourseMapper} object
     *
     * @param mapper DetailedCourseMapper object to map courses from
     * @return A {@link UserGpa} object
     */
    public UserGpa calculateGPA(DetailedCourseMapper mapper) {
        List<DetailedCourse> detailedCourses = mapper.mapAll(courses);

        double totalCreditHours = 0;
        double qualityPoints = 0;

        for (DetailedCourse detailedCourse : detailedCourses) {
            totalCreditHours += detailedCourse.getCreditHours();
            qualityPoints += detailedCourse.getGradeValue() * detailedCourse.getCreditHours();
        }

        return new UserGpa(user, detailedCourses, qualityPoints, totalCreditHours);
    }

    /**
     * Returns courses of this statistics object.
     *
     * @return courses of this statistics object
     */
    public List<Course> getCourses() {
        return courses;
    }

    /**
     * Returns the user of this statistics object.
     *
     * @return the {@link User} of this statistics object
     */
    public User getUser() {
        return user;
    }

}

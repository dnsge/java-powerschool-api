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
import org.dnsge.powerschoolapi.detail.GradingPeriod;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface that maps a {@link Course} to a {@link DetailedCourse}.
 * Provides a default implementation of mapping a list of Courses using the {@link #mapFrom(Course)}
 * method.
 *
 * @author Daniel Sage
 * @version 1.1
 */
public interface DetailedCourseMapper {

    /**
     * Maps a Course to a DetailedCourse
     *
     * @param course Course object to map
     * @return new converted/mapped DetailedCourse
     */
    DetailedCourse mapFrom(Course course);

    /**
     * Maps a list of Courses to a list of DetailedCourses
     *
     * @param courses list of Courses
     * @return list of DetailedCourses
     */
    default List<DetailedCourse> mapAll(List<Course> courses) {
        return courses.stream().map(this::mapFrom).collect(Collectors.toList());
    }

}

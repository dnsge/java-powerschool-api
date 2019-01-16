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

package org.dnsge.powerschoolapi.util;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Enum class that refers to the Column that an Element is in
 *
 * @author Daniel Sage
 * @version 1.0
 */
public enum ColumnMode {
    EXP,
    LAST_WEEK,
    THIS_WEEK,
    COURSE,
    Q1,
    Q2,
    E1,
    F1,
    Q3,
    Q4,
    E2,
    ABSENCES,
    TARDIES;

    /**
     * Gets a specific ColumnMode from a String
     *
     * @param s String to get from
     * @return Appropriate ColumnMode
     */
    public static ColumnMode fromString(String s) {
        s = s.toLowerCase();

        switch (s) {
            case "exp":
                return EXP;
            case "last week":
                return LAST_WEEK;
            case "this week":
                return THIS_WEEK;
            case "course":
                return COURSE;
            case "q1":
                return Q1;
            case "q2":
                return Q2;
            case "e1":
                return E1;
            case "f1":
                return F1;
            case "q3":
                return Q3;
            case "q4":
                return Q4;
            case "e2":
                return E2;
            case "absences":
                return ABSENCES;
            case "tardies":
                return TARDIES;
        }

        return null;
    }

    /**
     * @return whether the ColumnMode is a Grading Period
     */
    private boolean isGradingPeriod() {
        return this == Q1 ||
                this == Q2 ||
                this == Q3 ||
                this == Q4 ||
                this == E1 ||
                this == E2 ||
                this == F1;
    }

    /**
     * Gets all elements and their corresponding ColumnMode if the ColumnMode is a grading period
     *
     * @param input HashMap of ColumnModes and JSoup Elements
     * @return ArrayList of Pairs of Elements and ColumnModes
     */
    public static ArrayList<Pair<Element, ColumnMode>> allGradingElements(HashMap<ColumnMode, Element> input) {
        ArrayList<Pair<Element, ColumnMode>> rList = new ArrayList<>();

        for (ColumnMode c : input.keySet()) {
            if (c.isGradingPeriod()) {
                rList.add(new Pair<>(input.get(c), c));
            }
        }

        return rList;
    }
}


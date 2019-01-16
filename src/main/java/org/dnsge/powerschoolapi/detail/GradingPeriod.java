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

package org.dnsge.powerschoolapi.detail;

import org.dnsge.powerschoolapi.util.ColumnMode;

/**
 * Enum that represents a specific GradingPeriod for a GradeGroup
 *
 * @author Daniel Sage
 * @version 1.0
 */
public enum GradingPeriod {
    Q1,
    Q2,
    E1,
    F1,
    Q3,
    Q4,
    E2,
    Unknown;

    /**
     * Finds the specific GradingPeriod from a number
     *
     * @param numb Index of enum
     * @return Desired Enum
     */
    public static GradingPeriod fromNumber(int numb) {
        // Get a GradingPeriod from the number that corresponds with it
        try {
            return GradingPeriod.values()[numb];
        } catch (ArrayIndexOutOfBoundsException e) {
            return GradingPeriod.Unknown;
        }
    }

    /**
     * Converts a {@code ColumnMode} to a {@code GradingPeriod}
     *
     * @param mode ColumnMode to convert
     * @return Desired ColumnMode as a GradingPeriod
     * @see ColumnMode
     */
    public static GradingPeriod fromColumnMode(ColumnMode mode) {
        switch (mode.toString()) {
            case "Q1":
                return Q1;
            case "Q2":
                return Q2;
            case "Q3":
                return Q3;
            case "Q4":
                return Q4;
            case "E1":
                return E1;
            case "E2":
                return E2;
            case "F1":
                return F1;
        }

        return Unknown;
    }

    /**
     * @return String representation of the {@code GradingPeriod}, returns "??" if {@code GradingPeriod.Unknown}
     */
    @Override
    public String toString() {
        if (this == Unknown)
            return "??";
        return super.toString();
    }

}

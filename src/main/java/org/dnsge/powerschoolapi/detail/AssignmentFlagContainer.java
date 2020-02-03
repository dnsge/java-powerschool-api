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

package org.dnsge.powerschoolapi.detail;

import java.util.Objects;

/**
 * Class that holds information about an {@code Assignment's} flags
 *
 * @author Daniel Sage
 * @version 1.0.3
 * @see Assignment
 */
public class AssignmentFlagContainer {

    private final Boolean isCollected;
    private final Boolean isLate;
    private final Boolean isMissing;
    private final Boolean isExempt;
    private final Boolean isAbsent;
    private final Boolean isIncomplete;


    /**
     * Basic constructor for an AssignmentFlagContainer
     *
     * @param isCollected  Collected flag
     * @param isLate       Late flag
     * @param isMissing    Missing flag
     * @param isExempt     Exempt flag
     * @param isAbsent     Absent flag
     * @param isIncomplete Incomplete flag
     */
    public AssignmentFlagContainer(Boolean isCollected, Boolean isLate, Boolean isMissing,
                                   Boolean isExempt, Boolean isAbsent, Boolean isIncomplete) {
        this.isCollected = isCollected;
        this.isLate = isLate;
        this.isMissing = isMissing;
        this.isExempt = isExempt;
        this.isAbsent = isAbsent;
        this.isIncomplete = isIncomplete;
    }

    /**
     * @return a new {@code AssignmentFlagContainer} that is empty
     */
    public static AssignmentFlagContainer empty() {
        return new AssignmentFlagContainer(null, null, null, null, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentFlagContainer that = (AssignmentFlagContainer) o;
        return Objects.equals(isCollected, that.isCollected) &&
                Objects.equals(isLate, that.isLate) &&
                Objects.equals(isMissing, that.isMissing) &&
                Objects.equals(isExempt, that.isExempt) &&
                Objects.equals(isAbsent, that.isAbsent) &&
                Objects.equals(isIncomplete, that.isIncomplete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isCollected, isLate, isMissing, isExempt, isAbsent, isIncomplete);
    }

    /**
     * @return whether the Assignment has the <b>Collected</b> flag
     */
    public Boolean isCollected() {
        return isCollected;
    }

    /**
     * @return whether the Assignment has the <b>Late</b> flag
     */
    public Boolean isLate() {
        return isLate;
    }

    /**
     * @return whether the Assignment has the <b>Missing</b> flag
     */
    public Boolean isMissing() {
        return isMissing;
    }

    /**
     * @return whether the Assignment has the <b>Exempt</b> flag
     */
    public Boolean isExempt() {
        return isExempt;
    }

    /**
     * @return whether the Assignment has the <b>Absent</b> flag
     */
    public Boolean isAbsent() {
        return isAbsent;
    }

    /**
     * @return whether the Assignment has the <b>Incomplete</b> flag
     */
    public Boolean isIncomplete() {
        return isIncomplete;
    }
}

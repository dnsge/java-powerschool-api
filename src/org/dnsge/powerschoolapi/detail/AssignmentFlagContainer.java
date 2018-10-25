package org.dnsge.powerschoolapi.detail;

/**
 * Class that holds information about an {@code Assignment's} flags
 *
 * @author Daniel Sage
 * @version 0.2
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

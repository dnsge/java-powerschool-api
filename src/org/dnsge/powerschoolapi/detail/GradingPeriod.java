package org.dnsge.powerschoolapi.detail;

public enum GradingPeriod {
    Q1,
    Q2,
    E1,
    F1,
    Q3,
    Q4,
    E2,
    Unknown;

    public static GradingPeriod fromNumber(int numb) {
        // Get a GradingPeriod from the number that corresponds with it
        try {
            return GradingPeriod.values()[numb];
        } catch (ArrayIndexOutOfBoundsException e) {
            return GradingPeriod.Unknown;
        }
    }

    @Override
    public String toString() {
        // Get the String representation, return ?? if Unknown
        if (this == Unknown)
            return "??";
        return super.toString();
    }

}

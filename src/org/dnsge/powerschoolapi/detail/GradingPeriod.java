package org.dnsge.powerschoolapi.detail;

import org.dnsge.powerschoolapi.util.ColumnMode;

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

    @Override
    public String toString() {
        // Get the String representation, return ?? if Unknown
        if (this == Unknown)
            return "??";
        return super.toString();
    }

}

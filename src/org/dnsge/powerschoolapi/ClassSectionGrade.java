package org.dnsge.powerschoolapi;

public class ClassSectionGrade {
    public final String letterGrade;
    public final float numberGrade;
    public final int gradingPeriod;
    public final String gradingPeriodName;
    private boolean isEmpty;

    public ClassSectionGrade(String letterGrade, float numberGrade, int gradingPeriod) {
        this.letterGrade = letterGrade;
        this.numberGrade = numberGrade;

        this.gradingPeriod = gradingPeriod;
        switch (gradingPeriod) {
            case 0:
                gradingPeriodName = "Q1";
                break;
            case 1:
                gradingPeriodName = "Q2";
                break;
            case 2:
                gradingPeriodName = "E1";
                break;
            case 3:
                gradingPeriodName = "F1";
                break;
            case 4:
                gradingPeriodName = "Q3";
                break;
            case 5:
                gradingPeriodName = "Q4";
                break;
            case 6:
                gradingPeriodName = "E2";
                break;
            default:
                gradingPeriodName = "??";
                break;
        }
    }

    public static ClassSectionGrade emptyGrade(int gradingPeriod) {
        ClassSectionGrade temp = new ClassSectionGrade("", 0f, gradingPeriod);
        temp.setEmpty(true);
        return temp;
    }

    @Override
    public String toString() {
        if (!isEmpty)
            return letterGrade + " (" + numberGrade + ")" + " in " + gradingPeriodName;
        else
            return "Empty grade in " + gradingPeriodName;
    }

    public boolean getIsEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}

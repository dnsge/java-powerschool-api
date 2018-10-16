package org.dnsge.powerschoolapi.detail;

import org.dnsge.powerschoolapi.util.ColumnMode;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class that represents a group of grades
 *
 * @author Daniel Sage
 * @version 0.1
 */
public class GradeGroup {

    private final Course myCourse;
    private final String letterGrade;
    private final float numberGrade;
    private final GradingPeriod gradingPeriod;
    private final String gradingPeriodName;
    private final String hrefAttrib;
    private boolean isEmpty;

    private static final Pattern urlMatcherPattern =
            Pattern.compile("guardian/scores\\.html\\?frn=(\\d+)&begdate=(\\d{2})/(\\d{2})/(\\d{4})&enddate=(\\d{2})/(\\d{2})/(\\d{4})&fg=([^&]+)&schoolid=(\\d+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    /**
     * General constructor
     *
     * @param myCourse {@code Course} that this GradeGroup belongs to
     * @param letterGrade Grade as a letter for GradeGroup
     * @param numberGrade Grade as a number for GradeGroup
     * @param gradingPeriod Period of Grading (Whole year, quarter 1, ect)
     * @param hrefAttrib Partial URL that contains a link to the page for the assignments within this specific GradeGroup
     */
    public GradeGroup(Course myCourse, String letterGrade, float numberGrade, ColumnMode gradingPeriod, String hrefAttrib) {

        this.myCourse = myCourse;
        this.letterGrade = letterGrade;
        this.numberGrade = numberGrade;
        this.hrefAttrib = "guardian/" + hrefAttrib;

        this.gradingPeriod = GradingPeriod.fromColumnMode(gradingPeriod);
        this.gradingPeriodName = this.gradingPeriod.toString();

    }

    /**
     * Retrieves a {@code JSONObject} that represents every assignment in the GradeGroup
     *
     * @return The new JSONObject
     * @see JSONObject
     */
    JSONObject getJsonPostForAssignments() {
        Matcher urlMatcher = urlMatcherPattern.matcher(hrefAttrib);
        urlMatcher.matches();

        // 1 - class id
        // 2 - Begdate month
        // 3 - Begdate day
        // 4 - Begdate year
        // 5 - Enddate month
        // 6 - Enddate day
        // 7 - Enddate year
        // 8 - Grading period
        // 9 - School id

        // Match and construct the JSON data

        String begMonth = urlMatcher.group(2);
        String begDay = urlMatcher.group(3);
        String begYear = urlMatcher.group(4);
        String endMonth = urlMatcher.group(5);
        String endDay = urlMatcher.group(6);
        String endYear = urlMatcher.group(7);

        String formattedBegDate = begYear + "-" + begMonth + "-" + begDay;
        String formattedEndDate = endYear + "-" + endMonth + "-" + endDay;

        Document dd = myCourse.getUser().getAsSelf(hrefAttrib);
        Element target = dd.getElementById("content-main").child(2).child(6).child(0);
        String sectionId = target.attr("data-sectionid");

        JSONObject returnObject = new JSONObject();
        returnObject.put("start_date", formattedBegDate);
        returnObject.put("end_date", formattedEndDate);
        returnObject.put("section_ids", new String[]{sectionId});

        return returnObject;
    }

    /**
     * Creates a new GradeGroup that is 'empty'
     *
     * @param myCourse {@code Course} that the GradeGroup belongs to
     * @param gradingPeriod Period of Grading (Whole year, quarter 1, ect)
     * @return new GradeGroup object with the desired attributes
     */
    public static GradeGroup emptyGrade(Course myCourse, ColumnMode gradingPeriod) {
        // An empty GradeGroup bound to a course and grading period
        GradeGroup temp = new GradeGroup(myCourse, "", 0f, gradingPeriod, null);
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

    /**
     * @return Whether this {@code GradeGroup} has no grades
     */
    public boolean isEmpty() {
        return isEmpty;
    }

    /**
     * @param empty Whether this {@code GradeGroup} has no grades
     */
    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    /**
     * @return {@code Course} that this {@code GradeGroup} belongs to
     * @see Course
     */
    public Course getMyCourse() {
        return myCourse;
    }

    /**
     * @return {@code GradeGroup} letter grade
     */
    public String getLetterGrade() {
        return letterGrade;
    }

    /**
     * @return {@code GradeGroup} grade as a decimal out of 100
     */
    public float getNumberGrade() {
        return numberGrade;
    }

    /**
     * @return The {@code GradingPeriod} of this {@code GradeGroup}
     * @see GradingPeriod
     */
    public GradingPeriod getGradingPeriod() {
        return gradingPeriod;
    }

    /**
     * @return The name of the grading period of this {@code GradeGroup}
     */
    public String getGradingPeriodName() {
        return gradingPeriodName;
    }

    /**
     * @return The href attribute of the {@code Element} used to make this GradeGroup
     */
    public String getHrefAttrib() {
        return hrefAttrib;
    }
}

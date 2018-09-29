package org.dnsge.powerschoolapi;

import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GradeGroup {

    final Course myCourse;
    final String letterGrade;
    final float numberGrade;
    final GradingPeriod gradingPeriod;
    final String gradingPeriodName;
    final String hrefAttrib;
    private boolean isEmpty;

    private static final Pattern urlMatcherPattern =
            Pattern.compile("guardian/scores\\.html\\?frn=(\\d+)&begdate=(\\d{2})/(\\d{2})/(\\d{4})&enddate=(\\d{2})/(\\d{2})/(\\d{4})&fg=([^&]+)&schoolid=(\\d+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    public GradeGroup(Course myCourse, String letterGrade, float numberGrade, int gradingPeriod, String hrefAttrib) {

        this.myCourse = myCourse;
        this.letterGrade = letterGrade;
        this.numberGrade = numberGrade;
        this.hrefAttrib = "guardian/" + hrefAttrib;

        this.gradingPeriod = GradingPeriod.fromNumber(gradingPeriod);
        this.gradingPeriodName = this.gradingPeriod.toString();

    }

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

        Document dd = myCourse.user.getAsSelf(hrefAttrib);
        Element target = dd.getElementById("content-main").child(2).child(6).child(0);
        String sectionId = target.attr("data-sectionid");

        JSONObject returnObject = new JSONObject();
        returnObject.put("start_date", formattedBegDate);
        returnObject.put("end_date", formattedEndDate);
        returnObject.put("section_ids", new String[]{sectionId});

        return returnObject;

    }

    public static GradeGroup emptyGrade(Course myCourse, int gradingPeriod) {
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

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}

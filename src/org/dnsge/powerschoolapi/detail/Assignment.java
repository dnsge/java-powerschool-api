package org.dnsge.powerschoolapi.detail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Assignment {

    public final String name;
    public final Integer assignmentId;
    public final Integer totalPoints;
    public final Integer scoredPoints;
    public final Float scorePercent;
    public final String scoreLetterGrade;
    public final String dueDate;
    public final String scoreEntryDate;
    public final Boolean isCollected;
    public final Boolean isLate;
    public final Boolean isMissing;
    public final Boolean isExempt;
    public final Boolean isAbsent;
    public final Boolean isIncomplete;

    public final boolean isMissingDetails;

    private Assignment(String name, Integer assignmentId, Integer totalPoints, Integer scoredPoints, Float scorePercent,
                      String scoreLetterGrade, String[] dates, Boolean[] flags, boolean isMissingDetails) {
        this.name = name;
        this.assignmentId = assignmentId;
        this.totalPoints = totalPoints;
        this.scoredPoints = scoredPoints;
        this.scorePercent = scorePercent;
        this.scoreLetterGrade = scoreLetterGrade;
        this.dueDate = dates[0];
        this.scoreEntryDate = dates[1];
        this.isCollected = flags[0];
        this.isLate = flags[1];
        this.isMissing = flags[2];
        this.isExempt = flags[3];
        this.isAbsent = flags[4];
        this.isIncomplete = flags[5];
        this.isMissingDetails = isMissingDetails;
    }

    private Assignment(String name, Integer assignmentId, Integer totalPoints, String dueDate) {
        this(name, assignmentId, totalPoints, null, null, null,
                new String[]{dueDate, null}, new Boolean[]{null, null, null, null, null, null}, true);
    }

    private static Integer getIntOrNull(JSONObject jo, String key) {
        try {
            return jo.getInt(key);
        } catch (JSONException e) {
            return null;
        }
    }

    private static Float getFloatOrNull(JSONObject jo, String key) {
        try {
            return jo.getFloat(key);
        } catch (JSONException e) {
            return null;
        }
    }

    private static String getStringOrNull(JSONObject jo, String key) {
        try {
            return jo.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }

    private static Boolean getBooleanOrNull(JSONObject jo, String key) {
        try {
            return jo.getBoolean(key);
        } catch (JSONException e) {
            return null;
        }
    }

    static Assignment generateFromJsonObject(JSONObject assignmentJSON) {
        try {
            // Read the JSONObject and create a new Assignment object from it
            Integer assignmentId = getIntOrNull(assignmentJSON, "assignmentid");

            JSONObject assignmentSections = assignmentJSON.getJSONArray("_assignmentsections").getJSONObject(0);
            JSONArray assignmentScoresArray = assignmentSections.getJSONArray("_assignmentscores");

            String name = getStringOrNull(assignmentSections,"name");
            String dueDate = getStringOrNull(assignmentSections,"duedate");
            Integer totalPoints = getIntOrNull(assignmentSections, "totalpointvalue");

            if (!assignmentScoresArray.isEmpty()) {
                JSONObject assignmentScores = assignmentScoresArray.getJSONObject(0);

                Integer scoredPoints = getIntOrNull(assignmentScores, "scorepoints");// assignmentScores.getInt("scorepoints", null);
                Float scorePercent = getFloatOrNull(assignmentScores,"scorepercent");
                String scoreLetterGrade = getStringOrNull(assignmentScores,"scorelettergrade");
                String scoreEntryDate = getStringOrNull(assignmentScores, "scoreentrydate");

                Boolean isCollected = getBooleanOrNull(assignmentScores, "iscollected");
                Boolean isLate = getBooleanOrNull(assignmentScores, "islate");
                Boolean isMissing = getBooleanOrNull(assignmentScores, "ismissing");
                Boolean isExempt = getBooleanOrNull(assignmentScores, "isexempt");
                Boolean isAbsent = getBooleanOrNull(assignmentScores, "isabsent");
                Boolean isIncomplete = getBooleanOrNull(assignmentScores, "isincomplete");

                return new Assignment(name, assignmentId, totalPoints, scoredPoints, scorePercent, scoreLetterGrade,
                        new String[]{dueDate, scoreEntryDate},
                        new Boolean[]{isCollected, isLate, isMissing, isExempt, isAbsent, isIncomplete}, false
                );
            } else {
                return new Assignment(name, assignmentId, totalPoints, dueDate);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return name + " (" + scorePercent + ")";
    }
}

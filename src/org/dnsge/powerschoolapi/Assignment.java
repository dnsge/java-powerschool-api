package org.dnsge.powerschoolapi;

import org.json.JSONException;
import org.json.JSONObject;

public class Assignment {

    final String name;
    final int assignmentId;
    final int totalPoints;
    final int scoredPoints;
    final float scorePercent;
    final String scoreLetterGrade;
    final String dueDate;
    final String scoreEntryDate;
    final boolean isCollected;
    final boolean isLate;
    final boolean isMissing;
    final boolean isExempt;
    final boolean isAbsent;
    final boolean isIncomplete;

    private Assignment(String name, int assignmentId, int totalPoints, int scoredPoints, float scorePercent,
                      String scoreLetterGrade, String[] dates, boolean[] flags) {
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
    }

    static Assignment generateFromJsonObject(JSONObject jo) {
        try {
            // Read the JSONObject and create a new Assignment object from it
            int assignmentId = jo.getInt("assignmentid");

            JSONObject assignmentSections = jo.getJSONArray("_assignmentsections").getJSONObject(0);
            JSONObject assignmentScores = assignmentSections.getJSONArray("_assignmentscores").getJSONObject(0);
            String name = assignmentSections.getString("name");
            int totalPoints = assignmentSections.getInt("totalpointvalue");
            int scoredPoints = assignmentScores.getInt("scorepoints");
            float scorePercent = assignmentScores.getFloat("scorepercent");
            String scoreLetterGrade = assignmentScores.getString("scorelettergrade");
            String dueDate = assignmentSections.getString("duedate");
            String scoreEntryDate = assignmentScores.getString("scoreentrydate");

            boolean isCollected = assignmentScores.getBoolean("iscollected");
            boolean isLate = assignmentScores.getBoolean("islate");
            boolean isMissing = assignmentScores.getBoolean("ismissing");
            boolean isExempt = assignmentScores.getBoolean("isexempt");
            boolean isAbsent = assignmentScores.getBoolean("isabsent");
            boolean isIncomplete = assignmentScores.getBoolean("isincomplete");

            return new Assignment(name, assignmentId, totalPoints, scoredPoints, scorePercent, scoreLetterGrade,
                    new String[]{dueDate, scoreEntryDate},
                    new boolean[]{isCollected, isLate, isMissing, isExempt, isAbsent, isIncomplete}
            );
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

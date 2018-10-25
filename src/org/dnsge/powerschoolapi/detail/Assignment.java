/*
 * MIT License
 *
 * Copyright (c) 2018 Daniel Sage
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Object that represents an Assignment in Powerschool
 * <p>
 * Exposes many fields with information about the assignment
 *
 * @author Daniel Sage
 * @version 0.1
 */
public class Assignment {

    private final String name;
    private final Integer assignmentId;
    private final Integer totalPoints;
    private final Integer scoredPoints;
    private final Float scorePercent;
    private final String category;
    private final String scoreLetterGrade;
    private final String dueDateString;
    private final String scoreEntryDateString;
    private final Boolean isCollected;
    private final Boolean isLate;
    private final Boolean isMissing;
    private final Boolean isExempt;
    private final Boolean isAbsent;
    private final Boolean isIncomplete;
    private final Date dueDate;
    private final Date scoreEntryDate;

    private final boolean isMissingDetails;

    /**
     * Basic constructor for an Assignment
     *
     * @param name Assignment Name
     * @param assignmentId Assignment ID
     * @param totalPoints Total points possible
     * @param scoredPoints Scored points
     * @param scorePercent Percentage grade
     * @param scoreLetterGrade Letter Grade
     * @param dates Array of Dates, dates[0] is the due date String, dates[1] is the entry date String
     * @param flags Array of booleans, in the order of: isCollected, isLate, isMissing, isExempt, isAbsent, isIncomplete
     * @param isMissingDetails Whether the assignment is not fully generated/populated
     */
    private Assignment(String name, Integer assignmentId, Integer totalPoints, Integer scoredPoints, Float scorePercent,
                      String scoreLetterGrade, String category, String[] dates, Boolean[] flags, boolean isMissingDetails) {
        Date dueDate1;
        Date scoreEntryDate1;
        this.name = name;
        this.assignmentId = assignmentId;
        this.totalPoints = totalPoints;
        this.scoredPoints = scoredPoints;
        this.scorePercent = scorePercent;
        this.scoreLetterGrade = scoreLetterGrade;
        this.category = category;
        this.dueDateString = dates[0];
        this.scoreEntryDateString = dates[1];
        this.isCollected = flags[0];
        this.isLate = flags[1];
        this.isMissing = flags[2];
        this.isExempt = flags[3];
        this.isAbsent = flags[4];
        this.isIncomplete = flags[5];
        this.isMissingDetails = isMissingDetails;

        if (dueDateString != null) {
            try {
                dueDate1 = new SimpleDateFormat("yyyy-MM-dd").parse(dueDateString);
            } catch (ParseException e) {
                dueDate1 = null;
            }
        } else {
            dueDate1 = null;
        }

        dueDate = dueDate1;
        if (scoreEntryDateString != null) {
            try {
                scoreEntryDate1 = new SimpleDateFormat("yyyy-MM-dd").parse(scoreEntryDateString);
            } catch (ParseException e) {
                scoreEntryDate1 = null;
            }
        } else {
            scoreEntryDate1 = null;
        }
        scoreEntryDate = scoreEntryDate1;
    }

    /**
     * Constructor for a semi-completed Assignment
     *
     * @param name Assignment name
     * @param assignmentId Assignment ID
     * @param totalPoints Total points possible
     * @param dueDateString Due date as String
     */
    private Assignment(String name, Integer assignmentId, Integer totalPoints, String dueDateString, String category) {
        this(name, assignmentId, totalPoints, null, null, null, category,
                new String[]{dueDateString, null}, new Boolean[]{null, null, null, null, null, null}, true);
    }

    /**
     * Gets an integer from a JSONObject if it exists, else returns null
     *
     * @param jo JSONObject to get int from
     * @param key Key to get
     * @return Integer found or null
     */
    private static Integer getIntOrNull(JSONObject jo, String key) {
        try {
            return jo.getInt(key);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Gets an float from a JSONObject if it exists, else returns null
     *
     * @param jo JSONObject to get float from
     * @param key Key to get
     * @return Float found or null
     */
    private static Float getFloatOrNull(JSONObject jo, String key) {
        try {
            return jo.getFloat(key);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Gets an String from a JSONObject if it exists, else returns null
     *
     * @param jo JSONObject to get String from
     * @param key Key to get
     * @return String found or null
     */
    private static String getStringOrNull(JSONObject jo, String key) {
        try {
            return jo.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Gets an Boolean from a JSONObject if it exists, else returns null
     *
     * @param jo JSONObject to get Boolean from
     * @param key Key to get
     * @return Boolean found or null
     */
    private static Boolean getBooleanOrNull(JSONObject jo, String key) {
        try {
            return jo.getBoolean(key);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Creates a new Assignment from a JSONObject retrieved by a {@code PowerschoolClient} and asked for by a {@code Course} object
     *
     * @param assignmentJSON JSONObject to use to construct the Assignment
     * @return New Assignment object from JSONObject
     * @see JSONObject
     * @see org.dnsge.powerschoolapi.client.PowerschoolClient
     * @see Course
     */
    static Assignment generateFromJsonObject(JSONObject assignmentJSON) {
        try {
            // Read the JSONObject and create a new Assignment object from it
            Integer assignmentId = getIntOrNull(assignmentJSON, "assignmentid");

            JSONObject assignmentSections = assignmentJSON.getJSONArray("_assignmentsections").getJSONObject(0);
            JSONArray assignmentScoresArray = assignmentSections.getJSONArray("_assignmentscores");
            JSONArray assignmentCategoryAssociations = assignmentSections.getJSONArray("_assignmentcategoryassociations");

            String name = getStringOrNull(assignmentSections,"name");
            String dueDate = getStringOrNull(assignmentSections,"duedate");
            Integer totalPoints = getIntOrNull(assignmentSections, "totalpointvalue");

            String category;
            try {
                category = assignmentCategoryAssociations.getJSONObject(0)
                        .getJSONObject("_teachercategory").getString("name");
            } catch (JSONException i) {
                category = null;
            }

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

                return new Assignment(name, assignmentId, totalPoints, scoredPoints, scorePercent, scoreLetterGrade, category,
                        new String[]{dueDate, scoreEntryDate},
                        new Boolean[]{isCollected, isLate, isMissing, isExempt, isAbsent, isIncomplete}, false
                );
            } else {
                return new Assignment(name, assignmentId, totalPoints, dueDate, category);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return A {@code String} formatted like {@code "{Assignment Name} ({Score Percent})"}
     */
    @Override
    public String toString() {
        return name + " (" + scorePercent + ")";
    }

    /**
     * @return {@code Assignment} name
     */
    public String getName() {
        return name;
    }

    /**
     * @return {@code Assignment} ID
     */
    public Integer getAssignmentId() {
        return assignmentId;
    }

    /**
     * @return Total points possible on the {@code Assignment}
     */
    public Integer getTotalPoints() {
        return totalPoints;
    }

    /**
     * @return Points actually scored on the {@code Assignment}
     */
    public Integer getScoredPoints() {
        return scoredPoints;
    }

    /**
     * @return Score on the {@code Assignment} as a percent out of 100
     */
    public Float getScorePercent() {
        return scorePercent;
    }

    /**
     * @return Score on the {@code Assignment} as a letter grade
     */
    public String getScoreLetterGrade() {
        return scoreLetterGrade;
    }

    /**
     * @return Teacher-defined category of the {@code Assignment}
     */
    public String getCategory() {
        return category;
    }

    /**
     * @return Due date of the {@code Assignment} in the format of {@code "yyyy-MM-dd"}
     */
    public String getDueDateString() {
        return dueDateString;
    }

    /**
     * @return Date that the {@code Assignment} was graded in the format of {@code "yyyy-MM-dd"}
     */
    public String getScoreEntryDateString() {
        return scoreEntryDateString;
    }

    /**
     * @return Does the {@code Assignment} have the "Collected" flag (NOT if it has been graded)
     */
    public Boolean getCollected() {
        return isCollected;
    }

    /**
     * @return Does the {@code Assignment} have the "Late" flag
     */
    public Boolean getLate() {
        return isLate;
    }

    /**
     * @return Does the {@code Assignment} have the "Missing" flag
     */
    public Boolean getMissing() {
        return isMissing;
    }

    /**
     * @return Does the {@code Assignment} have the "Exempt" flag
     */
    public Boolean getExempt() {
        return isExempt;
    }

    /**
     * @return Does the {@code Assignment} have the "Absent" flag
     */
    public Boolean getAbsent() {
        return isAbsent;
    }

    /**
     * @return Does the {@code Assignment} have the "Incomplete" flag
     */
    public Boolean getIncomplete() {
        return isIncomplete;
    }

    /**
     * @return Due date of the {@code Assignment}
     * @see Date
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * @return Date that the {@code Assignment} was graded
     * @see Date
     */
    public Date getScoreEntryDate() {
        return scoreEntryDate;
    }

    /**
     * @return Whether the {@code Assignment} is missing certain details (i.e. the {@code Assignment} hasn't been graded yet)
     */
    public boolean isMissingDetails() {
        return isMissingDetails;
    }
}

package com.matt.studytracker;

/**
 * Created by matthewdunn on 8/12/15.
 */
public class Subject {
    private String subject;
    private String creditHours;
    private String difficulty;

    private int intCredits;
    private int intDifficulty;

    private int dataBaseID;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(String creditHours) {
        this.creditHours = creditHours;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getIntCredits(){return intCredits;}

    public void setIntCredits(int intCredits){
        this.intCredits = intCredits;
    }

    public int getIntDifficulty(){return intDifficulty;}

    public void setIntDifficulty(int intDifficulty){this.intDifficulty = intDifficulty;}

    public int getDataBaseID() {
        return dataBaseID;
    }

    public void setDataBaseID(int dataBaseID) {
        this.dataBaseID = dataBaseID;
    }
}

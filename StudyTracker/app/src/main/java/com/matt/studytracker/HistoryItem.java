package com.matt.studytracker;

/**
 * Created by matthewdunn on 6/20/15.
 */
public class HistoryItem {
    private String _subject = "";
    private String _timeElapsed = "";
    private String _date = "";
    private long DBdate = 0;
    private int dataBaseRowID = 0;

    public void setSubject(String subject){
        _subject = subject;
    }

    public void setTimeElapsed(String timeElapsed){
        _timeElapsed = timeElapsed;
    }

    public void setDate(String date){
        _date = date;
    }

    public void setDBdate(long date){
        DBdate = date;
    }

    public void setDataBaseRowID(int id){
        dataBaseRowID = id;
    }

    public String getSubject(){
        return _subject;
    }

    public String getTimeElapsed(){
        return _timeElapsed;
    }

    public String getDate(){
        return _date;
    }

    public long getDBdate(){
        return DBdate;
    }

    public int getDataBaseRowID(){
        return dataBaseRowID;
    }
}

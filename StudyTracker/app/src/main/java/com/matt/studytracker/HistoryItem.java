package com.matt.studytracker;

/**
 * Created by matthewdunn on 6/20/15.
 */
public class HistoryItem {
    private String _subject = "";
    private String _timeElapsed = "";
    private String _date = "";

    public void setSubject(String subject){
        _subject = subject;
    }

    public void setTimeElapsed(String timeElapsed){
        _timeElapsed = timeElapsed;
    }

    public void setDate(String date){
        _date = date;
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

}

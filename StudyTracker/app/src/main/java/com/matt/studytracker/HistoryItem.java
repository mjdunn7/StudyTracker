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
    private String interval = "";

    private int startYear;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;
    private int endYear;
    private int endMonth;
    private int endDay;
    private int endHour;
    private int endMinute;

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public int getEndDay() {
        return endDay;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

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

    public void setInterval(String startToEnd){
        interval = startToEnd;
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

    public String getInterval(){
        return interval;
    }
}

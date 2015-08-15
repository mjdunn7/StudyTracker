package com.matt.studytracker;

import java.util.Calendar;

/**
 * Created by matthewdunn on 8/15/15.
 */
public class DBTimeHelper {
    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final String[] DAYS = {"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    public long getFormattedPastTime(String when){

        Calendar calendar = Calendar.getInstance();
        long time = 0;

        if(when.equals("Day")){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            time = Long.parseLong(getFormattedDate(calendar));
        }

        if(when.equals("Week")){
            calendar.add(Calendar.DAY_OF_YEAR, -7);
            time = Long.parseLong(getFormattedDate(calendar));
        }

        if(when.equals("Month")){
            calendar.add(Calendar.MONTH, -1);
            time = Long.parseLong(getFormattedDate(calendar));
        }

        if(when.equals("Six Months")){
            calendar.add(Calendar.MONTH, -6);
            time = Long.parseLong(getFormattedDate(calendar));
        }

        if(when.equals("All Time")){
            time = 0;
        }

        return time;
    }

    public String getFormattedDate(Calendar calendar){
        String currentDate;
        currentDate = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

        if(calendar.get(Calendar.MONTH) < 10){
            currentDate += "0" + calendar.get(Calendar.MONTH);
        }else {
            currentDate += Integer.toString(calendar.get(Calendar.MONTH));
        }

        if(calendar.get(Calendar.DAY_OF_MONTH) < 10){
            currentDate += "0" + Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        }else {
            currentDate += Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        }

        currentDate += "235900";
        return currentDate;
    }

    public String getReadableTime(int hour, int minute){
        String startTime;
        if(hour <= 12){
            startTime = Integer.toString(hour) + ":";
            if(minute < 10){
                startTime += "0" + Integer.toString(minute);
            }else {
                startTime += Integer.toString(minute);
            }
            if(hour == 12){
                startTime += " PM";
            }else {
                startTime += " AM";
            }
        }else{
            hour = hour - 12;
            startTime = Integer.toString(hour) + ":";
            if (minute < 10){
                startTime += "0" + Integer.toString(minute);
            }else {
                startTime += Integer.toString(minute);
            }
            startTime += " PM";
        }
        return startTime;
    }

    public String getCurrentDBdate(){
        String currentDate;
        currentDate = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

        if(Calendar.getInstance().get(Calendar.MONTH) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.MONTH));
        }else {
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.MONTH));
        }

        if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }else {
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }

        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        }else{
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        }

        if(Calendar.getInstance().get(Calendar.MINUTE) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.MINUTE));
        }else{
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.MINUTE));
        }

        if(Calendar.getInstance().get(Calendar.SECOND) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.SECOND));
        }else{
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.SECOND));
        }


        return currentDate;
    }

    public String getHumanDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String date;

        date = DAYS[dayOfWeek];
        date += ", " + NewSessionActivity.MONTHS[month];
        date += " " + Integer.toString(day) + ",";
        date += " " + Integer.toString(year);

        return date;
    }

    public String getGivenDBDate(int year, int month, int day, int hour, int minute){
        String givenDate;

        givenDate = Integer.toString(year);

        if(month < 10){
            givenDate += "0" + Integer.toString(month);
        }else {
            givenDate += Integer.toString(month);
        }

        if(day < 10){
            givenDate += "0" + Integer.toString(day);
        }else {
            givenDate += Integer.toString(day);
        }

        if(hour < 10){
            givenDate += "0" + Integer.toString(hour);
        }else {
            givenDate += Integer.toString(hour);
        }

        if(minute < 10){
            givenDate += "0" + Integer.toString(minute);
        }else {
            givenDate += Integer.toString(minute);
        }

        if(Calendar.getInstance().get(Calendar.SECOND) < 10){
            givenDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.SECOND));
        }else {
            givenDate += Integer.toString(Calendar.getInstance().get(Calendar.SECOND));
        }

        return givenDate;
    }
}

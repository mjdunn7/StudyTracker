package com.matt.studytracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;

import java.util.Calendar;

public class TimerService extends Service {
    private static long UPDATE_EVERY = 200;

    protected Handler handler;
    protected Handler reverseHandler;
    protected UpdateTimer updateTimer;
    protected UpdateReverseTimer updateReverseTimer;

    protected boolean timerRunning = false;
    protected boolean reverseTimerRunning = false;

    protected long startedAt;
    protected long willStopAt;
    protected long stoppedAt;

    protected Notification note;
    protected String timedSubject;
    protected String humanStartTime;

    private final IBinder mBinder = new LocalBinder();
    private static final int NOTIFICATION_ID = 1;
    private Notification.Builder builder;
    private int[] startTimesHolder = new int[5];

    protected boolean vibrateOn;

    protected String readableTimeElapsed ="";
    protected String startHumanTime = "";

    public static final String IS_COUNTDOWN_END = "activity started at countdown end";

    private DBAdapter mAdapter;

    public TimerService() {
    }

    protected class LocalBinder extends Binder{
        TimerService getService(){
            return TimerService.this;
        }
    }

    private Notification getTimerNotification(String subject, String timer){
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, new Intent(this, MainActivity.class), 0);

        builder = new Notification.Builder(this)
                .setContentTitle(subject)
                .setContentText(timer)
                .setSmallIcon(R.drawable.studying_icon)
                        //.setUsesChronometer(true)
                .setContentIntent(contentIntent);

        return builder.getNotification();
    }

    private void updateNotification(String subject, String timer) {

        //Notification notification = getTimerNotification(subject, timer);
        if(timerRunning || reverseTimerRunning) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            builder.setContentText(timer);
            mNotificationManager.notify(NOTIFICATION_ID, builder.getNotification());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void stop(){
        if(timerRunning){
            handler.removeCallbacks(updateTimer);
            updateTimer = null;
            handler = null;

        }

        if(reverseTimerRunning){
            reverseHandler.removeCallbacks(updateReverseTimer);
            updateReverseTimer = null;
            reverseHandler = null;
        }

        timerRunning = false;
        reverseTimerRunning = false;

        stopForeground(true);
        //stopSelf();
    }

    public void startTimer(String subject){

        timedSubject = subject;
        startForeground(NOTIFICATION_ID, getTimerNotification(subject, ""));

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        humanStartTime = getReadableTime(hour, minute);

        timerInitiated();


    }

    public void startReverseTimer(String subject, int hours, int minutes, boolean vibrateOn){
        this.vibrateOn = vibrateOn;

        timedSubject = subject;
        startForeground(NOTIFICATION_ID, getTimerNotification(subject, ""));

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        humanStartTime = getReadableTime(hour, minute);

        backwardsTimerInitiated(hours, minutes);
    }

    public int[] getStartTimesHolder(){
        return startTimesHolder;
    }

    class UpdateTimer implements Runnable {
        public void run() {

            setTimeDisplay();

            if (handler != null) {
                handler.postDelayed(this, UPDATE_EVERY);
            }
        }

    }

    protected void setTimeDisplay(){
        if(timerRunning) {
            String display;
            long timeNow;
            long diff;
            long seconds;
            long minutes;
            long hours;

            if (timerRunning)
                timeNow = System.currentTimeMillis();
            else
                timeNow = stoppedAt;

            diff = timeNow - startedAt;

            if (diff < 0)
                diff = 0;

            seconds = diff / 1000;
            minutes = seconds / 60;
            hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;

            display = String.format("%d", hours) + ":"
                    + String.format("%02d", minutes) + ":"
                    + String.format("%02d", seconds);

            // Log.d("setTimeDisplay", display);

            updateNotification("Timer", display);
        }
    }

    private String getReadableTime(int hour, int minute){
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

    protected void timerInitiated(){
        startedAt = System.currentTimeMillis();
        timerRunning = true;

        Calendar calendar = Calendar.getInstance();
        startTimesHolder[0] = calendar.get(Calendar.YEAR);
        startTimesHolder[1] = calendar.get(Calendar.MONTH);
        startTimesHolder[2] = calendar.get(Calendar.DAY_OF_MONTH);
        startTimesHolder[3] = calendar.get(Calendar.HOUR_OF_DAY);
        startTimesHolder[4] = calendar.get(Calendar.MINUTE);

        handler = new Handler();
        updateTimer = new UpdateTimer();
        handler.postDelayed(updateTimer, UPDATE_EVERY);
    }

    private long getFutureTime(int hours, int minutes){
        long timeNow = System.currentTimeMillis();

        long millisTime = (long) (hours * 3600000) + (minutes * 60000);

        long futureTime = timeNow + millisTime;

        return futureTime;
    }

    public void backwardsTimerInitiated(int hours, int minutes){
        startedAt = System.currentTimeMillis();
        willStopAt = getFutureTime(hours, minutes);

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        DBTimeHelper helper = new DBTimeHelper();
        startHumanTime = helper.getReadableTime(hour, minute);

        Calendar calendar = Calendar.getInstance();
        startTimesHolder[0] = calendar.get(Calendar.YEAR);
        startTimesHolder[1] = calendar.get(Calendar.MONTH);
        startTimesHolder[2] = calendar.get(Calendar.DAY_OF_MONTH);
        startTimesHolder[3] = calendar.get(Calendar.HOUR_OF_DAY);
        startTimesHolder[4] = calendar.get(Calendar.MINUTE);

        reverseTimerRunning = true;

        reverseHandler = new Handler();
        updateReverseTimer = new UpdateReverseTimer();
        reverseHandler.postDelayed(updateReverseTimer, UPDATE_EVERY);
    }

    protected void setCountdownTimeDisplay(){
        String display;
        long timeNow;
        long diff;
        long seconds;
        long minutes;
        long hours;

        if(reverseTimerRunning)
            timeNow = System.currentTimeMillis();
        else
            timeNow = stoppedAt;

        diff = willStopAt - timeNow;

        if(diff < 0 )
            diff = 0;

        seconds = diff/1000;
        minutes = seconds/60;
        hours = minutes/60;
        seconds = seconds % 60;
        minutes = minutes % 60;

        display = String.format("%d",hours) + ":"
                + String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds);

        updateNotification("Timer", display);

        long timeElapsed = timeNow - startedAt;

        seconds = timeElapsed/1000;
        minutes = seconds/60;
        hours = minutes/60;
        seconds = seconds % 60;
        minutes = minutes % 60;

        readableTimeElapsed = String.format("%d",hours) + ":"
                + String.format("%02d", minutes + 1) + ":"
                + String.format("%02d", seconds);

        if(diff == 0 || display.equals("0:00:00")){
            // Get instance of Vibrator from current Context
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            // Vibrate for 400 milliseconds
            if(vibrateOn){
                long[] pattern = {0, 700, 200, 700, 200, 700, 200, 700, 200, 700, 200, 700};
                v.vibrate(pattern, -1);
            }

            reverseHandler.removeCallbacks(updateReverseTimer);
            updateReverseTimer = null;
            reverseHandler = null;

            reverseTimerRunning = false;

            stopAndStore();
        }

    }

    private void stopAndStore(){
        stoppedAt = System.currentTimeMillis();

        String tempStoppedTime = readableTimeElapsed;
        String stoppedTime = "";

        boolean firstColonHit = false;

        for (int i = 0; i < tempStoppedTime.length(); ++i) {

            if (tempStoppedTime.charAt(i) == ':' && firstColonHit == true) {
                i = tempStoppedTime.length();
            } else if (tempStoppedTime.charAt(i) == ':' && firstColonHit == false) {
                firstColonHit = true;
                stoppedTime += Character.toString(tempStoppedTime.charAt(i));
            } else {
                stoppedTime += Character.toString(tempStoppedTime.charAt(i));
            }
        }

        String stoppedSubject = timedSubject;

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        DBTimeHelper helper = new DBTimeHelper();
        String endHumanTime = helper.getReadableTime(hour, minute);

        String interval = startHumanTime + " - " + endHumanTime;

        Calendar calendar = Calendar.getInstance();
        int endYear = calendar.get(Calendar.YEAR);
        int endMonth = calendar.get(Calendar.MONTH);
        int endDay = calendar.get(Calendar.DAY_OF_MONTH);
        int endHour = calendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = calendar.get(Calendar.MINUTE);

        Intent intent = new Intent(TimerService.this, MainActivity.class);

        Bundle bundle = new Bundle();;
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        bundle.putBoolean(IS_COUNTDOWN_END, true);
        bundle.putString(DBTimeHelper.SUBJECT, stoppedSubject);
        bundle.putString(DBTimeHelper.ELAPSED_TIME, stoppedTime);
        bundle.putString(DBTimeHelper.INTERVAL, interval);
        bundle.putInt(DBTimeHelper.START_YEAR, startTimesHolder[0]);
        bundle.putInt(DBTimeHelper.START_MONTH, startTimesHolder[1]);
        bundle.putInt(DBTimeHelper.START_DAY, startTimesHolder[2]);
        bundle.putInt(DBTimeHelper.START_HOUR, startTimesHolder[3]);
        bundle.putInt(DBTimeHelper.START_MINUTE, startTimesHolder[4]);
        bundle.putInt(DBTimeHelper.END_YEAR, endYear);
        bundle.putInt(DBTimeHelper.END_MONTH, endMonth);
        bundle.putInt(DBTimeHelper.END_DAY, endDay);
        bundle.putInt(DBTimeHelper.END_HOUR, endHour);
        bundle.putInt(DBTimeHelper.END_MINUTE, endMinute);

        intent.putExtras(bundle);

        startActivity(intent);

        stop();


        timerRunning = false;
        reverseTimerRunning = false;
    }

    class UpdateReverseTimer implements Runnable {
        public void run() {

            setCountdownTimeDisplay();

            if (reverseHandler != null) {
                reverseHandler.postDelayed(this, UPDATE_EVERY);
            }
        }

    }

}

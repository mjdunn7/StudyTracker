package com.matt.studytracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import java.util.Calendar;

public class TimerService extends Service {
    private static long UPDATE_EVERY = 200;

    protected Handler handler;
    protected UpdateTimer updateTimer;
    protected boolean timerRunning;
    protected long startedAt;
    protected long stoppedAt;
    protected Notification note;
    protected String timedSubject;
    protected String humanStartTime;

    private final IBinder mBinder = new LocalBinder();
    private static final int NOTIFICATION_ID = 1;
    private Notification.Builder builder;
    private int[] startTimesHolder = new int[5];

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
                .setSmallIcon(R.drawable.studying)
                        //.setUsesChronometer(true)
                .setContentIntent(contentIntent);

        return builder.getNotification();
    }

    private void updateNotification(String subject, String timer) {

        //Notification notification = getTimerNotification(subject, timer);
        if(timerRunning) {
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
        timerRunning = false;
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
        handler = new Handler();
        updateTimer = new UpdateTimer();
        handler.postDelayed(updateTimer, UPDATE_EVERY);

    }

    public int[] getStartTimesHolder(){
        return startTimesHolder;
    }

    class UpdateTimer implements Runnable {
        /**
         * Updates the counter display and vibrate if needed.
         * Is called at regular intervals.
         */
        public void run() {

            setTimeDisplay();

            if (handler != null) {
                handler.postDelayed(this, UPDATE_EVERY);
            }
        }

    }

    protected void setTimeDisplay(){
        String display;
        long timeNow;
        long diff;
        long seconds;
        long minutes;
        long hours;

        if(timerRunning)
            timeNow = System.currentTimeMillis();
        else
            timeNow = stoppedAt;

        diff = timeNow - startedAt;

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

       // Log.d("setTimeDisplay", display);

        updateNotification("Timer", display);
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
    }
}

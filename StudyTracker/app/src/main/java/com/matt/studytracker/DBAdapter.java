package com.matt.studytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


// TO USE:
// Change the package (at top) to match your project.
// Search for "TODO", and make the appropriate changes.
public class DBAdapter {

    /////////////////////////////////////////////////////////////////////
    //	Constants & Data
    /////////////////////////////////////////////////////////////////////
    // For logging:
    private static final String TAG = "DBAdapter";

    // DB Fields
    public static final String KEY_ROWID = "_id";

    public static final int SUBJECT_COLUMN = 1;
    public static final int CREDIT_HOURS_COLUMN = 2;
    public static final int DIFFICULTY_RATING_COLUMN = 3;

    public static final int ROW_ID_COLUMN = 0;
    public static final int H_SUBJECT_COLUMN = 1;
    public static final int H_DATE_COLUMN = 2;
    public static final int H_HUMAN_DATE_COLUMN = 3;
    public static final int H_TIME_ELAPSED_COLUMN = 4;
    public static final int H_TIME_INTERVALS_COLUMN = 5;
    public static final int START_YEAR_COLUMN = 6;
    public static final int START_MONTH_COLUMN = 7;
    public static final int START_DAY_COLUMN = 8;
    public static final int START_HOUR_COLUMN = 9;
    public static final int START_MINUTE_COLUMN = 10;
    public static final int END_YEAR_COLUMN = 11;
    public static final int END_MONTH_COLUMN = 12;
    public static final int END_DAY_COLUMN = 13;
    public static final int END_HOUR_COLUMN = 14;
    public static final int END_MINUTE_COLUMN = 15;



    //subject columns
    public static final String KEY_SUBJECT = "subject";
    public static final String CREDIT_HOURS = "credit_hours";
    public static final String DIFFICULTY_RATING = "difficulty_rating";

    //history columns
    public static final String HISTORY_SUBJECT = "historySubject";
    public static final String HISTORY_DATE = "formatted_date";
    public static final String HISTORY_HUMAN_DATE = "human_readable_date";
    public static final String HISTORY_TIME_ELAPSED = "time";
    public static final String HISTORY_TIME_INTERVAL = "time_interval";
    public static final String START_YEAR = "history_start_year";
    public static final String START_MONTH = "history_start_month";
    public static final String START_DAY = "history_start_day";
    public static final String START_HOUR = "history_start_hour";
    public static final String START_MINUTE = "history_start_minute";
    public static final String END_YEAR = "history_end_year";
    public static final String END_MONTH = "history_end_month";
    public static final String END_DAY = "history_end_day";
    public static final String END_HOUR = "history_end_hour";
    public static final String END_MINUTE = "history_end_minute";

    public static final String[] ALL_SUBJECT_KEYS = new String[] {KEY_ROWID, KEY_SUBJECT, CREDIT_HOURS, DIFFICULTY_RATING};
    public static final String[] ALL_HISTORY_KEYS = new String[] {KEY_ROWID, HISTORY_SUBJECT, HISTORY_DATE,
            HISTORY_HUMAN_DATE, HISTORY_TIME_ELAPSED, HISTORY_TIME_INTERVAL, START_YEAR, START_MONTH, START_DAY,
            START_HOUR, START_MINUTE, END_YEAR, END_MONTH, END_DAY, END_HOUR, END_MINUTE};

    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "MyDb";
    public static final String SUBJECT_TABLE = "subjectTable";
    public static final String HISTORY_TABLE = "historyTable";
    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 5;

    private static final String CREATE_SUBJECT_TABLE =
            "create table " + SUBJECT_TABLE
                    + " (" + KEY_ROWID + " integer primary key autoincrement, "

                    + KEY_SUBJECT + " text not null,"
                    + CREDIT_HOURS + " text not null,"
                    + DIFFICULTY_RATING + " text not null"

                    // Rest  of creation:
                    + ");";

    private static final String CREATE_HISTORY_TABLE =
            "create table " + HISTORY_TABLE
                    + " (" + KEY_ROWID + " integer primary key autoincrement, "

                    + HISTORY_SUBJECT + " text not null,"
                    + HISTORY_DATE + " text not null,"
                    + HISTORY_HUMAN_DATE + " text not null,"
                    + HISTORY_TIME_ELAPSED + " text not null,"
                    + HISTORY_TIME_INTERVAL + " text not null,"
                    + START_YEAR + " integer not null,"
                    + START_MONTH + " integer not null,"
                    + START_DAY + " integer not null,"
                    + START_HOUR + " integer not null,"
                    + START_MINUTE + " integer not null,"
                    + END_YEAR + " integer not null,"
                    + END_MONTH + " integer not null,"
                    + END_DAY + " integer not null,"
                    + END_HOUR + " integer not null,"
                    + END_MINUTE + " integer not null"

                    + ");";

    // Context of application who uses us.
    private final Context context;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    /////////////////////////////////////////////////////////////////////
    //	Public methods:
    /////////////////////////////////////////////////////////////////////

    public DBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    // Add a new set of values to the database.
    public long insertSubjectRow(String subject, String creditHours, String difficultyRating) {

        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SUBJECT, subject);
        initialValues.put(CREDIT_HOURS, creditHours);
        initialValues.put(DIFFICULTY_RATING, difficultyRating);


        // Insert it into the database.
        return db.insert(SUBJECT_TABLE, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteSubjectRow(String subject) {
        Log.d("DBAdapter", "deleteSubjectRowCalled");
        String where = KEY_SUBJECT + "=?"; //+ rowId;
        return db.delete(SUBJECT_TABLE, where, new String[] {String.valueOf(subject)}) > 0;
    }

    public void deleteAllSubjects() {
        Cursor c = getAllSubjectRows();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if (c.moveToFirst()) {
            do {
                deleteSubjectRow(c.getString((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    // Return all data in the database.
    public Cursor getAllSubjectRows() {
        String where = null;
        Cursor c = 	db.query(true, SUBJECT_TABLE, ALL_SUBJECT_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getSubjectRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, SUBJECT_TABLE, ALL_SUBJECT_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Change an existing row to be equal to new data.
    public boolean updateSubjectRow(long rowId, String subject) {
        String where = KEY_ROWID + "=" + rowId;

        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_SUBJECT, subject);

        // Insert it into the database.
        return db.update(SUBJECT_TABLE, newValues, where, null) != 0;
    }

    public long insertHistoryRow(String subject, String time, String date, String humanDate, String interval,
                                 int startYear, int startMonth, int startDay, int startHour, int startMinute,
                                 int endYear, int endMonth, int endDay, int endHour, int endMinute) {

        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(HISTORY_SUBJECT, subject);
        initialValues.put(HISTORY_DATE, date);
        initialValues.put(HISTORY_HUMAN_DATE, humanDate);
        initialValues.put(HISTORY_TIME_ELAPSED, time);
        initialValues.put(HISTORY_TIME_INTERVAL, interval);
        initialValues.put(START_YEAR, startYear);
        initialValues.put(START_MONTH, startMonth);
        initialValues.put(START_DAY, startDay);
        initialValues.put(START_HOUR, startHour);
        initialValues.put(START_MINUTE, startMinute);
        initialValues.put(END_YEAR, endYear);
        initialValues.put(END_MONTH, endMonth);
        initialValues.put(END_DAY, endDay);
        initialValues.put(END_HOUR, endHour);
        initialValues.put(END_MINUTE, endMinute);

        // Insert it into the database.
        return db.insert(HISTORY_TABLE, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteHistoryRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return db.delete(HISTORY_TABLE, where, null) != 0;
    }

    public boolean deleteHistoryRows(String subject) {
        Log.d("DBAdapter", "deleteHistoryRowsCalled");
        String where = HISTORY_SUBJECT + "=?"; //+ rowId;
        return db.delete(HISTORY_TABLE, where, new String[] {String.valueOf(subject)}) > 0;
    }

    public void deleteAllHistory() {
        Cursor c = getAllHistoryRows();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if (c.moveToFirst()) {
            do {
                deleteHistoryRow(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    // Return all data in the database.
    public Cursor getAllHistoryRows() {
        String where = null;
        Cursor c = 	db.query(true, HISTORY_TABLE, ALL_HISTORY_KEYS,
                where, null, null, null, HISTORY_DATE + " DESC", null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getHistoryRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, SUBJECT_TABLE, ALL_HISTORY_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Change an existing row to be equal to new data.
    public boolean updateHistoryRow(long rowId, String subject, String time, String date, String humanDate, String interval,
                                    int startYear, int startMonth, int startDay, int startHour, int startMinute,
                                    int endYear, int endMonth, int endDay, int endHour, int endMinute) {
        String where = KEY_ROWID + "=" + rowId;

		/*
		 * CHANGE 4:
		 */
        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(HISTORY_SUBJECT, subject);
        newValues.put(HISTORY_DATE, date);
        newValues.put(HISTORY_HUMAN_DATE, humanDate);
        newValues.put(HISTORY_TIME_ELAPSED, time);
        newValues.put(HISTORY_TIME_INTERVAL, interval);
        newValues.put(START_YEAR, startYear);
        newValues.put(START_MONTH, startMonth);
        newValues.put(START_DAY, startDay);
        newValues.put(START_HOUR, startHour);
        newValues.put(START_MINUTE, startMinute);
        newValues.put(END_YEAR, endYear);
        newValues.put(END_MONTH, endMonth);
        newValues.put(END_DAY, endDay);
        newValues.put(END_HOUR, endHour);
        newValues.put(END_MINUTE, endMinute);


        // Insert it into the database.
        return db.update(HISTORY_TABLE, newValues, where, null) != 0;
    }



    /////////////////////////////////////////////////////////////////////
    //	Private Helper Classes:
    /////////////////////////////////////////////////////////////////////

    /**
     * Private class which handles database creation and upgrading.
     * Used to handle low-level database access.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {

            _db.execSQL(CREATE_HISTORY_TABLE);
            _db.execSQL(CREATE_SUBJECT_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + SUBJECT_TABLE);
            _db.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}



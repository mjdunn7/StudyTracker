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
    public static final int COL_ROWID = 0;


    //subject columns
    public static final String KEY_SUBJECT = "subject";

    //history columns
    public static final String HISTORY_SUBJECT = "historySubject";
    public static final String HISTORY_DATE = "date";
    public static final String HISTORY_TIME = "time";


    public static final String[] ALL_SUBJECT_KEYS = new String[] {KEY_ROWID, KEY_SUBJECT};
    public static final String[] ALL_HISTORY_KEYS = new String[] {KEY_ROWID, HISTORY_SUBJECT, HISTORY_DATE, HISTORY_TIME};

    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "MyDb";
    public static final String SUBJECT_TABLE = "subjectTable";
    public static final String HISTORY_TABLE = "historyTable";
    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 3;

    private static final String CREATE_SUBJECT_TABLE =
            "create table " + SUBJECT_TABLE
                    + " (" + KEY_ROWID + " integer primary key autoincrement, "

                    + KEY_SUBJECT + " text not null"

                    // Rest  of creation:
                    + ");";

    private static final String CREATE_HISTORY_TABLE =
            "create table " + HISTORY_TABLE
                    + " (" + KEY_ROWID + " integer primary key autoincrement, "

                    + HISTORY_SUBJECT + " text not null,"
                    + HISTORY_DATE + " text not null,"
                    + HISTORY_TIME + " text not null"

                    // Rest  of creation:
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
    public long insertSubjectRow(String subject) {

        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SUBJECT, subject);


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

		/*
		 * CHANGE 4:
		 */
        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_SUBJECT, subject);

        // Insert it into the database.
        return db.update(SUBJECT_TABLE, newValues, where, null) != 0;
    }

    public long insertHistoryRow(String subject, String time, String date) {
		/*
		 * CHANGE 3:
		 */
        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(HISTORY_SUBJECT, subject);
        initialValues.put(HISTORY_DATE, date);
        initialValues.put(HISTORY_TIME, time);


        // Insert it into the database.
        return db.insert(HISTORY_TABLE, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteHistoryRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return db.delete(SUBJECT_TABLE, where, null) != 0;
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
                where, null, null, null, null, null);
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
    public boolean updateHistoryRow(long rowId, String subject, String time, String date) {
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
        newValues.put(HISTORY_TIME, time);


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



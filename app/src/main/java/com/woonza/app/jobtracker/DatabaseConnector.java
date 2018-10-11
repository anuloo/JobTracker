package com.woonza.app.jobtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 10/18/2016.
 */

public class DatabaseConnector {

    // database name
    private static final String DATABASE_NAME = "JobApplicaton";

    private SQLiteDatabase database; // for interacting with the database
    private DatabaseOpenHelper databaseOpenHelper; // creates the database

    // public constructor for DatabaseConnector
    public DatabaseConnector(Context context)
    {
        // create a new DatabaseOpenHelper
        databaseOpenHelper =
                new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    // open the database connection
    public void open() throws SQLException
    {
        // create or open a database for reading/writing
        database = databaseOpenHelper.getWritableDatabase();
    }

    // close the database connection
    public void close()
    {
        if (database != null)
            database.close(); // close the database connection
    }

    // inserts a new job in the database
    public long insertJob(String title, String employer, String agency, String agent,
                              String phone, String email, String interview, String jobtype, String interviewdate, String doc, String date)
    {
        ContentValues newJob = new ContentValues();
        newJob.put("title", title);
        newJob.put("employer", employer);
        newJob.put("agency", agency);
        newJob.put("agent", agent);
        newJob.put("phone", phone);
        newJob.put("email", email);
        newJob.put("interview", interview);
        newJob.put("jobtype", jobtype);
        newJob.put("interviewdate", interviewdate);
        newJob.put("doc", doc);
        newJob.put("date", date);

        open(); // open the database
        long rowID = database.insert("jobs", null, newJob);
        close(); // close the database
        return rowID;
    }

    // updates an existing job in the database
    public void updateJob(long id, String title, String employer, String agency, String agent,
                              String phone, String email, String interview, String jobtype, String interviewdate, String doc)
    {
        ContentValues editJob = new ContentValues();
        editJob.put("title", title);
        editJob.put("employer", employer);
        editJob.put("agency", agency);
        editJob.put("agent", agent);
        editJob.put("phone", phone);
        editJob.put("email", email);
        editJob.put("interview", interview);
        editJob.put("jobtype", jobtype);
        editJob.put("interviewdate", interviewdate);
        editJob.put("doc", doc);

        open(); // open the database
        database.update("jobs", editJob, "_id=" + id, null);
        close(); // close the database
    } // end method updateJob

    // return a Cursor with all job title in the database
    public Cursor getAllJobs()
    {
        return database.query("jobs", new String[] {"_id", "title", "employer", "date"},
                null, null, null, null, "title ASC", null);
    }

    // return a Cursor containing specified job's information
    public Cursor getOneJob(long id)
    {
        return database.query(
                "jobs", null, "_id=" + id, null, null, null, null);
    }

    // delete the job specified by the given String name
    public void deleteJob(long id)
    {
        open(); // open the database
        database.delete("jobs", "_id=" + id, null);
        close(); // close the database
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper
    {
        // constructor
        public DatabaseOpenHelper(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        }

        // creates the contacts table when the database is created
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            // query to create a new table named contacts
            String createQuery = "CREATE TABLE jobs" +
                    "(_id integer primary key autoincrement," +
                    "title TEXT, employer TEXT, agency TEXT, agent TEXT," +
                    "phone TEXT, email TEXT, interview TEXT, jobtype TEXT, interviewdate TEXT, doc TEXT, date TEXT);";

            db.execSQL(createQuery); // execute query to create the database
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
        }
    } // end class DatabaseOpenHelper
}

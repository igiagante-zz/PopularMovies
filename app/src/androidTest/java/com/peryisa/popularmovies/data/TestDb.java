package com.peryisa.popularmovies.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

import data.MovieDbHelper;

import data.MovieContract.MovieEntry;
import data.MovieContract.TrailerEntry;
import data.MovieContract.ReviewEntry;

/**
 * Created by igiagante on 16/9/15.
 */
public class TestDb extends AndroidTestCase {

    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieEntry.TABLE_NAME);
        tableNameHashSet.add(TrailerEntry.TABLE_NAME);
        tableNameHashSet.add(ReviewEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);

        MovieDbHelper movieDbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieEntry._ID);
        movieColumnHashSet.add(MovieEntry.COLUMN_DESCRIPTION);
        movieColumnHashSet.add(MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieEntry.COLUMN_DURATION);
        movieColumnHashSet.add(MovieEntry.COLUMN_VOTE_AVERAGE);
        movieColumnHashSet.add(MovieEntry.COLUMN_RELEASE_DATE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                movieColumnHashSet.isEmpty());
        db.close();
    }
}

package com.peryisa.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import data.MovieContract.MovieEntry;
import data.MovieContract.ReviewEntry;
import data.MovieContract.TrailerEntry;
import data.MovieDbHelper;

/**
 * Created by igiagante on 16/9/15.
 */
public class TestProvider extends AndroidTestCase {

    public void deleteAllRecords() {
        TestUtilities.cleanDataBase(mContext);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        deleteAllRecords();
    }

    public void testGetType() {

        final long TEST_MOVIE_ID = 12345;

        // content://com.peryisa.provider.popularmovies/movie
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.peryisa.provider.popularmovies/movie
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        // content://com.peryisa.provider.popularmovies/movie
        type = mContext.getContentResolver().getType(MovieEntry.buildMovieUri(TEST_MOVIE_ID));
        // vnd.android.cursor.dir/com.peryisa.provider.popularmovies/movie
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE_ITEM, type);

        // content://com.peryisa.provider.popularmovies/trailer
        type = mContext.getContentResolver().getType(TrailerEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.peryisa.provider.popularmovies/trailer
        assertEquals("Error: the TrailerEntry CONTENT_URI should return TrailerEntry.CONTENT_TYPE",
                TrailerEntry.CONTENT_TYPE, type);

        // content://com.peryisa.provider.popularmovies/review
        type = mContext.getContentResolver().getType(ReviewEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.peryisa.provider.popularmovies/review
        assertEquals("Error: the ReviewEntry CONTENT_URI should return ReviewEntry.CONTENT_TYPE",
                ReviewEntry.CONTENT_TYPE, type);

    }

    public void testMovieTrailerQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long movieRowId = TestUtilities.insertMovieValues(mContext);

        //testing trailers values
        ContentValues trailerValues = TestUtilities.createTrailerValues(movieRowId);

        long trailerRowId = db.insert(TrailerEntry.TABLE_NAME, null, trailerValues);
        assertTrue("Unable to Insert TrailerEntry into the Database", trailerRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor trailerCursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testQuery", trailerCursor, trailerValues);
    }

    public void testMovieReviewQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long movieRowId = TestUtilities.insertMovieValues(mContext);

        //testing reviews values
        ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);

        long reviewRowId = db.insert(ReviewEntry.TABLE_NAME, null, reviewValues);
        assertTrue("Unable to Insert ReviewEntry into the Database", reviewRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testQuery", reviewCursor, reviewValues);
    }
}

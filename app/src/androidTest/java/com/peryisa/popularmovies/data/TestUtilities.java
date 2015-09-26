package com.peryisa.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import data.MovieContract;
import data.MovieContract.MovieEntry;
import data.MovieContract.ReviewEntry;
import data.MovieContract.TrailerEntry;
import data.MovieDbHelper;
import model.Movie;
import model.Review;
import model.Trailer;

/**
 * Created by igiagante on 16/9/15.
 */
public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createMovieValues() {

        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieEntry.COLUMN_TITLE, "Mad Max");
        movieValues.put(MovieEntry.COLUMN_DESCRIPTION, "Description");
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, "Poster_Path");
        movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, 7.6);
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, "2015-05-05");

        return movieValues;
    }

    static ContentValues createTrailerValues(long movieRowId) {

        ContentValues trailerValues = new ContentValues();

        trailerValues.put(TrailerEntry.COLUMN_MOVIE_KEY, movieRowId);
        trailerValues.put(TrailerEntry.COLUMN_TRAILER_ID, "KLDF0987Q45J");
        trailerValues.put(TrailerEntry.COLUMN_KEY, "vadf7896adva");

        return trailerValues;
    }

    static ContentValues createReviewValues(long movieRowId) {

        ContentValues reviewValues = new ContentValues();

        reviewValues.put(ReviewEntry.COLUMN_MOVIE_KEY, movieRowId);
        reviewValues.put(ReviewEntry.COLUMN_REVIEW_ID, "adf234jkhuviy");
        reviewValues.put(ReviewEntry.COLUMN_AUTHOR, "PEDRO");
        reviewValues.put(ReviewEntry.COLUMN_CONTENT, "AMAZING");

        return reviewValues;
    }

    static long insertMovieValues(Context mContext) {

        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();

        long movieRowId = db.insert(MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Movie Values", movieRowId != -1);

        return movieRowId;
    }

    static Movie buildMovie(long id){

        Movie movie = new Movie();
        movie.setId(id);
        movie.setTitle("Mad Max");
        movie.setDescription("description");
        movie.setPosterPath("poster_path");
        movie.setDuration("120");
        movie.setVoteAverage((long) 7.4);
        movie.setReleaseDate("2015-05-05");

        return movie;
    }

    static Movie addTrailers(Movie movie){

        ArrayList<Trailer> trailers = new ArrayList<>();

        Trailer trailerOne = new Trailer();
        trailerOne.setName("trailerOne");
        trailerOne.setKey("nice");
        trailerOne.setTrailerId("adfa345");

        trailers.add(trailerOne);

        Trailer trailerTwo = new Trailer();
        trailerTwo.setName("trailerTwo");
        trailerTwo.setKey("two");
        trailerTwo.setTrailerId("aadsfae5");

        trailers.add(trailerTwo);

        movie.setTrailers(trailers);
        return movie;
    }

    static Movie addReviews(Movie movie){

        ArrayList<Review> reviews = new ArrayList<>();

        Review reviewOne = new Review();
        reviewOne.setReviewId("jklahds98f");
        reviewOne.setAuthor("PEDRO");
        reviewOne.setContent("amazing");

        reviews.add(reviewOne);

        Review reviewTwo = new Review();
        reviewTwo.setReviewId("908127345h");
        reviewTwo.setAuthor("JOSE");
        reviewTwo.setContent("nice");

        reviews.add(reviewTwo);

        movie.setReviews(reviews);
        return movie;
    }

    static void cleanDataBase(Context mContext){

        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Trailer table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();
    }
}

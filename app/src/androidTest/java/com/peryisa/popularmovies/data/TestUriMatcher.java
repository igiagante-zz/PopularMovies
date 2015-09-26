package com.peryisa.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import data.MovieContract.MovieEntry;
import data.MovieContract.TrailerEntry;
import data.MovieContract.ReviewEntry;
import data.MovieProvider;

/**
 * Created by igiagante on 16/9/15.
 */
public class TestUriMatcher extends AndroidTestCase {

    private static final Uri TEST_MOVIE_DIR = MovieEntry.CONTENT_URI;
    private static final Uri TEST_TRAILER_DIR = TrailerEntry.CONTENT_URI;
    private static final Uri TEST_REVIEW_DIR = ReviewEntry.CONTENT_URI;

    public void testUriMatcher() {

        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);

        assertEquals("Error: The TRAILER URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILER_DIR), MovieProvider.TRAILER);

        assertEquals("Error: The REVIEW URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEW_DIR), MovieProvider.REVIEW);
    }
}

package com.peryisa.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import data.MovieContract;

/**
 * Created by igiagante on 16/9/15.
 */
public class TestMovieContract  extends AndroidTestCase {

    private static final long TEST_MOVIE_ID = 12345;

    public void testBuildMovieUri() {
        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned.", movieUri);
        assertEquals("Error: Movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.peryisa.provider.popularmovies/movie/12345");
    }

    public void testBuildMovieWithTrailersUri() {

        Uri movieUri = MovieContract.MovieEntry.buildMovieWithTrailersUri(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned.", movieUri);
        assertEquals("Error: Movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.peryisa.provider.popularmovies/movie/12345/trailers");
    }

    public void testBuildMovieWitReviewsUri() {
        Uri movieUri = MovieContract.MovieEntry.buildMovieWithReviewsUri(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned.", movieUri);
        assertEquals("Error: Movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.peryisa.provider.popularmovies/movie/12345/reviews");
    }
}


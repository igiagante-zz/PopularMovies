package com.peryisa.popularmovies.data;

import android.test.AndroidTestCase;

import java.util.ArrayList;

import data.MovieDao;
import model.Movie;

/**
 * Created by igiagante on 16/9/15.
 */
public class TestMovieDao extends AndroidTestCase {

    MovieDao movieDao;

    public void deleteAllRecords() {
        TestUtilities.cleanDataBase(mContext);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
        movieDao = new MovieDao(mContext);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        deleteAllRecords();
    }

    public void testAddMovie(){

        Movie movie = TestUtilities.buildMovie(76341);
        TestUtilities.addTrailers(movie);
        TestUtilities.addReviews(movie);

        long movieId = movieDao.createMovie(movie);

        Movie movieFromDb = movieDao.getMovie(movieId);

        assertEquals(movie.getId(), movieFromDb.getId());
        assertEquals(movie.getTitle(), movieFromDb.getTitle());
        assertEquals(movie.getDescription(), movieFromDb.getDescription());
        assertEquals(movie.getPosterPath(), movieFromDb.getPosterPath());
        assertEquals(movie.getDuration(), movieFromDb.getDuration());
        assertEquals(movie.getVoteAverage(), movieFromDb.getVoteAverage());
        assertEquals(movie.getReleaseDate(), movieFromDb.getReleaseDate());
    }

    public void testGetMovieWithTrailers(){

        Movie movie = TestUtilities.buildMovie(76341);
        TestUtilities.addTrailers(movie);

        //persist movie with trailers
        movieDao.createMovie(movie);

        //get trailers for a movie
        Movie movieFromDb = movieDao.getMovieTrailers(movie);

        assertEquals(movie.getTitle(), movieFromDb.getTitle());
        assertEquals(movie.getDescription(), movieFromDb.getDescription());
        assertEquals(movie.getPosterPath(), movieFromDb.getPosterPath());
        assertEquals(movie.getDuration(), movieFromDb.getDuration());
        assertEquals(movie.getVoteAverage(), movieFromDb.getVoteAverage());
        assertEquals(movie.getReleaseDate(), movieFromDb.getReleaseDate());

        assertTrue(movieFromDb.getTrailers().size() == 2);
    }

    public void testGetMovieWithReviews(){

        Movie movie = TestUtilities.buildMovie(76341);
        TestUtilities.addReviews(movie);

        //persist movie with reviews
        movieDao.createMovie(movie);

        //get reviews for a movie
        Movie movieFromDb = movieDao.getMovieWithReviews(movie);

        assertEquals(movie.getTitle(), movieFromDb.getTitle());
        assertEquals(movie.getDescription(), movieFromDb.getDescription());
        assertEquals(movie.getPosterPath(), movieFromDb.getPosterPath());
        assertEquals(movie.getDuration(), movieFromDb.getDuration());
        assertEquals(movie.getVoteAverage(), movieFromDb.getVoteAverage());
        assertEquals(movie.getReleaseDate(), movieFromDb.getReleaseDate());

        assertTrue(movieFromDb.getReviews().size() == 2);
    }

    public void testGetAllmovies(){

        Movie movieOne = TestUtilities.buildMovie(76341);
        TestUtilities.addTrailers(movieOne);
        TestUtilities.addReviews(movieOne);

        movieDao.createMovie(movieOne);

        Movie movieTwo = TestUtilities.buildMovie(76342);
        TestUtilities.addTrailers(movieTwo);
        TestUtilities.addReviews(movieTwo);

        movieDao.createMovie(movieTwo);

        Movie movieThree = TestUtilities.buildMovie(76343);
        TestUtilities.addTrailers(movieThree);
        TestUtilities.addReviews(movieThree);

        movieDao.createMovie(movieThree);

        ArrayList<Movie> movies = movieDao.getMovies();

        assertEquals(movies.size(), 3);
    }

    public void testDeleteMovie(){

        Movie movie = TestUtilities.buildMovie(76341);
        TestUtilities.addTrailers(movie);
        TestUtilities.addReviews(movie);

        long movieId = movieDao.createMovie(movie);

        int rowDeleted = movieDao.deleteMovie(movie);

        assertEquals(rowDeleted, 1);

        assertNull(movieDao.getMovie(movieId));
    }
}

package data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

import data.MovieContract.MovieEntry;
import data.MovieContract.ReviewEntry;
import data.MovieContract.TrailerEntry;
import model.Movie;
import model.Review;
import model.Trailer;

/**
 * uUsed to separate low level data accessing API or operations from high level business service.
 * @author igiagante, on 15/9/15.
 */
public class MovieDao {

    private final Context mContext;

    //These alias allow to create easier queries.
    public final static String MOVIE_ID_ALIAS = MovieEntry.TABLE_NAME + MovieEntry._ID;
    public final static String MOVIE_ID_KEY = MovieEntry.TABLE_NAME + "." + MovieEntry._ID;
    public final static String MOVIE_ID_KEY_ALIAS = MOVIE_ID_KEY + " AS " + MOVIE_ID_ALIAS;

    public final static String TRAILER_ID_ALIAS = TrailerEntry.TABLE_NAME + TrailerEntry._ID + "_alias";
    public final static String TRAILER_ID_KEY = TrailerEntry.TABLE_NAME + "." + TrailerEntry._ID;
    public final static String TRAILER_ID_KEY_ALIAS = TRAILER_ID_KEY + " AS " + TRAILER_ID_ALIAS;

    public final static String REVIEW_ID_ALIAS = ReviewEntry.TABLE_NAME + ReviewEntry._ID + "_alias";
    public final static String REVIEW_ID_KEY = ReviewEntry.TABLE_NAME + "." + ReviewEntry._ID;
    public final static String REVIEW_ID_KEY_ALIAS = REVIEW_ID_KEY + " AS " + REVIEW_ID_ALIAS;

    public MovieDao(Context context){
        this.mContext = context;
    }

    private String[] allColumnsMovie = {
            MOVIE_ID_KEY_ALIAS,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_DESCRIPTION,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_DURATION,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_RELEASE_DATE };

    private String[] allColumnsTrailer = {
            TRAILER_ID_KEY_ALIAS,
            TrailerEntry.COLUMN_MOVIE_KEY,
            TrailerEntry.COLUMN_TRAILER_ID,
            TrailerEntry.COLUMN_KEY };

    private String[] allColumnsReview = {
            REVIEW_ID_KEY_ALIAS,
            ReviewEntry.COLUMN_MOVIE_KEY,
            ReviewEntry.COLUMN_REVIEW_ID,
            ReviewEntry.COLUMN_AUTHOR,
            ReviewEntry.COLUMN_CONTENT };


    /**
     * Persist a movie object into the database.
     * @param movie Object to be persisted.
     * @return long id from object persisted.
     */
    public long createMovie(Movie movie) {

        long movieId;

        //check if the movie is already in database
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                new String[]{MovieEntry._ID},
                MovieEntry._ID + " = ?",
                new String[]{getId(movie.getId())},
                null);

        if (movieCursor.moveToFirst()) {
            int movieIdIndex = movieCursor.getColumnIndex(MovieEntry._ID);
            movieId = movieCursor.getLong(movieIdIndex);
        } else {

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieEntry._ID, movie.getId());
            movieValues.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
            movieValues.put(MovieEntry.COLUMN_DESCRIPTION, movie.getDescription());
            movieValues.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            movieValues.put(MovieEntry.COLUMN_DURATION, movie.getDuration());
            movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());

            Uri insertedUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI,
                    movieValues
            );
            movieId = ContentUris.parseId(insertedUri);
        }

        movieCursor.close();

        //One the movie was created, let's add some trailers
        for(Trailer trailer : movie.getTrailers()){
            addTrailer(movie.getId(), trailer);
        }

        //One the movie was created, let's add some reviews
        for(Review review : movie.getReviews()){
            addReview(movie.getId(), review);
        }

        return movieId;
    }

    /**
     * Adds a trailer to one movie.
     * @param movieId represents the id from one movie.
     * @param trailer represents the trailer object.
     * @return long id from object persisted.
     */
    public long addTrailer(long movieId, Trailer trailer) {

        long trailerId;

        ContentValues trailerValues = new ContentValues();

        trailerValues.put(TrailerEntry.COLUMN_MOVIE_KEY, movieId);
        trailerValues.put(TrailerEntry.COLUMN_TRAILER_ID, trailer.getTrailerId());
        trailerValues.put(TrailerEntry.COLUMN_KEY, trailer.getKey());

        Uri insertedUri = mContext.getContentResolver().insert(TrailerEntry.CONTENT_URI,
                trailerValues
        );

        trailerId = ContentUris.parseId(insertedUri);
        trailer.setId(trailerId);

        return trailerId;
    }

    /**
     * Returns a movie with its corresponding trailers.
     * @param movie the movie object.
     * @return Movie
     */
    public Movie getMovieTrailers(Movie movie) {

        Cursor trailerCursor = mContext.getContentResolver().query(TrailerEntry.CONTENT_URI,
                allColumnsTrailer,
                TrailerEntry.COLUMN_MOVIE_KEY + " = ?",
                new String[]{getId(movie.getId())},
                null);

        trailerCursor.moveToFirst();
        movie.getTrailers().clear();

        while (!trailerCursor.isAfterLast()){
            setTrailerCursorData(trailerCursor, movie);
            trailerCursor.moveToNext();
        }

        return movie;
    }

    /**
     * Adds a review to one movie.
     * @param movieId represents the id from one movie.
     * @param review represents the trailer object.
     * @return long id from object persisted.
     */
    public long addReview(long movieId, Review review) {

        long reviewId;

        ContentValues reviewValues = new ContentValues();

        reviewValues.put(ReviewEntry.COLUMN_MOVIE_KEY, movieId);
        reviewValues.put(ReviewEntry.COLUMN_REVIEW_ID, review.getReviewId());
        reviewValues.put(ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
        reviewValues.put(ReviewEntry.COLUMN_CONTENT, review.getContent());

        Uri insertedUri = mContext.getContentResolver().insert(ReviewEntry.CONTENT_URI,
                reviewValues
        );

        reviewId = ContentUris.parseId(insertedUri);
        review.setId(reviewId);

        return reviewId;
    }

    /**
     * Returns a movie with its corresponding reviews.
     * @param movie the movie object.
     * @return Movie
     */
    public Movie getMovieWithReviews(Movie movie) {

        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                allColumnsReview,
                ReviewEntry.COLUMN_MOVIE_KEY + " = ?",
                new String[]{getId(movie.getId())},
                null);

        reviewCursor.moveToFirst();
        movie.getReviews().clear();

        while (!reviewCursor.isAfterLast()){
            setReviewCursorData(reviewCursor, movie);
            reviewCursor.moveToNext();
        }

        return movie;
    }

    /**
     * Gets a movie from database.
     * @param movieId id from the object to be requested.
     * @return movie.
     */
    public Movie getMovie(long movieId) {

        Movie movie = new Movie();

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                allColumnsMovie,
                MovieEntry._ID + " = ?",
                new String[]{getId(movieId)},
                null);

        if (movieCursor.moveToFirst()) {
            setMovieCursorData(movieCursor, movie);
            return movie;
        }

        return null;
    }

    /**
     * Gets all the movies from database.
     * @return ArrayList<Movie>.
     */
    public ArrayList<Movie> getMovies() {

        ArrayList<Movie> movies = new ArrayList<>();

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                allColumnsMovie,
                null,
                null,
                null);

        movieCursor.moveToFirst();
        while(!movieCursor.isAfterLast()){

            Movie movie = new Movie();
            setMovieCursorData(movieCursor, movie);

            //add trailers
            for(Trailer trailer : movie.getTrailers()){
                addTrailer(movie.getId(), trailer);
            }

            //add reviews
            for(Review review : movie.getReviews()){
                addReview(movie.getId(), review);
            }

            movies.add(movie);
            movieCursor.moveToNext();
        }

        return movies;
    }

    /**
     * Delete a movie with its trailers and reviews.
     * @param movie the object to be deleted.
     * @return count the number of rows deleted.
     */
    public int deleteMovie(Movie movie) {

        //delete all trailers attached to the movie
        for(Trailer trailer : movie.getTrailers()){
           mContext.getContentResolver().delete(
                    TrailerEntry.CONTENT_URI,
                    TrailerEntry._ID + " = ?",
                    new String[]{ getId(trailer.getId())});
        }

        //delete all reviews attached to the movie
        for(Review review : movie.getReviews()){
             mContext.getContentResolver().delete(
                    ReviewEntry.CONTENT_URI,
                    ReviewEntry._ID + " = ?",
                    new String[]{ getId(review.getId())});
        }
        //delete movie
        int count = mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                MovieEntry._ID + " = ?",
                new String[]{ getId(movie.getId())});

        return count;
    }

    /**
     * Checks if one movie exits in the database.
     * @param movieId id from movie object.
     * @return boolean.
     */
    public boolean existMovie(long movieId){

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                allColumnsMovie,
                MovieEntry._ID + " = ?",
                new String[]{getId(movieId)},
                null);

        return movieCursor.getCount() == 1;
    }

    private Movie setMovieCursorData(Cursor movieCursor, Movie movie){

        int movieIdIndex = movieCursor.getColumnIndex(MOVIE_ID_ALIAS);
        int titleIndex = movieCursor.getColumnIndex(MovieEntry.COLUMN_TITLE);
        int descriptionIndex = movieCursor.getColumnIndex(MovieEntry.COLUMN_DESCRIPTION);
        int posterPathIndex = movieCursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH);
        int durationIndex = movieCursor.getColumnIndex(MovieEntry.COLUMN_DURATION);
        int voteAverageIndex = movieCursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE);
        int releaseDateIndex = movieCursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE);

        movie.setId(movieCursor.getLong(movieIdIndex));
        movie.setTitle(movieCursor.getString(titleIndex));
        movie.setDescription(movieCursor.getString(descriptionIndex));
        movie.setPosterPath(movieCursor.getString(posterPathIndex));
        movie.setDuration(movieCursor.getString(durationIndex));
        movie.setVoteAverage(movieCursor.getLong(voteAverageIndex));
        movie.setReleaseDate(movieCursor.getString(releaseDateIndex));

        return movie;
    }

    private void setTrailerCursorData(Cursor trailerCursor, Movie movie){

        int tidIndex = trailerCursor.getColumnIndex(TRAILER_ID_ALIAS);
        int trailerIdIndex = trailerCursor.getColumnIndex(TrailerEntry.COLUMN_TRAILER_ID);
        int trailerKeyIndex = trailerCursor.getColumnIndex(TrailerEntry.COLUMN_KEY);

        Trailer trailer = new Trailer();

        trailer.setId(trailerCursor.getLong(tidIndex));
        trailer.setTrailerId(trailerCursor.getString(trailerIdIndex));
        trailer.setKey(trailerCursor.getString(trailerKeyIndex));

        movie.getTrailers().add(trailer);
    }

    private void setReviewCursorData(Cursor reviewCursor, Movie movie){

        int tidIndex = reviewCursor.getColumnIndex(REVIEW_ID_ALIAS);
        int reviewIdIndex = reviewCursor.getColumnIndex(ReviewEntry.COLUMN_REVIEW_ID);
        int authorIndex = reviewCursor.getColumnIndex(ReviewEntry.COLUMN_AUTHOR);
        int contentIndex = reviewCursor.getColumnIndex(ReviewEntry.COLUMN_CONTENT);

        Review review = new Review();

        review.setId(reviewCursor.getLong(tidIndex));
        review.setReviewId(reviewCursor.getString(reviewIdIndex));
        review.setAuthor(reviewCursor.getString(authorIndex));
        review.setContent(reviewCursor.getString(contentIndex));

        movie.getReviews().add(review);
    }

    private String getId(long id){
        return String.valueOf(id);
    }
}

package parsers;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import model.Movie;
import model.Review;
import model.Trailer;

/**
 * This class consists exclusively of static methods that are used to parser JSON data into String.
 * @author igiagante, on 3/9/15.
 */
public class MovieParser {

    /**
     * Converts the result into a list of movies.
     * @param result - the data which is going to be parse
     * @return Movie
     * @throws JSONException
     */
    public static ArrayList<Movie> parseMoviesJson(String result) throws JSONException {

        ArrayList<Movie> movies = new ArrayList<>();
        final String RESULTS = "results";

        JSONObject resultJSON = new JSONObject(result);
        JSONArray moviesJSONArray = resultJSON.getJSONArray(RESULTS);

        for (int i = 0; i < moviesJSONArray.length(); i++) {

            JSONObject movieJson = moviesJSONArray.getJSONObject(i);
            movies.add(parseMovieJson(movieJson));
        }

        return movies;
    }

    /**
     * Converts one movie JSON data into an object movie.
     * @param movieJson - the data which is going to be parse
     * @return Movie
     * @throws JSONException
     */
    public static Movie parseMovieJson(JSONObject movieJson) throws JSONException{

        final String MOVIE_ID = "id";
        final String TITLE = "title";
        final String DESCRIPTION = "overview";
        final String POSTER_PATH = "poster_path";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";

        Movie movie = new Movie();

        movie.setId(movieJson.getInt(MOVIE_ID));
        movie.setTitle(movieJson.getString(TITLE));

        String description = movieJson.getString(DESCRIPTION);
        description = description.equals("null") ? "" : description;
        movie.setDescription(description);

        movie.setPosterPath(movieJson.getString(POSTER_PATH));
        movie.setVoteAverage(Float.parseFloat(movieJson.getString(VOTE_AVERAGE)));

        String releaseDate = movieJson.getString(RELEASE_DATE);
        releaseDate = releaseDate.equals("null") ? "" : releaseDate;

        movie.setReleaseDate(releaseDate);

        return movie;
    }

    /**
     * Converts the result into a list of trailers.
     * @param result - the data which is going to be parse
     * @return ArrayList<Trailer>
     * @throws JSONException
     */
    public static ArrayList<Trailer> parseTrailersJson(String result) throws JSONException{

        ArrayList<Trailer> trailers = new ArrayList<>();
        final String RESULTS = "results";

        JSONObject resultJSON = new JSONObject(result);
        JSONArray trailersJSONArray = resultJSON.getJSONArray(RESULTS);

        for (int i = 0; i < trailersJSONArray.length(); i++) {

            JSONObject trailerJson = trailersJSONArray.getJSONObject(i);
            trailers.add(parseTrailerJson(trailerJson));
        }

        return trailers;
    }

    /**
     * Converts one trailer JSON data into an object trailer.
     * @param trailerJson - the data which is going to be parse
     * @return Trailer
     * @throws JSONException
     */
    public static Trailer parseTrailerJson(JSONObject trailerJson) throws JSONException{

        // These are the names of the JSON objects that need to be extracted.
        final String TRAILER_ID = "id";
        final String NAME = "name";
        final String KEY = "key";

        Trailer trailer = new Trailer();
        trailer.setTrailerId(trailerJson.getString(TRAILER_ID));
        trailer.setName(trailerJson.getString(NAME));
        trailer.setKey(trailerJson.getString(KEY));

        return trailer;
    }

    /**
     * Converts the result into a list of reviews.
     * @param result - the data which is going to be parse
     * @return ArrayList<Review>
     * @throws JSONException
     */
    public static ArrayList<Review> parseReviewsJson(String result) throws JSONException{

        ArrayList<Review> reviews = new ArrayList<>();
        final String RESULTS = "results";

        JSONObject resultJSON = new JSONObject(result);
        JSONArray reviewsJSONArray = resultJSON.getJSONArray(RESULTS);

        for (int i = 0; i < reviewsJSONArray.length(); i++) {

            JSONObject reviewJson = reviewsJSONArray.getJSONObject(i);
            reviews.add(parseReviewJson(reviewJson));
        }

        return reviews;
    }

    /**
     * Converts one review JSON data into an object review.
     * @param reviewJson - the data which is going to be parse
     * @return Review
     * @throws JSONException
     */
    public static Review parseReviewJson(JSONObject reviewJson) throws JSONException{

        // These are the names of the JSON objects that need to be extracted.
        final String REVIEW_ID = "id";
        final String AUTHOR = "author";
        final String CONTENT = "content";

        Review review = new Review();
        review.setReviewId(reviewJson.getString(REVIEW_ID));
        review.setAuthor(reviewJson.getString(AUTHOR));
        review.setContent(reviewJson.getString(CONTENT));

        return review;
    }
}

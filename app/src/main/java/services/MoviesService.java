package services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.peryisa.popularmovies.R;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import data.MovieDao;
import enums.TypeOfMovie;
import model.Movie;
import model.Review;
import model.Trailer;
import model.VideoLibrary;
import parsers.MovieParser;

/**
 * This service provides access to the api.themoviedb.org
 * @author igiagante, on 9/9/15.
 */
public class MoviesService extends IntentService{

    private final String LOG_TAG = MoviesService.class.getSimpleName();

    //Params
    public static final String PARAM_VIDEO_LIBRARY = "PARAM_VIDEO_LIBRARY";
    public static final String PARAM_SORT_BY = "PARAM_SORT_BY";
    public static final String PARAM_MOVIE = "PARAM_MOVIE";
    public static final String PARAM_LANDSCAPE = "PARAM_LANDSCAPE";

    //Actions
    public static final String ACTION_GET_MOVIES = "ACTION_GET_MOVIES";
    public static final String ACTION_GET_TRAILERS = "ACTION_GET_TRAILERS";
    public static final String ACTION_GET_REVIEWS = "ACTION_GET_REVIEWS";

    //Notifications
    public static final String NOTIFICATION_GET_MOVIES = "NOTIFICATION_GET_MOVIES";
    public static final String NOTIFICATION_GET_TRAILERS = "NOTIFICATION_GET_TRAILERS";
    public static final String NOTIFICATION_GET_REVIEWS = "NOTIFICATION_GET_REVIEWS";

    private MovieDao movieDao;

    public MoviesService() {
        super("MoviesService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        movieDao = new MovieDao(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_MOVIES.equals(action)) {
                String sortBy = intent.getStringExtra(PARAM_SORT_BY);
                Boolean isLandScape = intent.getBooleanExtra(PARAM_LANDSCAPE, false);
                getMovies(sortBy, isLandScape);
            } else if(ACTION_GET_TRAILERS.equals(action)){
                Movie movie  = intent.getParcelableExtra(PARAM_MOVIE);
                addTrailers(movie);
            } else if(ACTION_GET_REVIEWS.equals(action)){
                Movie movie  = intent.getParcelableExtra(PARAM_MOVIE);
                addReviews(movie);
            }
        }
    }

    /**
     * Creates a video library which encapsulates a list of movies.
     * @param sortBy indicates the types of movies to request.
     * @param isLandScape is used to determine the number of movies to be rendered.
     * @see {@link TypeOfMovie#sortBy}.
     */
    private void getMovies(String sortBy, Boolean isLandScape) {

        ArrayList<Movie> listOfMovies;

        if(TypeOfMovie.FAVORITE.getSortBy().equals(sortBy)){
            listOfMovies = getListOfMoviesLocal();
        }else{
            listOfMovies = getListOfMovies(sortBy);
            if(isLandScape){
                listOfMovies = getRightNumberOfMovies(listOfMovies);
            }
        }

        VideoLibrary videoLibrary = new VideoLibrary();
        videoLibrary.setMovies(listOfMovies);

        publishResults(videoLibrary);
    }

    private void publishResults(VideoLibrary videoLibrary) {
        Intent intent = new Intent();
        intent.putExtra(PARAM_VIDEO_LIBRARY, videoLibrary);
        intent.setAction(NOTIFICATION_GET_MOVIES);
        sendBroadcast(intent);
    }

    /**
     * Returns favorite movies persisted locally
     * @return ArrayList<Movie>
     */
    private ArrayList<Movie> getListOfMoviesLocal() {
        return movieDao.getMovies();
    }

    /**
     * Returns a list of movies then a request was done.
     * @param sortBy indicates the types of movies to request.
     * @return ArrayList<Movie>.
     */
    private ArrayList<Movie> getListOfMovies(String sortBy) {
        String url = createListMovieUrl(sortBy);
        try{
            return MovieParser.parseMoviesJson(getData(url));
        }catch (JSONException je) {
            Log.e(LOG_TAG, "Error ", je);
        }
        return null;
    }

    /**
     * Adds the corresponding trailers to the movie.
     * @param movie the object that's going to contain the list of trailers.
     */
    private void addTrailers(Movie movie) {
        final String VIDEOS = "videos";
        String url = createMovieAdditionalDataUrl(String.valueOf(movie.getId()), VIDEOS);
        try{
            ArrayList<Trailer> trailers = MovieParser.parseTrailersJson(getData(url));
            movie.setTrailers(trailers);
            publishTrailersResults(movie);
        }catch (JSONException je) {
            Log.e(LOG_TAG, "Error ", je);
        }
    }

    private void publishTrailersResults(Movie movie) {
        Intent intent = new Intent();
        intent.putExtra(PARAM_MOVIE, movie);
        intent.setAction(NOTIFICATION_GET_TRAILERS);
        sendBroadcast(intent);
    }

    /**
     * Adds the corresponding reviews to the movie.
     * @param movie the object that's going to contain the list of reviews.
     */
    private void addReviews(Movie movie) {
        final String REVIEWS = "reviews";
        String url = createMovieAdditionalDataUrl(String.valueOf(movie.getId()), REVIEWS);
        try{
            ArrayList<Review> reviews = MovieParser.parseReviewsJson(getData(url));
            movie.setReviews(reviews);
            publishReviewsResults(movie);
        }catch (JSONException je) {
            Log.e(LOG_TAG, "Error ", je);
        }
    }

    private void publishReviewsResults(Movie movie) {
        Intent intent = new Intent();
        intent.putExtra(PARAM_MOVIE, movie);
        intent.setAction(NOTIFICATION_GET_REVIEWS);
        sendBroadcast(intent);
    }

    /**
     * Returns the data asked by a request.
     * @param url the address where the data is requested.
     * @return String contains the streamed information.
     */
    private String getData(String url){

        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;

        try {
            //get connection
            urlConnection = connect(url);

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            return buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    /**
     * Create an url connection.
     * @param path url address.
     * @return HttpURLConnection
     * @throws IOException if the address is wrong or there is not internet connection.
     */
    private HttpURLConnection connect(String path) throws IOException {

        URL url = new URL(path);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        return urlConnection;
    }

    /**
     * Create an url address to request a list of movies.
     * @param sortBy type of movies to retrieve.
     * @return String url created.
     */
    private String createListMovieUrl(String sortBy) {

        final String apiKey = getResources().getString(R.string.api_key);
        final String imageSize = "w185";

        final String MOVIES_PATH_URL = "http://api.themoviedb.org/3/discover/movie";
        final String SIZE_IMAGE_PARAM = "q";
        final String API_KEY = "api_key";
        final String SORT_BY = "sort_by";

        Uri builtUri = Uri.parse(MOVIES_PATH_URL).buildUpon()
                .appendQueryParameter(SIZE_IMAGE_PARAM, imageSize)
                .appendQueryParameter(SORT_BY, sortBy)
                .appendQueryParameter(API_KEY, apiKey)
                .build();

        return builtUri.toString();
    }

    /**
     * Create an url address in order to request some movie's additional data.
     * @param movieId id from one movie.
     * @param path could be <i>trailers</i> or <i>reviews</i>
     * @return String url created.
     */
    private String createMovieAdditionalDataUrl(String movieId, String path) {

        String apiKey = getResources().getString(R.string.api_key);

        final String MOVIES_PATH_URL = "http://api.themoviedb.org/3/movie";
        final String API_KEY = "api_key";

        Uri builtUri = Uri.parse(MOVIES_PATH_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(path)
                .appendQueryParameter(API_KEY, apiKey)
                .build();

        return builtUri.toString();
    }

    /**
     * Returns the count of movies to be rendered. For example, if the display is in landscape,
     * so the number of movies to be shown is three by rows, not two like the case of portrait.
     * The idea of this method is to avoid blank views in the grid list of movies.
     * @param movies movies to be rendered.
     * @return ArrayList<Movie>.
     */
    private ArrayList<Movie> getRightNumberOfMovies(ArrayList<Movie> movies){

        ArrayList<Movie> newListMovie = new ArrayList<>();

        for(int i = 0; i < getModule(movies); i++){
            newListMovie.add(movies.get(i));
        }

        return newListMovie;
    }

    private int getModule(ArrayList<Movie> movies){
        for(int i = movies.size(); i > 0; i--){
            if(i % 3 == 0){
                return i;
            }
        }
        return 0;
    }
}


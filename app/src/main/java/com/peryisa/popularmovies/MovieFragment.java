package com.peryisa.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import adapters.MovieAdapter;
import connections.Connection;
import data.MovieDao;
import model.Movie;
import model.Review;

/**
 * @author igiagante, on 9/9/15.
 */
public class MovieFragment extends Fragment {

    private TextView mTitleTextView;
    private TextView mYearTextView;
    private TextView mAverageTextView;
    private TextView mDescriptionTextView;
    private ImageView mPoster;
    private Button mReviewButton;
    private ToggleButton mMovieFavoriteButton;
    private FavoriteMovieListener favoriteMovieListener;

    private boolean buttonFavoritePressed = false;
    private boolean favoriteMovieListActive = false;
    private Movie mMovie;

    private MovieDao movieDao;

    public MovieFragment() {
    }

    /**
     * Actions that should be implemented to listen favorite movies' events.
     */
    public interface FavoriteMovieListener {
        void deleteFavoriteMovie(long movieId);

        void setDefaultMovie(Movie movie);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View containerView = inflater.inflate(R.layout.fragment_movie, container, false);

        if (savedInstanceState != null) {
            mMovie = savedInstanceState.getParcelable("movie");
        }

        mTitleTextView = (TextView) containerView.findViewById(R.id.movie_title);
        mYearTextView = (TextView) containerView.findViewById(R.id.movie_year);
        mAverageTextView = (TextView) containerView.findViewById(R.id.movie_average);
        mDescriptionTextView = (TextView) containerView.findViewById(R.id.movie_description);
        mPoster = (ImageView) containerView.findViewById(R.id.movie_image);

        mMovieFavoriteButton = (ToggleButton) containerView.findViewById(R.id.movie_button);
        mMovieFavoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                buttonFavoritePressed = true;

                if (isChecked) {
                    mMovie.setFavorite(true);
                } else {
                    mMovie.setFavorite(false);
                    //this implementation is for display bigger than 550
                    if (getSmallWithDisplay() > 550 && favoriteMovieListActive) {
                        favoriteMovieListener.deleteFavoriteMovie(mMovie.getId());
                    }
                }
            }
        });

        mMovieFavoriteButton.setEnabled(false);

        mReviewButton = (Button) containerView.findViewById(R.id.review_button);
        mReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReviewActivity();
            }
        });
        mReviewButton.setEnabled(false);

        movieDao = new MovieDao(getActivity());

        return containerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        movieDao = new MovieDao(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        updateFavoriteMovieData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("movie", mMovie);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (activity instanceof ListMovieActivity) {
                favoriteMovieListener = (FavoriteMovieListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FavoriteMovieListener");
        }
    }

    private void startReviewActivity() {
        if (Connection.checkInternet(getActivity())) {
            Intent intent = new Intent(getActivity(), ReviewActivity.class);
            intent.putExtra(MovieActivity.MOVIE_PARAM, mMovie);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "Internet is not available", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Initialize the movie view's components.
     *
     * @param movie object to be populated.
     */
    public void setMovie(Movie movie) {

        updateFavoriteMovieData();

        this.mMovie = movie;
        this.buttonFavoritePressed = false;

        String year = movie.getReleaseDate().split("-")[0];

        mTitleTextView.setText(movie.getTitle());
        mYearTextView.setText(year);
        mAverageTextView.setText(String.valueOf(movie.getVoteAverage()) + " / 10");

        String description = movie.getDescription().equals("") ? "No overview found." : movie.getDescription();

        mDescriptionTextView.setText(description);

        String url = MovieAdapter.PATH_URL_POSTER_FIRST_PART + movie.getPosterPath();

        Picasso.with(getActivity()).load(url)
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(mPoster);

        mMovieFavoriteButton.setEnabled(true);
        mReviewButton.setEnabled(true);

        //set favorite movie state (Mark of Unmark)
        mMovieFavoriteButton.setChecked(movieDao.existMovie(mMovie.getId()));
    }

    public void updateReviews(ArrayList<Review> reviews) {
        this.mMovie.setReviews(reviews);
    }

    /**
     * It should be called one time, not every time that an user clicks the button "Mark as Favorite"
     * because this could terminate in a overloading for the database.
     */
    private void updateFavoriteMovieData() {

        if (mMovie != null && buttonFavoritePressed) {
            if (mMovie.isFavorite()) {
                Log.d("Persist", "New movie was marked as favorite");
                movieDao.createMovie(mMovie);
            } else {
                Log.d("Delete", "One movie was unmarked as favorite");
                movieDao.deleteMovie(mMovie);
            }
        }
    }

    private float getSmallWithDisplay() {

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float scaleFactor = metrics.density;

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        return Math.min(widthDp, heightDp);
    }

    public void setFavoriteMovieListActive(boolean favoriteMovieListActive) {
        this.favoriteMovieListActive = favoriteMovieListActive;
    }

    public Movie getMovie(){
        return mMovie;
    }
}

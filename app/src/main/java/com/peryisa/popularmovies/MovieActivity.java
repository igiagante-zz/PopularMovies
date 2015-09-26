package com.peryisa.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import model.Movie;
import model.Review;
import services.MoviesService;

/**
 * @author igiagante, on 9/9/15.
 */
public class MovieActivity extends AppCompatActivity implements UpdateReview {

    private Movie mMovie;

    private MovieFragment mMovieFragment;
    private TrailerFragment mTrailerFragment;

    //Fragment tags
    static final String TAG_FRAGMENT_MOVIE = "fragment_movie";
    static final String TAG_FRAGMENT_TRAILER = "fragment_trailer";

    //Params
    public static final String MOVIE_PARAM = "MOVIE_PARAM";

    //Broadcast receivers
    private BroadcastReceiver mReceiverReview = new BroadcastReceiverReview(this);
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MoviesService.NOTIFICATION_GET_TRAILERS)) {
                Movie movie = intent.getParcelableExtra(MoviesService.PARAM_MOVIE);
                mTrailerFragment.setMovieTitle(movie.getTitle());
                mTrailerFragment.setTrailers(movie.getTrailers());
                mMovie.setTrailers(movie.getTrailers());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mMovieFragment = (MovieFragment) getFragmentManager().findFragmentById(R.id.movie_detail);
        mTrailerFragment = (TrailerFragment) getFragmentManager().findFragmentById(R.id.list_trailers);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            mMovie = bundle.getParcelable(MOVIE_PARAM);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(MoviesService.NOTIFICATION_GET_TRAILERS));
        registerReceiver(mReceiverReview, new IntentFilter(MoviesService.NOTIFICATION_GET_REVIEWS));
        mMovieFragment.setMovie(mMovie);
        mTrailerFragment.addTrailers(mMovie);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        unregisterReceiver(mReceiverReview);
    }

    @Override
    public void updateReviews(ArrayList<Review> reviews) {
        mMovieFragment.updateReviews(reviews);
    }
}

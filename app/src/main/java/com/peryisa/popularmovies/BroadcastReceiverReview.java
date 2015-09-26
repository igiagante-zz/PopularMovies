package com.peryisa.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import model.Movie;
import services.MoviesService;

/**
 * @author igiagante, on 9/9/15.
 */
public class BroadcastReceiverReview extends BroadcastReceiver {

    private UpdateReview mUpdateReview;

    public BroadcastReceiverReview(UpdateReview updateReview) {
        super();
        this.mUpdateReview = updateReview;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MoviesService.NOTIFICATION_GET_REVIEWS)) {
            Movie movie = intent.getParcelableExtra(MoviesService.PARAM_MOVIE);
            mUpdateReview.updateReviews(movie.getReviews());
        }
    }
}

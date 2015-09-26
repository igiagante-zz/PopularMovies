package com.peryisa.popularmovies;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import adapters.ReviewAdapter;
import connections.Connection;
import model.Movie;
import model.Review;
import services.MoviesService;

public class ReviewActivity extends AppCompatActivity implements UpdateReview {

    private Movie movie;
    private ReviewFragment mReviewFragment;

    private BroadcastReceiver mReceiver = new BroadcastReceiverReview(this);

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(MoviesService.NOTIFICATION_GET_REVIEWS));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            movie = bundle.getParcelable(MovieActivity.MOVIE_PARAM);
        }
        mReviewFragment = (ReviewFragment) getFragmentManager().findFragmentById(R.id.list_reviews);
        addReviews(movie);
    }

    @Override
    public void updateReviews(ArrayList<Review> reviews) {
        if(null != mReviewFragment){
            mReviewFragment.setReviews(reviews);
        }
    }

    public void addReviews(Movie movie){
        if (Connection.checkInternet(this)){
            Intent intent = new Intent(this, MoviesService.class);
            intent.putExtra(MoviesService.PARAM_MOVIE, movie);
            intent.setAction(MoviesService.ACTION_GET_REVIEWS);
            startService(intent);
        } else {
            Toast.makeText(this, "Internet is not available", Toast.LENGTH_LONG).show();
        }
    }

    public static class ReviewFragment extends Fragment {

        private ArrayList<Review> mReviews = new ArrayList<>();
        private ReviewAdapter mReviewAdapter;
        private ProgressBar mProgressBar;
        private RecyclerView mRecyclerView;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View containerView = inflater.inflate(R.layout.fragment_review, container, false);

            mRecyclerView = (RecyclerView) containerView.findViewById(R.id.recycler_view_reviews);
            mRecyclerView.setHasFixedSize(true);
            mProgressBar = (ProgressBar) containerView.findViewById(R.id.progress_bar_reviews);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            if( savedInstanceState != null ) {
                mReviews = savedInstanceState.getParcelableArrayList("reviews");
                mProgressBar.setVisibility(View.INVISIBLE);
            }else{
                mProgressBar.setVisibility(View.VISIBLE);
            }

            mReviewAdapter = new ReviewAdapter(mReviews);
            mRecyclerView.setAdapter(mReviewAdapter);

            return containerView;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelableArrayList("reviews", mReviews);
        }

        public void setReviews(ArrayList<Review> reviews){
            mReviewAdapter.setReviews(reviews);
            TextView noReviews = (TextView) getActivity().findViewById(R.id.review_no_reviews_for_movie);

            if(reviews.isEmpty()){
                noReviews.setVisibility(View.VISIBLE);
            }
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}

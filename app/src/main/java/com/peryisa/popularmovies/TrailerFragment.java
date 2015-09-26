package com.peryisa.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import adapters.TrailerAdapter;
import connections.Connection;
import model.Movie;
import model.Trailer;
import services.MoviesService;

/**
 * Created by igiagante on 14/9/15.
 */
public class TrailerFragment extends Fragment {

    private ArrayList<Trailer> mTrailers = new ArrayList<>();
    private TrailerAdapter mTrailerAdapter;
    private ProgressBar mProgressBar;

    private String movieTitle;
    private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View containerView = inflater.inflate(R.layout.fragment_trailer, container, false);

        RecyclerView mRecyclerView = (RecyclerView) containerView.findViewById(R.id.recycler_view_trailers);
        mRecyclerView.setHasFixedSize(true);
        mProgressBar = (ProgressBar) containerView.findViewById(R.id.progress_bar_trailers);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (savedInstanceState != null) {
            mTrailers = savedInstanceState.getParcelableArrayList("trailers");
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        mTrailerAdapter = new TrailerAdapter(mTrailers, getActivity());
        mRecyclerView.setAdapter(mTrailerAdapter);

        return containerView;
    }

    /**
     * Starts MovieSerive asking to add the trailers from one movie.
     *
     * @param movie
     */
    public void addTrailers(Movie movie) {
        if (Connection.checkInternet(getActivity())) {
            Intent intent = new Intent(getActivity(), MoviesService.class);
            intent.putExtra(MoviesService.PARAM_MOVIE, movie);
            intent.setAction(MoviesService.ACTION_GET_TRAILERS);
            getActivity().startService(intent);
        } else {
            Toast.makeText(getActivity(), "Internet is not available", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("trailers", mTrailers);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate menu resource file.
        inflater.inflate(R.menu.menu_movie, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = new ShareActionProvider(getActivity());
        MenuItemCompat.setActionProvider(menuItem, mShareActionProvider);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setupShareTrailer(String subject, String trailerKey) {

        final String YOUTUBE = "http://www.youtube.com/watch?v=";

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, YOUTUBE + trailerKey);
        shareIntent.setType("text/plain");

        if (null != mShareActionProvider) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        ImageView noTrailers = (ImageView) getActivity().findViewById(R.id.trailer_no_trailers);
        if (!trailers.isEmpty()) {
            String trailerKey = trailers.get(0).getKey();
            setupShareTrailer(movieTitle, trailerKey);
            noTrailers.setVisibility(View.GONE);
        } else {
            noTrailers.setVisibility(View.VISIBLE);
        }
        mTrailerAdapter.setTrailers(trailers);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
}

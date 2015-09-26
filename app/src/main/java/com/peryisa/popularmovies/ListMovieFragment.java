package com.peryisa.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import adapters.MovieAdapter;
import connections.Connection;
import enums.TypeOfMovie;
import model.Movie;
import services.MoviesService;

/**+
 * @author igiagante, on 9/9/15.
 */
public class ListMovieFragment extends Fragment {

    private ArrayList<Movie> mMovies = new ArrayList<>();

    private MovieAdapter mAdapter;
    private ProgressBar mProgressBar;

    private MovieFragment mMovieFragment;
    private MovieFragment.FavoriteMovieListener favoriteMovieListener;
    private ActiveFavoriteMovie activeFavoriteMovie;

    public ListMovieFragment() {
    }

    /**
     * Used to notify whether the list of favorite movies is actived or not.
     */
    public interface ActiveFavoriteMovie{
        /**
         * Set the type of list of movies (popularity, rating, favorites)
         * @param typeOfList
         * @see TypeOfMovie
         */
        void setActiveFavoriteMovie(String typeOfList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View containerView = inflater.inflate(R.layout.fragment_list_movie, container, false);

        RecyclerView mRecyclerView = (RecyclerView) containerView.findViewById(R.id.recycler_view_movies);
        mRecyclerView.setHasFixedSize(true);

        //Two columns for portrait
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);

        //Three columns for landscape
        if(isLandScape()){
            manager = new GridLayoutManager(getActivity(), 3);
        }

        mRecyclerView.setLayoutManager(manager);
        mProgressBar = (ProgressBar) containerView.findViewById(R.id.progress_bar_movies);

        if( savedInstanceState != null ) {
            mMovies = savedInstanceState.getParcelableArrayList("movies");
            mProgressBar.setVisibility(View.INVISIBLE);
        }else{
            mProgressBar.setVisibility(View.VISIBLE);
        }

        Activity activity = getActivity();
        if(activity instanceof MovieAdapter.OnMovieSelectedListener){
            mAdapter = new MovieAdapter(mMovies, getActivity(), (MovieAdapter.OnMovieSelectedListener) activity);
        }else{
            mAdapter = new MovieAdapter(mMovies, getActivity(), null);
        }

        mRecyclerView.setAdapter(mAdapter);

        if(null != getArguments() && getArguments().getBoolean(ListMovieActivity.TWO_PANEL)){
            selectFirstMovie();
        }

        return containerView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void selectFirstMovie(){
        mMovieFragment = (MovieFragment) getFragmentManager().findFragmentByTag(MovieActivity.TAG_FRAGMENT_MOVIE);
        if(null != mMovieFragment){
            mMovieFragment.setMovie(mAdapter.getMovie(0));
        }
    }

    /**
     * Refresh the list of movies considering the settings defined.
     */
    private void updateMovies(){

        final String DESC = ".desc";

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sort_movies_by_key),
                getString(R.string.pref_sort_movies_by_default_value));

        if(!TypeOfMovie.FAVORITE.getSortBy().equals(sortBy)){
            sortBy = sortBy + DESC;
        }
        activeFavoriteMovie.setActiveFavoriteMovie(sortBy);

        if(Connection.checkInternet(getActivity())){
            Intent intentService = new Intent(getActivity(), MoviesService.class);
            intentService.setAction(MoviesService.ACTION_GET_MOVIES);
            intentService.putExtra(MoviesService.PARAM_SORT_BY, sortBy);

            //adds boolean param to notify the service that display is in landscape. This param is used
            // to determinate the number of movies to be rendered.
            if(isLandScape()){
                intentService.putExtra(MoviesService.PARAM_LANDSCAPE, isLandScape());
            }
            getActivity().startService(intentService);
        } else {
            Toast.makeText(getActivity(), "Internet is not available", Toast.LENGTH_LONG).show();
        }

        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", mMovies);
    }

    /**
     * Sets the movies into the adapter object.
     * @param movies list of movies.
     */
    public void setMovies(ArrayList<Movie> movies){
        mAdapter.setMovies(movies);
    }

    /**
     * Used when the list of movies is the favorite list created by the user.
     * @param movieId id from the object movie.
     */
    public void deleteMovieFromFavoriteList(long movieId){
        Movie movie = mAdapter.deleteMovie(movieId);
        if(movie != null){
            //The movie was deleted. So, it needs other movie to set
            //the default movie which is display in movie detail fragment.
            favoriteMovieListener.setDefaultMovie(getRandomMovie());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            favoriteMovieListener = (MovieFragment.FavoriteMovieListener) activity;
            activeFavoriteMovie = (ActiveFavoriteMovie) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FavoriteMovieListener");
        }
    }

    private boolean isLandScape(){
        return getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private Movie getRandomMovie(){
        return mAdapter.getRandomMovie();
    }
}

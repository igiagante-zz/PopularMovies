package com.peryisa.popularmovies;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import adapters.MovieAdapter;
import connections.Connection;
import enums.TypeOfMovie;
import model.Movie;
import model.VideoLibrary;
import services.MoviesService;

/**
 * @author igiagante, on 9/9/15.
 */
public class ListMovieActivity extends AppCompatActivity implements MovieAdapter.OnMovieSelectedListener,
        MovieFragment.FavoriteMovieListener, ListMovieFragment.ActiveFavoriteMovie {

    private MovieFragment mMovieFragment;
    private TrailerFragment mTrailerFragment;
    private ListMovieFragment mListMovieFragment;

    //use to indicate if two fragments were added.
    private boolean mTwoPane;
    public static final String TWO_PANEL = "TWO_PANEL";

    private Movie mDefaultMovie;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MoviesService.NOTIFICATION_GET_MOVIES)) {
                //Get the video library with the movies
                VideoLibrary videoLibrary = intent.getParcelableExtra(MoviesService.PARAM_VIDEO_LIBRARY);

                //Inflate the listMovieFragment
                mListMovieFragment = (ListMovieFragment) getFragmentManager().findFragmentById(R.id.list_movies);

                if (null != videoLibrary && null != mListMovieFragment) {

                    mListMovieFragment.setMovies(videoLibrary.getMovies());

                    //Inflate MovieFragment and TrailerFragment
                    mMovieFragment = (MovieFragment) getFragmentManager().findFragmentByTag(MovieActivity.TAG_FRAGMENT_MOVIE);
                    mTrailerFragment = (TrailerFragment) getFragmentManager().findFragmentByTag(MovieActivity.TAG_FRAGMENT_TRAILER);

                    if (null != mMovieFragment && null != mTrailerFragment) {
                        //if one movie was selected and button reviews was clicked. Then, when the flow come back to
                        //ListMovieActivity it should be selected the same movie as before
                        mDefaultMovie = mMovieFragment.getMovie();
                        if (null == mDefaultMovie) {
                            //if these fragments were inflated, the first movie is going to be set like default.
                            mDefaultMovie = videoLibrary.getMovies().get(0);
                        }
                        //set movie
                        mMovieFragment.setMovie(mDefaultMovie);
                        //add trailers
                        mTrailerFragment.addTrailers(mDefaultMovie);
                    }
                }
            }
            //One the trailers were requested and added
            if (intent.getAction().equals(MoviesService.NOTIFICATION_GET_TRAILERS)) {
                Movie movie = intent.getParcelableExtra(MoviesService.PARAM_MOVIE);
                mTrailerFragment.setMovieTitle(movie.getTitle());
                mTrailerFragment.setTrailers(movie.getTrailers());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_movie);

        if (null != findViewById(R.id.movie_detail_container)) {

            mTwoPane = true;

            if (null == savedInstanceState) {

                //In two-pane the detail movie fragment is added by a fragment transaction.
                mMovieFragment = new MovieFragment();
                FragmentTransaction fragmentMovieTransaction = getFragmentManager().beginTransaction();

                fragmentMovieTransaction.replace(R.id.movie_detail_container,
                        mMovieFragment, MovieActivity.TAG_FRAGMENT_MOVIE);
                fragmentMovieTransaction.addToBackStack(null);
                fragmentMovieTransaction.commit();

                //In two-pane the trailer fragment is added by a fragment transaction.
                FragmentTransaction fragmentTrailerTransaction = getFragmentManager().beginTransaction();
                mTrailerFragment = new TrailerFragment();

                fragmentTrailerTransaction.replace(R.id.movie_trailers_container,
                        mTrailerFragment, MovieActivity.TAG_FRAGMENT_TRAILER);
                fragmentTrailerTransaction.addToBackStack(null);
                fragmentTrailerTransaction.commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MoviesService.NOTIFICATION_GET_MOVIES);
        filter.addAction(MoviesService.NOTIFICATION_GET_TRAILERS);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void movieSelected(Movie movie) {

        if (mTwoPane) {
            mMovieFragment = (MovieFragment) getFragmentManager().findFragmentByTag(MovieActivity.TAG_FRAGMENT_MOVIE);
            mTrailerFragment = (TrailerFragment) getFragmentManager().findFragmentByTag(MovieActivity.TAG_FRAGMENT_TRAILER);
            if (null != mMovieFragment) {
                //set movie
                mMovieFragment.setMovie(movie);
                //add trailers
                mTrailerFragment.addTrailers(movie);
            }
        } else if (Connection.checkInternet(this)) {
            Intent intent = new Intent(this, MovieActivity.class);
            intent.putExtra(MovieActivity.MOVIE_PARAM, movie);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Internet is not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setActiveFavoriteMovie(String typeOfList) {
        if (mMovieFragment != null) {
            if (TypeOfMovie.FAVORITE.getSortBy().equals(typeOfList)) {
                mMovieFragment.setFavoriteMovieListActive(true);
            } else {
                mMovieFragment.setFavoriteMovieListActive(false);
            }
        }
    }

    @Override
    public void deleteFavoriteMovie(long movieId) {
        mListMovieFragment.deleteMovieFromFavoriteList(movieId);
    }

    @Override
    public void setDefaultMovie(Movie movie) {
        if (null != movie) {
            mMovieFragment.setMovie(movie);
        } else {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.remove(mTrailerFragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

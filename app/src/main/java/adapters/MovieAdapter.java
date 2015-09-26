package adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.peryisa.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import model.Movie;

/**
 * Creatse a view adapter for Movie RecycleView.
 * @author igiagante, on 3/9/15.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ItemViewHolder> {

    public static final String PATH_URL_POSTER_FIRST_PART = "http://image.tmdb.org/t/p/w185/";

    private Context mContext;

    private ArrayList<Movie> movies;
    private OnMovieSelectedListener onMovieSelectedListener;

    /**
     * Creates a movie adapter.
     *
     * @param movies                  provides the data for the new view.
     * @param context                 activity context.
     * @param onMovieSelectedListener object that implements an action when a movie is selected.
     */
    public MovieAdapter(ArrayList<Movie> movies, Context context, OnMovieSelectedListener onMovieSelectedListener) {
        this.movies = movies;
        this.mContext = context;
        this.onMovieSelectedListener = onMovieSelectedListener;
    }

    // inner class to hold a reference to each card_view of RecyclerView
    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView imageView;

        public ItemViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view);
            imageView = (ImageView) v.findViewById(R.id.item_image);

            //if one movie card is clicked, then movie detail should be shown.
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Movie movie = movies.get(getAdapterPosition());
                    onMovieSelectedListener.movieSelected(movie);
                }
            });
        }
    }

    /**
     * Actions for one movie selected.
     */
    public interface OnMovieSelectedListener {
        void movieSelected(Movie movie);
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        //inflate the card view and build the ItemViewHolder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_movie_view, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int position) {

        String url = PATH_URL_POSTER_FIRST_PART + movies.get(position).getPosterPath();

        Picasso.with(mContext).load(url)
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(itemViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public Movie getMovie(int position) {
        return movies.get(position);
    }

    /**
     * Used when it was loaded the favorites from local. It allows to delete a movie from the list
     * when the user decided to unmark the movie as favorite.
     * @param movieId id from movie object.
     * @return movie the movie that was deleted.
     */
    public Movie deleteMovie(long movieId) {

        Movie movie;
        int position = -1;

        for (Movie movieItem : movies) {
            if (movieItem.getId() == movieId) {
                position = movies.indexOf(movieItem);
            }
        }

        if (position != -1) {
            movie = movies.get(position);
            this.movies.remove(position);
            notifyDataSetChanged();
            return movie;
        }
        return null;
    }

    /**
     * Returns a random movie from the list of movies.
     *
     * @return Movie
     */
    public Movie getRandomMovie() {

        Random rand = new Random();
        int randomNum = rand.nextInt(movies.size());

        if (!movies.isEmpty()) {
            return movies.get(randomNum);
        } else {
            return new Movie();
        }
    }
}

package adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.peryisa.popularmovies.R;

import java.util.ArrayList;

import model.Review;

/**
 * Creates a view adapter for Review RecycleView.
 * @author igiagante, on 14/9/15.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ItemViewHolder> {

    private ArrayList<Review> mReviews;

    public ReviewAdapter(ArrayList<Review> reviews) {
        this.mReviews = reviews;
    }

    // inner class to hold a reference to each card_view of RecyclerView
    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView textViewAuthor;
        public TextView textViewDesc;

        public ItemViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_review_view);
            textViewAuthor = (TextView) v.findViewById(R.id.review_author);
            textViewDesc = (TextView) v.findViewById(R.id.review_description);
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        //inflate the card view and build the ItemViewHolder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_review_view, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int position) {
        itemViewHolder.textViewAuthor.setText(mReviews.get(position).getAuthor());
        itemViewHolder.textViewDesc.setText(mReviews.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews == null ? 0 : mReviews.size();
    }

    public ArrayList<Review> getReviews() {
        return mReviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.mReviews = reviews;
        notifyDataSetChanged();
    }
}


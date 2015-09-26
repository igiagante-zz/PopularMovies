package adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.peryisa.popularmovies.R;

import java.util.ArrayList;

import connections.Connection;
import model.Trailer;

/**
 * Creates a view adapter for Trailer RecycleView.
 * @author igiagante, on 3/9/15.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<Trailer> mTrailers;

    public TrailerAdapter(ArrayList<Trailer> mTrailers, Context context) {
        this.mTrailers = mTrailers;
        this.mContext = context;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView trailerName;
        public ImageView imageView;

        public ItemViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.icon_play);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playTrailer(getAdapterPosition());
                }
            });
            trailerName = (TextView) v.findViewById(R.id.trailer_name);
        }
    }

    /**
     * Starts an activity that can play the trailer.
     * @param position indicates the position of the trailer inside of the trailer's list.
     */
    private void playTrailer(int position) {
        if (Connection.checkInternet(mContext)) {
            final String YOUTUBE = "vnd.youtube://";
            String key = mTrailers.get(position).getKey();
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE + key)));
        } else {
            Toast.makeText(mContext, "Internet is not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_row_view, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int position) {
        itemViewHolder.trailerName.setText(mTrailers.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mTrailers == null ? 0 : mTrailers.size();
    }

    public void setTrailers(ArrayList<Trailer> mTrailers) {
        this.mTrailers = mTrailers;
        notifyDataSetChanged();
    }
}

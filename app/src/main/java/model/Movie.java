package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * This class represents the movie model.
 * @author igiagante, on 3/9/15.
 */
public class Movie  implements Parcelable {

    public Movie(){}

    private long id;
    private String title;
    private String description;
    private String posterPath;
    private String duration;
    private float voteAverage;
    private String releaseDate;
    private boolean favorite;
    private ArrayList<Trailer> trailers = new ArrayList<>();
    private ArrayList<Review> reviews = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(posterPath);
        dest.writeString(duration);
        dest.writeFloat(voteAverage);
        dest.writeString(releaseDate);
        dest.writeInt(favorite ? 1 : 0);
        dest.writeList(trailers);
        dest.writeList(reviews);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        id = in.readLong();
        title = in.readString();
        description = in.readString();
        posterPath = in.readString();
        duration = in.readString();
        voteAverage = in.readFloat();
        releaseDate = in.readString();
        favorite = in.readInt() == 1;
        in.readList(trailers, this.getClass().getClassLoader());
        in.readList(reviews, this.getClass().getClassLoader());
    }
}

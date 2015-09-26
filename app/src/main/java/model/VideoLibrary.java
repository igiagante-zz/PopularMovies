package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * This class basically represents a wrapper for a ArrayList of movies,
 * which allows to transfer data more easily between different Intents.
 * @author igiagante, on 9/9/15.
 */
public class VideoLibrary implements Parcelable {

    private ArrayList<Movie> movies = new ArrayList<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(movies);
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public static final Parcelable.Creator<VideoLibrary> CREATOR = new Parcelable.Creator<VideoLibrary>() {
        public VideoLibrary createFromParcel(Parcel in) {
            return new VideoLibrary(in);
        }

        public VideoLibrary[] newArray(int size) {
            return new VideoLibrary[size];
        }
    };

    private VideoLibrary(Parcel in) {
        in.readList(movies, this.getClass().getClassLoader());
    }

    public VideoLibrary() {
    }
}

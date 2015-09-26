package model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents the review model.
 * @author igiagante, on 3/9/15.
 */
public class Review implements Parcelable{

    private long id;
    private String reviewId;
    private String author;
    private String content;

    public Review(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(reviewId);
        dest.writeString(author);
        dest.writeString(content);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>(){
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    private Review(Parcel in){
        id = in.readLong();
        reviewId = in.readString();
        author = in.readString();
        content = in.readString();
    }
}

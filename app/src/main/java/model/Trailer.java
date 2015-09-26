package model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents the trailer model.
 * @author igiagante, on 3/9/15.
 */
public class Trailer implements Parcelable{

    private long id;
    private String trailerId;
    private String name;
    private String key;

    public Trailer(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(trailerId);
        dest.writeString(name);
        dest.writeString(key);
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>(){
        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    private Trailer(Parcel in){
        id = in.readLong();
        trailerId = in.readString();
        name = in.readString();
        key = in.readString();
    }
}

package data;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Afzal on 6/17/15.
 */
public class CustomArtist implements Parcelable {

    public String mArtistID;
    public String mArtistName;
    public String mArtistImage;

    public CustomArtist(Artist artist){
        mArtistID = artist.id;
        mArtistName = artist.name;
        if(artist.images.size() != 0)
            mArtistImage = (artist.images.get(artist.images.size() -1)).url;
        else
            mArtistImage = null;
    }


    protected CustomArtist(Parcel in) {
        mArtistID = in.readString();
        mArtistName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mArtistID);
        dest.writeString(mArtistName);
    }

    public void readFromParcel(Parcel in){
       mArtistID = in.readString();
        mArtistName = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CustomArtist> CREATOR = new Parcelable.Creator<CustomArtist>() {
        @Override
        public CustomArtist createFromParcel(Parcel in) {
            return new CustomArtist(in);
        }

        @Override
        public CustomArtist[] newArray(int size) {
            return new CustomArtist[size];
        }
    };
}
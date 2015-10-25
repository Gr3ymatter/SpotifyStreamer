package data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Afzal on 6/18/15.
 */
public class CustomTrack implements Parcelable, Serializable {

    public String mAlbumName;
    public String mSongName;
    public String mArtistName;
    public String mAlbumImage_small;
    public String mAlbumImage_large;
    public String mTrackPreview;


    public CustomTrack(Track track){
        mAlbumName = track.album.name;
        mSongName = track.name;
        mArtistName = track.artists.get(0).name;
        mTrackPreview = track.preview_url;
        mAlbumImage_large = track.album.images.get(0).url;
        mAlbumImage_small = track.album.images.get(track.album.images.size()-1).url;
    }

    protected CustomTrack(Parcel in) {
        mAlbumName = in.readString();
        mSongName = in.readString();
        mAlbumImage_large = in.readString();
        mAlbumImage_small = in.readString();
        mTrackPreview = in.readString();
        mArtistName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAlbumName);
        dest.writeString(mSongName);
        dest.writeString(mAlbumImage_large);
        dest.writeString(mAlbumImage_small);
        dest.writeString(mTrackPreview);
        dest.writeString(mArtistName);

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CustomTrack> CREATOR = new Parcelable.Creator<CustomTrack>() {
        @Override
        public CustomTrack createFromParcel(Parcel in) {
            return new CustomTrack(in);
        }

        @Override
        public CustomTrack[] newArray(int size) {
            return new CustomTrack[size];
        }
    };
}
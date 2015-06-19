package data;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Afzal on 6/18/15.
 */

public class CustomTrack implements Parcelable {

        public String mAlbumName;
        public String mSongName;
        public String mAlbumImage;


        public CustomTrack(Track track){
            mAlbumName = track.album.name;
            mSongName = track.name;
            mAlbumImage = track.album.images.get(track.album.images.size() -1).url;
        }




        protected CustomTrack(Parcel in) {
            mAlbumName = in.readString();
            mSongName = in.readString();
            mAlbumImage = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mAlbumName);
            dest.writeString(mSongName);
            dest.writeString(mAlbumImage);
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




package com.gr3ymatter.spotifystreamer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Afzal on 6/13/15.
 */
public class ArtistTrackAdapter extends ArrayAdapter<Track> {


    Activity mContext;

    private static class ViewHolder{
        TextView trackName;
        TextView albumName;
        ImageView albumArt;
    }

    public ArtistTrackAdapter(Activity context, int resource){
        super(context, resource);
        mContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if(rowView == null){

            LayoutInflater inflater = mContext.getLayoutInflater();

            rowView = inflater.inflate(R.layout.list_item_track, parent, false);

            ViewHolder holder = new ViewHolder();

            holder.albumArt = (ImageView)rowView.findViewById(R.id.list_item_track_imageview);
            holder.albumName = (TextView)rowView.findViewById(R.id.list_item_album_textview);
            holder.trackName = (TextView)rowView.findViewById(R.id.list_item_track_textview);

            rowView.setTag(holder);

        }

        ViewHolder holder = (ViewHolder)rowView.getTag();
        if(getItem(position).album.images.size() != 0)
        {
            String albumArtUrl = getItem(position).album.images.get(getItem(position).album.images.size()-1).url;
            Picasso.with(mContext).load(albumArtUrl).into(holder.albumArt);
        }

        holder.trackName.setText(getItem(position).name);
        holder.albumName.setText(getItem(position).album.name);

        return rowView;
    }
}

package com.gr3ymatter.spotifystreamer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Afzal on 6/13/15.
 */
public class ArtistListAdapter extends ArrayAdapter<Artist> {

    static class ViewHolder{
        ImageView artistImage;
        TextView artistName;
    }

    Activity mContext;

    public ArtistListAdapter(Activity context, int resource) {
        super(context, resource);
        mContext = context;
    }


    //Instead of doing it this way, should i be using Volley? NetworkImageView's etc?
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if(rowView == null){
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_artist, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.artistName = (TextView)rowView.findViewById(R.id.list_item_artist_textview);
            viewHolder.artistImage = (ImageView)rowView.findViewById(R.id.list_item_artist_imageview);

            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.artistName.setText(this.getItem(position).name);

        if(getItem(position).images.size() != 0)
            Picasso.with(mContext).load(getItem(position).images.get(getItem(position).images.size() - 1).url).into(holder.artistImage);
        return rowView;
    }
}

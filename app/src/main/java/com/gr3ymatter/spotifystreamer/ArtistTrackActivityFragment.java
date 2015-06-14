package com.gr3ymatter.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistTrackActivityFragment extends Fragment {

    ArtistTrackAdapter mArtistTrackAdapter;
    ListView mArtistTrackListView;

    public ArtistTrackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_track, container, false);
        String artistID = getActivity().getIntent().getStringExtra(MainActivityFragment.ARTIST_ID);


        mArtistTrackListView = (ListView)rootView.findViewById(R.id.listview_artist_track);
        mArtistTrackAdapter = new ArtistTrackAdapter(getActivity(),R.layout.list_item_track);

        mArtistTrackListView.setAdapter(mArtistTrackAdapter);


        Log.d("ARTIST_ID", artistID);
        new FetchTrackData().execute(artistID);



        return rootView;
    }


    private class FetchTrackData extends AsyncTask<String, Void, List<Track>>{
        @Override
        protected List<Track> doInBackground(String... params) {

            SpotifyService spotifyService = new SpotifyApi().getService();

            HashMap<String, Object> locationQuery;
            locationQuery = new HashMap<String, Object>();

            locationQuery.put("country","US");

            Tracks tracks = spotifyService.getArtistTopTrack(params[0],locationQuery);


            return tracks.tracks;

        }

        @Override
        protected void onPostExecute(List<Track> tracks) {

            mArtistTrackAdapter.addAll(tracks);
        }
    }

}

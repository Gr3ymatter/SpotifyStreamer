package com.gr3ymatter.spotifystreamer;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ViewSwitcher;

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
    String artistID;
    ViewSwitcher mViewSwitcher;
    SharedPreferences pref;
    public ArtistTrackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_track, container, false);
        artistID = getActivity().getIntent().getStringExtra(MainActivityFragment.ARTIST_ID);

        mViewSwitcher = (ViewSwitcher)rootView.findViewById(R.id.viewswitcher);


        mArtistTrackListView = (ListView)rootView.findViewById(R.id.listview_artist_track);
        mArtistTrackAdapter = new ArtistTrackAdapter(getActivity(),R.layout.list_item_track);

        mArtistTrackListView.setAdapter(mArtistTrackAdapter);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = pref.getString(getString(R.string.pref_country_preference_key),getString(R.string.pref_country_preference_default));
        Log.d("ARTIST_ID", artistID);
        new FetchTrackData().execute(artistID, location);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private class FetchTrackData extends AsyncTask<String, Void, List<Track>>{
        @Override
        protected List<Track> doInBackground(String... params) {

            SpotifyService spotifyService = new SpotifyApi().getService();

            HashMap<String, Object> locationQuery;
            locationQuery = new HashMap<String, Object>();

            locationQuery.put("country", params[1]);

            Tracks tracks = spotifyService.getArtistTopTrack(params[0],locationQuery);


            return tracks.tracks;

        }



        @Override
        protected void onPostExecute(List<Track> tracks) {

            if(tracks == null || tracks.size() == 0){
                //Do something here... show that there are no results
                if(mViewSwitcher.getCurrentView().equals(mArtistTrackListView))
                    mViewSwitcher.showNext();
            }
            else
            {
                //If the current view is not the ArtistListView then display the ArtistListView
                //as we have artists to display
                if(!mViewSwitcher.getCurrentView().equals(mArtistTrackListView))
                    mViewSwitcher.showNext();;
                mArtistTrackAdapter.addAll(tracks);
                mArtistTrackAdapter.setNotifyOnChange(true);
            }

        }
    }

}

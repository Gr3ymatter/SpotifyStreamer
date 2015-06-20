package com.gr3ymatter.spotifystreamer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import data.CustomTrack;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistTrackActivityFragment extends Fragment {


    //Global Class Variables
    ArtistTrackAdapter mArtistTrackAdapter;
    ListView mArtistTrackListView;
    String artistID;
    ViewSwitcher mViewSwitcher;
    SharedPreferences pref;
    ArrayList<CustomTrack> customTracks;


    //Strings
    private String errorString;
    private String LOCATION_KEY = "country";
    private String TRACKLIST_KEY = "tracklist";


    public ArtistTrackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_track, container, false);
        //Populating References
        mViewSwitcher = (ViewSwitcher)rootView.findViewById(R.id.viewswitcher);
        mArtistTrackListView = (ListView)rootView.findViewById(R.id.listview_artist_track);
        mArtistTrackAdapter = new ArtistTrackAdapter(getActivity(),R.layout.list_item_track);

        //Set Adapter
        mArtistTrackListView.setAdapter(mArtistTrackAdapter);

        //Getting Query Parameters from Intent and Shared Preferences
        artistID = getActivity().getIntent().getStringExtra(MainActivityFragment.ARTIST_ID);
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = pref.getString(getString(R.string.pref_country_preference_key),getString(R.string.pref_country_preference_default));

        //If there is no previous data then query Api
        if(savedInstanceState == null){
            getTrackData(artistID, location);
            return rootView;
        }

        //If there is previous data then get from savedInstanceState Bundle
        customTracks = savedInstanceState.getParcelableArrayList(TRACKLIST_KEY);
        mArtistTrackAdapter.clear();
        mArtistTrackAdapter.addAll(customTracks);
        mArtistTrackAdapter.setNotifyOnChange(true);        //Maybe Redundant

        return rootView;
    }

    private void getTrackData(String artistID, String location){

        //Local Variables
        SpotifyService spotifyService = new SpotifyApi().getService();
        HashMap<String, Object> locationQuery;
        locationQuery = new HashMap<String, Object>();

        locationQuery.put("country", location);

        spotifyService.getArtistTopTrack(artistID, locationQuery, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
            List<Track> trackList= tracks.tracks;

                customTracks = new ArrayList<CustomTrack>();
                for(Track track :trackList) {
                    customTracks.add(new CustomTrack(track));
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(customTracks == null || customTracks.size() == 0){
                            //Do something here... show that there are no results
                            if(mViewSwitcher.getCurrentView().equals(mArtistTrackListView))
                                mViewSwitcher.showNext();
                        }
                        else
                        {
                            //If the current view is not the ArtistTrackListView then display the ArtistTrackListView
                            //as we have artists to display
                            if(!mViewSwitcher.getCurrentView().equals(mArtistTrackListView))
                                mViewSwitcher.showNext();;
                            mArtistTrackAdapter.clear();                      //Clear the list

                            mArtistTrackAdapter.addAll(customTracks);           //Add Tracks together. Its better to add them all together than one by one
                            mArtistTrackAdapter.setNotifyOnChange(true);
                        }
                    }
                });

            }

            @Override
            public void failure(RetrofitError error) {

                //If there is an error then get the Response String and Display in an AlertDialog
                errorString = error.getResponse().getReason();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                        builder.setTitle("Invalid Input");
                        builder.setMessage(errorString);
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.show();



                        

                    }
                });


            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TRACKLIST_KEY, customTracks);
    }
}

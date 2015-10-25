package com.gr3ymatter.spotifystreamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    DialogFragment playerFragment;

    //Strings
    private static String errorString;
    private String LOCATION_KEY = "country";
    private String TRACKLIST_KEY = "tracklist";

    static String SONGNAME_KEY = "songname";
    static String ALBUMNAME_KEY = "albumname";
    static String ALBUMIMAGE_KEY = "albumImage";
    static String ALBNUMPREVIEW_URL_KEY = "albumpreviewkey";
    static String ARTISTNAME_KEY = "artistnamekey";
    static Bundle mSavedInstanceState;
    String location;

    boolean mIsLargeLayout = false;

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
        location = pref.getString(getString(R.string.pref_country_preference_key), getString(R.string.pref_country_preference_default));

        mArtistTrackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("PLAYERINTENT_TEST", "Clicked on artist top track number " + position);

                Intent playerIntent = new Intent(getActivity(), PlayerActivity.class);
                CustomTrack customTrack = customTracks.get(position);
                String songName = customTrack.mSongName;
                String albumName = customTrack.mAlbumName;
                String albumArt = customTrack.mAlbumImage_large;

                String albumPreview_URL = customTrack.mTrackPreview;

                playerIntent.putExtra(ARTISTNAME_KEY, customTrack.mArtistName);
                playerIntent.putExtra(SONGNAME_KEY, songName);
                playerIntent.putExtra(ALBUMNAME_KEY, albumName);
                playerIntent.putExtra(ALBUMIMAGE_KEY, albumArt);
                playerIntent.putExtra(ALBNUMPREVIEW_URL_KEY, albumPreview_URL);

               // DialogFragment playerDialog = PlayerDialog.newInstance(position, customTracks);

                showDialog(position);

               // playerDialog.show(getActivity().getSupportFragmentManager(),"player");
            }
        });

        mSavedInstanceState = savedInstanceState;
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


    public void showDialog(int position) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        playerFragment = new PlayerDialog().newInstance(position, customTracks);

        if (mIsLargeLayout) {
            // The device is using a large layout, so show the fragment as a dialog
            playerFragment.show(fragmentManager, "dialog");
        } else {
            // The device is smaller, so show the fragment fullscreen

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.replace(R.id.fragment_container, playerFragment)
                    .addToBackStack(null).commit();
        }
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

                if(error.getResponse() == null)
                    return;
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TRACKLIST_KEY, customTracks);
        outState.putString(MainActivityFragment.ARTIST_ID, artistID);
        //if(playerFragment != null)
        //    outState.putParcelableArrayList("player");
    }
}

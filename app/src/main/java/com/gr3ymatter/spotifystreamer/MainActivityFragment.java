package com.gr3ymatter.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

import data.CustomArtist;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    EditText mSearchEditText;
    ListView mArtistListView;
    ViewSwitcher mViewSwitcher;
    AsyncTask fetchArtistData;
    ArrayList<CustomArtist> customArtists;
    static ArrayAdapter<CustomArtist> artistAdapter;
    public static final String ARTIST_ID = "artist_id";
    public static final String ARTIST_NAME = "artist_name";
    private static final String EDITTEXT_VALUE = "edittext_value";
    private static final String CUSTOM_ARTIST_TAG = "customartist";

    static SpotifyApi mspotifyApi;

    long lastTimeTyped;

    public MainActivityFragment() {
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        artistAdapter = new ArtistListAdapter(getActivity(), R.layout.list_item_artist);
        mViewSwitcher = (ViewSwitcher)rootView.findViewById(R.id.viewswitcher);
        if(mSearchEditText == null)
            mSearchEditText = (EditText)rootView.findViewById(R.id.editText_search);
        mArtistListView = (ListView)rootView.findViewById(R.id.listview_artist);
        mArtistListView.setAdapter(artistAdapter);

        lastTimeTyped = System.currentTimeMillis();

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (System.currentTimeMillis() - lastTimeTyped > 500) {
                    Log.d("ONTEXT CHANGED", s.toString());
                    lastTimeTyped = System.currentTimeMillis();
                    if(!s.toString().equals(""))
                        refreshSearch(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        //This is workaround for saving state. The Spotify library does not implement Parceble or Serializable
        //thus i cannot save the Artist Arrays. Instead i choose to save the text value and redo the search
        //everytime the orientation is changed. One drawback to this approach is that the list clears if the
        //EditText view has no string.
        if(savedInstanceState != null){
            artistAdapter.clear();
            customArtists = savedInstanceState.getParcelableArrayList(CUSTOM_ARTIST_TAG);
            artistAdapter.addAll(customArtists);
        }

        mArtistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String artistId = ((CustomArtist)parent.getItemAtPosition(position)).mArtistID;
                String artistName = ((CustomArtist)parent.getItemAtPosition(position)).mArtistName;
                Intent idIntent = new Intent(getActivity(), ArtistTrackActivity.class);
                idIntent.putExtra(ARTIST_ID, artistId);
                idIntent.putExtra(ARTIST_NAME, artistName);
                startActivity(idIntent);
            }
        });

        Log.d(MainActivityFragment.class.getSimpleName(), "OnCreateView()");
        return rootView;
    }


    //This method cancels my old search. However the way i am doing it is questionable.
    //Am i creating a memoryleak by losing reference to the old AsyncTask??
    private void refreshSearch(String name){

        mspotifyApi = new SpotifyApi();
        SpotifyService spotifyService = mspotifyApi.getService();
        spotifyService.searchArtists(name, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
               List<Artist> artistList =  artistsPager.artists.items;
               customArtists = new ArrayList<CustomArtist>();
                for(Artist artist: artistList){
                    customArtists.add(new CustomArtist(artist));
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(customArtists == null || customArtists.size() == 0){
                            //Do something here... show that there are no results
                            if(mViewSwitcher.getCurrentView().equals(mArtistListView))
                                mViewSwitcher.showNext();
                        }
                        else
                        {
                            //If the current view is not the ArtistListView then display the ArtistListView
                            //as we have artists to display
                            if(!mViewSwitcher.getCurrentView().equals(mArtistListView))
                                mViewSwitcher.showNext();;
                            artistAdapter.clear();                      //Clear the list

                            artistAdapter.addAll(customArtists);           //Add Artists together. Its better to add them all together than one by one
                            artistAdapter.setNotifyOnChange(true);
                        }
                    }
                });


            }

            @Override
            public void failure(RetrofitError error) {

            }
        });


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(CUSTOM_ARTIST_TAG, customArtists);

    }

}

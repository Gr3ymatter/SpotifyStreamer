package com.gr3ymatter.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    EditText mSearchEditText;
    ListView mArtistListView;

    static SpotifyApi mspotifyApi;

    long lastTimeTyped;

    AsyncTask fetchArtistData;

    static ArrayAdapter<Artist> artistAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        artistAdapter = new ArtistListAdapter(getActivity(), R.layout.list_item_artist);

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

        return rootView;
    }


    private void refreshSearch(String name){

        if(fetchArtistData == null){
            fetchArtistData = new FetchArtistData().execute(name);
        }
        else
        {
            fetchArtistData.cancel(true);
            fetchArtistData = new FetchArtistData().execute(name);
        }

    }



    private class  FetchArtistData extends AsyncTask<String, Void, List>{

        List<Artist> artistList;

        private final String FETCHARTIST_TAG = FetchArtistData.class.getSimpleName();


        @Override
        protected List doInBackground(String... params) {
            mspotifyApi = new SpotifyApi();
            SpotifyService spotify = mspotifyApi.getService();

            artistList = spotify.searchArtists(params[0]).artists.items;

            return artistList;
        }

        @Override
        protected void onPostExecute(List aString) {
            super.onPostExecute(aString);

            if(artistList == null){
                //Do something here... show that there are no results
            }
            else
            {
                artistAdapter.clear();
                artistAdapter.addAll(artistList);
                artistAdapter.setNotifyOnChange(true);
            }

        }
    }


}

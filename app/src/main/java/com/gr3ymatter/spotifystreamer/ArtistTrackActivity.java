package com.gr3ymatter.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class ArtistTrackActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_track);
        if(savedInstanceState == null){

            Bundle args = new Bundle();
            args.putString(MainActivityFragment.ARTIST_ID, getIntent().getStringExtra(MainActivityFragment.ARTIST_ID));
            args.putString(MainActivityFragment.ARTIST_NAME, getIntent().getStringExtra(MainActivityFragment.ARTIST_NAME));

            ArtistTrackActivityFragment fragment = new ArtistTrackActivityFragment();
            fragment.setArguments(args);
            getSupportActionBar().setSubtitle(getIntent().getStringExtra(MainActivityFragment.ARTIST_NAME));
            getSupportFragmentManager().beginTransaction().add(R.id.artist_track_container, fragment, MainActivity.ARTISTTRACK_TAG).commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_artist_track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//        }

        switch(item.getItemId()){
            case android.R.id.home:
                Log.d("PLAYER DIALOG", "Back Button Pressed");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
//
    }
}

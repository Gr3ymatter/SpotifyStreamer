package com.gr3ymatter.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.FragCallback{

    public static Boolean mTwoPane = false;

    static String ARTISTTRACK_TAG = "ARTISTTRACK";

    @Override
    public void onItemSelected(String ArtistID, String ArtistName) {
        if(mTwoPane)
        {
            Bundle args = new Bundle();
            args.putString(MainActivityFragment.ARTIST_ID, ArtistID);
            args.putString(MainActivityFragment.ARTIST_NAME, ArtistName);

            ArtistTrackActivityFragment fragment = new ArtistTrackActivityFragment();
            fragment.setArguments(args);


            getSupportFragmentManager().beginTransaction().replace(R.id.artist_track_container, fragment, ARTISTTRACK_TAG).commit();
        }
        else{

            Intent activityIntent = new Intent(this, ArtistTrackActivity.class).putExtra(MainActivityFragment.ARTIST_ID, ArtistID);
            activityIntent.putExtra(MainActivityFragment.ARTIST_NAME, ArtistName);
            startActivity(activityIntent);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.artist_track_container) != null){

            mTwoPane = true;

            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction().replace(R.id.artist_track_container, new ArtistTrackActivityFragment(), ARTISTTRACK_TAG).commit();
            }

        } else {
            mTwoPane = false;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.





        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}

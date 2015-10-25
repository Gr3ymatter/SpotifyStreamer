package com.gr3ymatter.spotifystreamer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    SeekBar seekBar;
    Button playButton;
    Button nextButton;
    Button prevButton;
    TextView currentTimeTextView;
    TextView maxTimeTextView;


    boolean isCompleted;
    boolean isInitialized;

    private MediaPlayer mediaPlayer;

    enum State {
        Retrieving,
        Prepared,
        Stopped,
        Preparing,
        Playing,
        Paused
    };

    State mState = State.Retrieving;

    Handler mHandler;
    Runnable audioPlayer;

    public PlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_player, container, false);

        Intent infoIntent = getActivity().getIntent();

        String albumName = infoIntent.getStringExtra(ArtistTrackActivityFragment.ALBUMNAME_KEY);
        String songName = infoIntent.getStringExtra(ArtistTrackActivityFragment.SONGNAME_KEY);
        String albumImage = infoIntent.getStringExtra(ArtistTrackActivityFragment.ALBUMIMAGE_KEY);
        String albumPreviewURL = infoIntent.getStringExtra(ArtistTrackActivityFragment.ALBNUMPREVIEW_URL_KEY);

        TextView albumName_TextView = (TextView)rootView.findViewById(R.id.album_name);
        albumName_TextView.setText(albumName);
        TextView songName_TextView = (TextView)rootView.findViewById(R.id.song_name);
        songName_TextView.setText(songName);
        ImageView albumImage_ImageView = (ImageView)rootView.findViewById(R.id.album_imageview);
        TextView artistName_TextView = (TextView)rootView.findViewById(R.id.artist_name);
        artistName_TextView.setText(infoIntent.getStringExtra(ArtistTrackActivityFragment.ARTISTNAME_KEY));

        maxTimeTextView= (TextView)rootView.findViewById(R.id.maxTimeTextView);
        currentTimeTextView= (TextView)rootView.findViewById(R.id.currentTimeTextView);




        isInitialized = false;
        mHandler = new Handler();
        mediaPlayer = new MediaPlayer();
        audioPlayer = new Runnable() {
            @Override
            public void run() {
                if (!mState.equals(State.Paused) && mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition() / 500;
                    seekBar.setProgress(currentPosition);
                    updateTimeTextViews(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());

                }
                mHandler.postDelayed(this, 500);
            }
        };

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(albumPreviewURL);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        seekBar = (SeekBar)rootView.findViewById(R.id.track_seekbar);
        playButton = (Button)rootView.findViewById(R.id.play_button);
        prevButton = (Button)rootView.findViewById(R.id.prev_button);
        nextButton = (Button)rootView.findViewById(R.id.next_button);

        mediaPlayer.prepareAsync();
        mState = State.Preparing;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(audioPlayer);
            }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(audioPlayer);
                int maxDuration = mediaPlayer.getDuration()/500;
                int currentDuration = seekBar.getProgress();

                mediaPlayer.seekTo(currentDuration* 500);
                seekBar.setProgress(currentDuration);
                mHandler.postDelayed(audioPlayer, 500);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isCompleted = true;
                playButton.setBackgroundResource(android.R.drawable.ic_media_play);
                mState = State.Paused;

            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Toast.makeText(getActivity(), mediaPlayer.getDuration() + "", Toast.LENGTH_LONG);
                mState = State.Prepared;
                if (!mState.equals(State.Preparing) &&!mState.equals(State.Retrieving)) {
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration() / 500);
                    playButton.setBackgroundResource(android.R.drawable.ic_media_pause);

                }





            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState == State.Retrieving) {
                    mediaPlayer.prepareAsync();
                    mState = State.Preparing;

                }
                if (mState == State.Playing) {
                    mediaPlayer.pause();
                    mState = State.Paused;
                    playButton.setBackgroundResource(android.R.drawable.ic_media_play);

                    return;
                }
                if (mState == State.Paused) {
                    mediaPlayer.start();
                    mState = State.Playing;
                    playButton.setBackgroundResource(android.R.drawable.ic_media_pause);

                }

                if(mState == State.Preparing || mState == State.Playing) {

                    audioPlayer.run();
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (!mState.equals(State.Paused)) {
//                                int currentPosition = mediaPlayer.getCurrentPosition() / 500;
//                                seekBar.setProgress(currentPosition);
//
//                            }
//                            mHandler.postDelayed(this, 500);
//                        }
//                    });
                }

            }
        });

        Picasso.with(getActivity()).load(albumImage).into(albumImage_ImageView);

        return rootView;

    }

    void updateTimeTextViews(int currentDuration, int maxDuration){

        int currentSeconds = currentDuration/1000;
        int currentMinutes = currentSeconds/60;

        int maxSeconds = maxDuration/1000;
        int maxMinutes = maxSeconds/60;

        String currentTimeString = currentMinutes + ":" + currentSeconds%60;
        String maxTimeString = maxMinutes + ":" + maxSeconds % 60;

        maxTimeTextView.setText(maxTimeString);
        currentTimeTextView.setText(currentTimeString);



    }


    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(audioPlayer);
        mediaPlayer.stop();
        mediaPlayer.release();
    }



}

package com.gr3ymatter.spotifystreamer;

import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import data.CustomTrack;

/**
 * Created by salaf on 31-Aug-15.
 */
public class PlayerDialog extends android.support.v4.app.DialogFragment {

   static String INDEX = "index";
    static String TRACKSLIST = "tracklist";
    static String CURRENTLOCATION = "tracklocation";
    static String PLAYERSTATE = "state";
    ArrayList<CustomTrack> trackLists;
    int currentIndex;
    int currentLocation;
    boolean prepared = false;

    SeekBar seekBar;
    Button playButton;
    Button nextButton;
    Button prevButton;
    TextView currentTimeTextView;
    TextView maxTimeTextView;
    TextView albumName_TextView;
    TextView songName_TextView;
    ImageView albumImage_ImageView;
    TextView artistName_TextView;



    private MediaPlayer mediaPlayer;

    enum State {
        Retrieving,
        Stopped,
        Preparing,
        Playing,
        Paused
    };

    State mState = State.Retrieving;

    Handler mHandler;
    Runnable audioPlayer;

    public static PlayerDialog newInstance(int index, ArrayList<CustomTrack> customTracks) {

        Bundle args = new Bundle();

        PlayerDialog fragment = new PlayerDialog();
         args.putInt(INDEX, index);
         args.putParcelableArrayList(TRACKSLIST, customTracks);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // return super.onCreateView(inflater, container, savedInstanceState);


        View rootView = inflater.inflate(R.layout.fragment_player, container, false);


        albumName_TextView = (TextView)rootView.findViewById(R.id.album_name);
        songName_TextView = (TextView)rootView.findViewById(R.id.song_name);
        albumImage_ImageView = (ImageView)rootView.findViewById(R.id.album_imageview);
        artistName_TextView = (TextView)rootView.findViewById(R.id.artist_name);

        maxTimeTextView= (TextView)rootView.findViewById(R.id.maxTimeTextView);
        currentTimeTextView= (TextView)rootView.findViewById(R.id.currentTimeTextView);

        updateTrackInfo(currentIndex);
        mHandler = new Handler();

        mediaPlayer = new MediaPlayer();
        audioPlayer = new Runnable() {
            @Override
            public void run() {
                if (mState.equals(State.Playing) && mediaPlayer != null) {
                    currentLocation = mediaPlayer.getCurrentPosition() / 500;
                    seekBar.setProgress(currentLocation);
                    updateTimeTextViews(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());

                }
                mHandler.postDelayed(this, 500);
            }
        };

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(trackLists.get(currentIndex).mTrackPreview);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        seekBar = (SeekBar)rootView.findViewById(R.id.track_seekbar);
        playButton = (Button)rootView.findViewById(R.id.play_button);
        prevButton = (Button)rootView.findViewById(R.id.prev_button);
        nextButton = (Button)rootView.findViewById(R.id.next_button);

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
                int maxDuration = mediaPlayer.getDuration() / 500;
                int currentDuration = seekBar.getProgress();

                mediaPlayer.seekTo(currentDuration * 500);
                seekBar.setProgress(currentDuration);
                mHandler.postDelayed(audioPlayer, 500);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                playButton.setBackgroundResource(android.R.drawable.ic_media_play);
                mState = State.Paused;

            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Toast.makeText(getActivity(), mediaPlayer.getDuration() + "", Toast.LENGTH_LONG);

                mState = State.Playing;
                prepared = true;
                if (!mState.equals(State.Preparing) && !mState.equals(State.Retrieving)) {
                    mediaPlayer.seekTo(currentLocation * 500);
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration() / 500);
                    playButton.setBackgroundResource(android.R.drawable.ic_media_pause);

                }

            }
        });


        if(mState != State.Paused){
            mediaPlayer.prepareAsync();
            mState = State.Preparing;
        }


        audioPlayer.run();


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
                    if(!prepared) {
                        mediaPlayer.prepareAsync();
                        mState = State.Preparing;
                    }
                    else {
                        mediaPlayer.start();
                        mState = State.Playing;
                        playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
                    }
                }

                if (mState == State.Preparing || mState == State.Playing) {

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

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentIndex == 0)
                    currentIndex = trackLists.size() -1;
                else
                    currentIndex = currentIndex -1;
                updateTrackInfo(currentIndex);
                reinitializeMediaplayer(currentIndex);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = (currentIndex+1)%(trackLists.size());
                updateTrackInfo(currentIndex);
                reinitializeMediaplayer(currentIndex);
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                Log.d("PLAYER DIALOG", "Back Button Pressed");
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//
//        View rootView = inflater.inflate(R.layout.fragment_player, null);
//
//
//         albumName_TextView = (TextView)rootView.findViewById(R.id.album_name);
//         songName_TextView = (TextView)rootView.findViewById(R.id.song_name);
//         albumImage_ImageView = (ImageView)rootView.findViewById(R.id.album_imageview);
//         artistName_TextView = (TextView)rootView.findViewById(R.id.artist_name);
//
//        maxTimeTextView= (TextView)rootView.findViewById(R.id.maxTimeTextView);
//        currentTimeTextView= (TextView)rootView.findViewById(R.id.currentTimeTextView);
//
//
//
//        builder.setView(rootView);
//
//        updateTrackInfo(currentIndex);
//        mHandler = new Handler();
//
//        mediaPlayer = new MediaPlayer();
//        audioPlayer = new Runnable() {
//            @Override
//            public void run() {
//                if (mState.equals(State.Playing) && mediaPlayer != null) {
//                    int currentPosition = mediaPlayer.getCurrentPosition() / 500;
//                    seekBar.setProgress(currentPosition);
//                    updateTimeTextViews(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
//
//                }
//                mHandler.postDelayed(this, 500);
//            }
//        };
//
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//            mediaPlayer.setDataSource(trackLists.get(currentIndex).mTrackPreview);
//        }
//        catch(Exception ex){
//            ex.printStackTrace();
//        }
//
//        seekBar = (SeekBar)rootView.findViewById(R.id.track_seekbar);
//        playButton = (Button)rootView.findViewById(R.id.play_button);
//        prevButton = (Button)rootView.findViewById(R.id.prev_button);
//        nextButton = (Button)rootView.findViewById(R.id.next_button);
//
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                mHandler.removeCallbacks(audioPlayer);
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                mHandler.removeCallbacks(audioPlayer);
//                int maxDuration = mediaPlayer.getDuration()/500;
//                int currentDuration = seekBar.getProgress();
//
//                mediaPlayer.seekTo(currentDuration* 500);
//                seekBar.setProgress(currentDuration);
//                mHandler.postDelayed(audioPlayer, 500);
//            }
//        });
//
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//
//                playButton.setBackgroundResource(android.R.drawable.ic_media_play);
//                mState = State.Paused;
//
//            }
//        });
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                Toast.makeText(getActivity(), mediaPlayer.getDuration() + "", Toast.LENGTH_LONG);
//
//                mState = State.Playing;
//
//                if (!mState.equals(State.Preparing) && !mState.equals(State.Retrieving)) {
//                    mediaPlayer.start();
//                    seekBar.setMax(mediaPlayer.getDuration() / 500);
//                    playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
//
//                }
//
//            }
//        });
//
//        playButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mState == State.Retrieving) {
//                    mediaPlayer.prepareAsync();
//                    mState = State.Preparing;
//
//                }
//                if (mState == State.Playing) {
//                    mediaPlayer.pause();
//                    mState = State.Paused;
//                    playButton.setBackgroundResource(android.R.drawable.ic_media_play);
//
//                    return;
//                }
//                if (mState == State.Paused) {
//                    mediaPlayer.start();
//                    mState = State.Playing;
//                    playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
//
//                }
//
//                if (mState == State.Preparing || mState == State.Playing) {
//
//                    audioPlayer.run();
////                    getActivity().runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            if (!mState.equals(State.Paused)) {
////                                int currentPosition = mediaPlayer.getCurrentPosition() / 500;
////                                seekBar.setProgress(currentPosition);
////
////                            }
////                            mHandler.postDelayed(this, 500);
////                        }
////                    });
//                }
//
//            }
//        });
//
//        prevButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(currentIndex == 0)
//                    currentIndex = trackLists.size() -1;
//                else
//                    currentIndex = currentIndex -1;
//                updateTrackInfo(currentIndex);
//                reinitializeMediaplayer(currentIndex);
//            }
//        });
//
//        nextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentIndex = (currentIndex+1)%(trackLists.size());
//                updateTrackInfo(currentIndex);
//                reinitializeMediaplayer(currentIndex);
//            }
//        });
//
//        return builder.create();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INDEX, currentIndex);
        outState.putInt(CURRENTLOCATION, currentLocation);
        outState.putSerializable(PLAYERSTATE, mState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
        {
            currentIndex = savedInstanceState.getInt(INDEX);
            currentLocation = savedInstanceState.getInt(CURRENTLOCATION);
            mState = (State)savedInstanceState.getSerializable(PLAYERSTATE);
        }
        else {
            currentIndex = getArguments().getInt(INDEX);
        }
        trackLists = getArguments().getParcelableArrayList(TRACKSLIST);


    }

    void reinitializeMediaplayer(int Index)
    {
        mHandler.removeCallbacks(audioPlayer);
        mediaPlayer.reset();
        currentLocation = 0;
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(trackLists.get(Index).mTrackPreview);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        if(mState.equals(State.Playing))
        {
            mState = State.Retrieving;
            playButton.performClick();
        }
        mState = State.Retrieving;
    }

    void updateTimeTextViews(int currentDuration, int maxDuration){

        int currentSeconds = currentDuration/1000;
        int currentMinutes = currentSeconds/60;

        int maxSeconds = maxDuration/1000;
        int maxMinutes = maxSeconds/60;

        String currentTimeString = currentMinutes + ":" + String.format("%02d",currentSeconds%60);
        String maxTimeString = maxMinutes + ":" + maxSeconds%60;

        maxTimeTextView.setText(maxTimeString);
        currentTimeTextView.setText(currentTimeString);



    }

    void updateTrackInfo(int Index)
    {
        CustomTrack  currentTrack = trackLists.get(Index);
        albumName_TextView.setText(currentTrack.mAlbumName);
        songName_TextView.setText(currentTrack.mSongName);
        artistName_TextView.setText(currentTrack.mArtistName);
        Picasso.with(getActivity()).load(currentTrack.mAlbumImage_large).into(albumImage_ImageView);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(audioPlayer);
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}

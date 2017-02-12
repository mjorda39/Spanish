package com.example.android.spanish;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.spanish.R;

import java.util.ArrayList;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColorsFragment extends Fragment {
    private MediaPlayer mp;

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
        }
    };

    private AudioManager mAudioManager;

    AudioManager.OnAudioFocusChangeListener amChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT ||
                            focusChange == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        mp.pause();
                        mp.seekTo(0);
                    } else if (focusChange == AUDIOFOCUS_GAIN) {
                        mp.start();
                    } else if (focusChange == AUDIOFOCUS_LOSS) {
                        releaseMediaPlayer();
                    }
                }
            };


    public ColorsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);


        //ArrayList because it's dyynamic
        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("rojo", "red", R.drawable.color_red, R.raw.span_red));
        words.add(new Word("amarillo", "yellow", R.drawable.color_mustard_yellow, R.raw.span_yellow));
        words.add(new Word("verde", "green", R.drawable.color_green, R.raw.span_green));
        words.add(new Word("marrón", "brown", R.drawable.color_brown, R.raw.span_brown));
        words.add(new Word("gris", "gray", R.drawable.color_gray, R.raw.span_gray));
        words.add(new Word("negro", "black", R.drawable.color_black, R.raw.span_black));
        words.add(new Word("blanco", "white", R.drawable.color_white, R.raw.span_white));
        //ListView handles displaying items
        //ArrayAdapter handles formatting data into TextView
        WordAdapter adapter = new WordAdapter(getActivity(), words, R.color.category_colors); //context, items, layout
        ListView listView = (ListView)rootView.findViewById(R.id.list);
        listView.setAdapter(adapter); //attach adapter to ListView
        //attach item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Word word = words.get(i);
                //release MediaPlayer resources if already exist
                releaseMediaPlayer();
                //AudioManager
                int result = mAudioManager.requestAudioFocus(amChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    //create and setup MediaPlayer to play audio for current word
                    mp = MediaPlayer.create(getActivity(), word.getmAudioId());
                    //start audio file
                    mp.start();
                    //set up listener to release media when done playing
                    mp.setOnCompletionListener(mOnCompletionListener);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    /**
     * Free up media resources
     */
    private void releaseMediaPlayer() {
        if (mp != null) {
            mp.release();
            mp = null;
            mAudioManager.abandonAudioFocus(amChangeListener);
        }
    }

}

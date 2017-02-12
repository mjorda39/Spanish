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

import java.util.ArrayList;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;

/**
 * {@link Fragment} that displays a list of number vocabulary words.
 */
public class NumbersFragment extends Fragment {
    /**Handles playback of sound files*/
    private MediaPlayer mp;
    /**
     * Triggered when {@link MediaPlayer} has finished playing sound file
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //Audio has finished
            //release resources
            releaseMediaPlayer();
        }
    };
    /**Handles audio focus when playing sound files*/
    private AudioManager mAudioManager;
    /**
     * Triggered whenever audio focus changes - gain or lose
     * because of device or another app
     */
    AudioManager.OnAudioFocusChangeListener amChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                //AUDIOFOCUS_LOSS_TRANSIENT - lost for short amount of time
                //AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK - can continue but at lower volume
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT ||
                            focusChange == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        //pause and go back to beginning
                        mp.pause();
                        mp.seekTo(0);
                    } else if (focusChange == AUDIOFOCUS_GAIN) {
                        //AUDIOFOCUS_GAIN - regained focus and can continue playback
                        mp.start();
                    } else if (focusChange == AUDIOFOCUS_LOSS) {
                        //AUDIOFOCUS_LOSS - lost focus
                        // stop playback and release resources
                        releaseMediaPlayer();
                    }
                }
            };

    public NumbersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);
        /**Create {@link AudioManager to request audio focus}*/
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        //ArrayList for list of words
        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("uno", "one", R.drawable.number_one, R.raw.span_1));
        words.add(new Word("dos", "two", R.drawable.number_two, R.raw.span_2));
        words.add(new Word("tres", "three", R.drawable.number_three, R.raw.span_3));
        words.add(new Word("cuatro", "four", R.drawable.number_four, R.raw.span_4));
        words.add(new Word("cinco", "five", R.drawable.number_five, R.raw.span_5));
        words.add(new Word("seis", "six", R.drawable.number_six, R.raw.span_6));
        words.add(new Word("siete", "seven", R.drawable.number_seven, R.raw.span_7));
        words.add(new Word("ocho", "eight", R.drawable.number_eight, R.raw.span_8));
        words.add(new Word("nueve", "nine", R.drawable.number_nine, R.raw.span_9));
        words.add(new Word("diez", "ten", R.drawable.number_ten, R.raw.span_10));

        /**{@link WordAdapter has data source of a list of {@link Word}s}*/
        WordAdapter adapter = new WordAdapter(getActivity(), words, R.color.category_numbers); //context, data, layout
        /**Find ListView by id list defined in word_list.xml*/
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        //attach adapter to ListView
        listView.setAdapter(adapter);

        //add item click listener to play audio file
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //get the current word clicked on
                Word word = words.get(position);

                //release MediaPlayer resources if already exist
                releaseMediaPlayer();

                //AudioManager
                int result = mAudioManager.requestAudioFocus(
                        amChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                );

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    //we have audio focus
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
        //release resources when stopped
        releaseMediaPlayer();
    }

    /**
     * Free up media resources
     */
    private void releaseMediaPlayer() {
        //check if it exists
        if (mp != null) {
            //release anyways
            mp.release();
            //set it back to null
            mp = null;
            //release audio focus
            mAudioManager.abandonAudioFocus(amChangeListener);
        }
    }

}

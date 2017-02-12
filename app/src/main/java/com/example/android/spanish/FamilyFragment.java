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

import java.util.ArrayList;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;

/**
 * A simple {@link Fragment} subclass.
 */
public class FamilyFragment extends Fragment {
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


    public FamilyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        //ArrayList because it's dyynamic
        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("padre", "father", R.drawable.family_father, R.raw.span_dad));
        words.add(new Word("madre", "mother", R.drawable.family_mother, R.raw.span_mom));
        words.add(new Word("hermano", "brother", R.drawable.family_older_brother, R.raw.span_brother));
        words.add(new Word("hermana", "sister", R.drawable.family_older_sister, R.raw.span_sister));
        words.add(new Word("hijo", "son", R.drawable.family_son, R.raw.span_son));
        words.add(new Word("hija", "daughter", R.drawable.family_daughter, R.raw.span_daughter));
        words.add(new Word("abuelo", "grandfather", R.drawable.family_grandfather, R.raw.span_grandpa));
        words.add(new Word("abuela", "grandmother", R.drawable.family_grandmother, R.raw.span_grandma));
        //ListView handles displaying items
        //ArrayAdapter handles formatting data into TextView
        WordAdapter adapter = new WordAdapter(getActivity(), words, R.color.category_family); //ctxt, layout, items
        ListView listView = (ListView)rootView.findViewById(R.id.list);
        listView.setAdapter(adapter); //attach adapter to ListView
        //add item click listener to play audio file
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

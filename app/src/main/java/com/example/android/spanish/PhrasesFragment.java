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
public class PhrasesFragment extends Fragment {
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


    public PhrasesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        //ArrayList because it's dyynamic
        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("Buenos días", "Good morning", R.raw.span_morning));
        words.add(new Word("Buenas tardes", "Good afternoon", R.raw.span_afternoon));
        words.add(new Word("Buenas noches", "Good evening / night", R.raw.span_night));
        words.add(new Word("¿Cómo te llamas? / ¿Cuál es tu nombre?", "What's your name?", R.raw.span_yourname));
        words.add(new Word("Me llamo ... / Mi nombre es ...", "My name is ...", R.raw.span_myname));
        words.add(new Word("¿Cómo estás?", "How are you?", R.raw.span_hru));
        words.add(new Word("Estoy ...", "I'm ...", R.raw.span_iam));
        words.add(new Word("Bien", "Good / Fine", R.raw.span_fine));
        words.add(new Word("Mal", "Bad", R.raw.span_bad));
        words.add(new Word("Cansado (hombre) / Cansada (mujer)", "Tired", R.raw.span_tired));
        words.add(new Word("Enfermo (hombre) / Enferma (mujer)", "Sick", R.raw.span_sick));
        words.add(new Word("Gracias", "Thank you", R.raw.span_thanks));
        words.add(new Word("De nada", "You're welcome", R.raw.span_welcome));
        words.add(new Word("Hasta luego / Nos vemos", "See you later / See you", R.raw.span_seeyou));
        words.add(new Word("Adiós", "Goodbye", R.raw.span_bye));
        //ListView handles displaying items
        //ArrayAdapter handles formatting data into TextView
        WordAdapter adapter = new WordAdapter(getActivity(), words, R.color.category_phrases); //ctxt, layout, items
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

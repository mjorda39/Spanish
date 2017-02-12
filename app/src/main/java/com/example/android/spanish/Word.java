package com.example.android.spanish;

/**
 * Created by mjord on 2/2/2017.
 */

public class Word {
    private String mSpanishTranslation;
    private String mDefaultTranslation;
    private int mImageResId = NO_IMAGE;
    private static final int NO_IMAGE = -1;
    private int mAudioId;

    public Word(String spanishTranslation, String defaultTranslation, int audioId) {
        mSpanishTranslation = spanishTranslation;
        mDefaultTranslation = defaultTranslation;
        mAudioId = audioId;
    }

    public Word(String spanishTranslation, String defaultTranslation, int resId, int audioId) {
        mSpanishTranslation = spanishTranslation;
        mDefaultTranslation = defaultTranslation;
        mImageResId = resId;
        mAudioId = audioId;
    }

    public String getSpanishTranslation() {
        return mSpanishTranslation;
    }

    public String getDefaultTranslation() {
        return mDefaultTranslation;
    }

    public int getImageResId() {
        return mImageResId;
    }

    public boolean has_image() {
        return mImageResId != NO_IMAGE;
    }

    public int getmAudioId() {
        return mAudioId;
    }


}

package com.example.android.spanish;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.spanish.R;
import com.example.android.spanish.Word;

import java.util.ArrayList;

/**
 * Created by mjord on 2/2/2017.
 * created own adapter class to return a Word to NumbersActivity
 * rather than a TextView
 */

public class WordAdapter extends ArrayAdapter<Word> {

    private int mColorResId;

    public WordAdapter(Activity context, ArrayList<Word> words, int colorResId) {
        super(context, 0, words);
        mColorResId = colorResId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Check if view being used. if not, inflate it
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        //get the Word object located at the position in the list
        final Word currentWord = getItem(position);
        //get and set ImageView
        ImageView image = (ImageView)listItemView.findViewById(R.id.image);
        if (currentWord.has_image()) {
            image.setImageResource(currentWord.getImageResId());
            image.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.GONE);
        }
        //set theme color for list item
        View textContainer = listItemView.findViewById(R.id.text_container);
        //find color the resource maps to
        int color = ContextCompat.getColor(getContext(), mColorResId);
        //set background color
        textContainer.setBackgroundColor(color);
        //get and set TextViews
        TextView spanishView = (TextView)listItemView.findViewById(R.id.spanish);
        spanishView.setText(currentWord.getSpanishTranslation());
        TextView defaultView = (TextView)listItemView.findViewById(R.id.english);
        defaultView.setText(currentWord.getDefaultTranslation());
        return listItemView;
    }
}

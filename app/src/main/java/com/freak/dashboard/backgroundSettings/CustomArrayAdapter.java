package com.freak.dashboard.backgroundSettings;

import android.content.Context;
import android.widget.ArrayAdapter;

public class CustomArrayAdapter extends ArrayAdapter<CharSequence> {

    private final CharSequence[] mObjects;

    public CustomArrayAdapter(Context context, int textViewResourceId, CharSequence[] objects) {
        super(context, textViewResourceId, objects);
        mObjects = objects;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != mObjects.length - 1;
    }

}
package com.freak.dashboard.backgroundSettings;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.ListPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ListAdapter;

public class CustomListPreference extends ListPreference {

    public CustomListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepareDialogBuilder(@NonNull AlertDialog.Builder builder) {
        ListAdapter listAdapter = new CustomArrayAdapter(getContext(),
                android.R.layout.select_dialog_singlechoice, getEntries());

        builder.setAdapter(listAdapter, this);

        super.onPrepareDialogBuilder(builder);
    }
}
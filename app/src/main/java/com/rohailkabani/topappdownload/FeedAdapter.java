package com.rohailkabani.topappdownload;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by rohailkabani on 2017-12-28.
 */

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> application;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> application) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.application = application;
    }

//    tvName = (TextView) findViewById(R.id.tvName);
//    tvArtist = (TextView) findViewById(R.id.tvArtist);
//    tvSummary = (TextView) findViewById(R.id.tvSummary);
}

package com.rohailkabani.topappdownload;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by rohailkabani on 2017-12-27.
 */

public class ParseApps {
    private static final String TAG = "ParseApps";
    private ArrayList<FeedEntry> applications;

    public ParseApps() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse (String xmlData) {
        boolean status = true;
        FeedEntry currentEntry = null;
        boolean inEntry = false;
        String textValue = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xapp = factory.newPullParser();
            xapp.setInput(new StringReader(xmlData));
            int eventType = xapp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xapp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "parse: starting tag for " + tagName);
                        if ("entry".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentEntry = new FeedEntry();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xapp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "parse: Ending tag for " + tagName);
                        if (inEntry) {
                            applications.add(currentEntry);
                            inEntry = false;
                        } else if ("name".equalsIgnoreCase(tagName)) {
                            currentEntry.setName(textValue);
                        } else if ("artist".equalsIgnoreCase(tagName)) {
                            currentEntry.setArtist(textValue);
                        } else if ("releaseDate".equalsIgnoreCase(tagName)) {
                            currentEntry.setReleaseDate(textValue);
                        } else if ("summary".equalsIgnoreCase(tagName)) {
                            currentEntry.setSummary(textValue);
                        } else if ("image".equalsIgnoreCase(tagName)) {
                            currentEntry.setImageURL(textValue);
                        }
                        break;
                    default:
                        //Nothing else to do
                }
                eventType = xapp.next();
            }

        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }

        return status;
    }
}

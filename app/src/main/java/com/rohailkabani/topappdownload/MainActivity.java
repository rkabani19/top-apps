package com.rohailkabani.topappdownload;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listApps;
    private String URL_APPS = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    private String feedCachedURL = "Invalid";
    public static final String STATE_URL = "feedURL";
    public static final String STATE_LIMIT = "feedLimit";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = (ListView) findViewById(R.id.xmlListView);

        if (savedInstanceState != null) {
            URL_APPS = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

        downloadURL(String.format(URL_APPS, feedLimit));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if (feedLimit == 10) {
            menu.findItem(R.id.mnu10).setChecked(true);
        } else {
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.mnuFree:
                URL_APPS = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                URL_APPS = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                URL_APPS = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feed limit to " + feedLimit);
                } else {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " feedLimit is unchanged.");
                }
                break;
            case R.id.mnuRefresh:
                feedCachedURL = "Invalid";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        downloadURL(String.format(URL_APPS, feedLimit));

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, URL_APPS);
        outState.putInt(STATE_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }

    private void downloadURL (String URL_MNU) {
        if (!URL_MNU.equalsIgnoreCase(feedCachedURL)){
            Log.d(TAG, "onCreate: starting AsyncTask");
            DownloadData downloadData = new DownloadData();
            downloadData.execute(URL_MNU);
            feedCachedURL = URL_MNU;
            Log.d(TAG, "downloadURL: done.");
        } else {
            Log.d(TAG, "downloadURL: URL not changed.");
        }
    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApps parseApps = new ParseApps();
            parseApps.parse(s);

//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(
//                    MainActivity.this, R.layout.list_item, parseApps.getApplications());
//            listApps.setAdapter(arrayAdapter);
            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record, parseApps.getApplications());
            listApps.setAdapter(feedAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if (rssFeed == null) {
                Log.e(TAG, "doInBackground: Error downloading.");
            }
            return rssFeed;
        }

        private String downloadXML (String urlPath) {
            StringBuilder xmlResult = new StringBuilder();

            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was " + response);
//                InputStream inputStream = connection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader reader = new BufferedReader(inputStreamReader);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead;
                char[] inputBuffer = new char[500];
                while (true) {
                    charsRead = reader.read(inputBuffer);
                    if (charsRead < 0) {
                        break;
                    }
                    if (charsRead > 0) {
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }
                reader.close();

                return xmlResult.toString();
            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage() );
            } catch (IOException e) {
                Log.e(TAG, "downloadXML: IO Exception reading data. " + e.getLocalizedMessage() );
            } catch (SecurityException e) {
                Log.e(TAG, "downloadXML: Security Exception. " + e.getMessage());
            }

            return null;
        }
    }
}

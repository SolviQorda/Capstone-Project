package qorda_projects.tracktive.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import qorda_projects.tracktive.R;
import qorda_projects.tracktive.data.CardsContract;

import static android.text.format.DateUtils.DAY_IN_MILLIS;

/**
 * Created by sorengoard on 11/01/2017.
 */

public class TracktiveSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String LOG_TAG = TracktiveSyncAdapter.class.getSimpleName().toString();

    public static final int SYNC_INTERVAL = ((int)DAY_IN_MILLIS * 1000) / 4;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    ContentResolver mContentResolver;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LOCATION_STATUS_OK, LOCATION_STATUS_SERVER_DOWN, LOCATION_STATUS_SERVER_INVALID, LOCATION_STATUS_UNKNOWN, LOCATION_STATUS_INVALID})
    public @interface LocationStatus{}

    public static final int LOCATION_STATUS_OK = 0;
    public static final int LOCATION_STATUS_SERVER_DOWN = 1;
    public static final int LOCATION_STATUS_SERVER_INVALID = 2;
    public static final int LOCATION_STATUS_UNKNOWN = 3;
    public static final int LOCATION_STATUS_INVALID = 4;

    public TracktiveSyncAdapter(Context context,boolean autoInitalize) {
        super(context, autoInitalize);

    }


    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult result) {
        Log.d(LOG_TAG, "starting sync");

        HttpURLConnection urlConnection = null;
        BufferedReader buffReader = null;

        //loop the keywords
        //will contain the json response.
        String storiesJsonStr = null;
        String callbackFormat = "JSON_CALLBACK";
        //TODO : this needs to pull a hard string from the DB hardcoded for the moment
        String keywordsQuery = "migrants,mediterranean,deaths";
        String getArticles = "getArticles";
        String articles = "articles";

        try {
            // construct URL for the EventRegistry query

            final String STORIES_BASE_URL = "http://eventregistry.org/json/article?ignoreKeywords=";
            final String KEYWORDS_PARAM = "keywords";
            final String ACTION_PARAM = "action";
            final String RESULT_TYPE_PARAM = "resultType";
            final String CALLBACK_PARAM = "callback";

            Uri builtUri = Uri.parse(STORIES_BASE_URL).buildUpon()
                    .appendQueryParameter(KEYWORDS_PARAM, keywordsQuery)
                    .appendQueryParameter(ACTION_PARAM, getArticles)
                    .appendQueryParameter(RESULT_TYPE_PARAM, articles)
                    .appendQueryParameter(CALLBACK_PARAM, callbackFormat)
                    .build();

            URL url = new URL(builtUri.toString());

            //create request to EventRegisty, then open the connection.
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //read input stream into a string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }
            buffReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = buffReader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                //stream was empty therefore no point parsing it
                //TODO: set the status as server down if we want feedback - might not be necessary but good to bookmark.
                return;
              }
            storiesJsonStr = buffer.toString();
            getStoriesDataFromJson(storiesJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: ", e);
            //TODO: set server status if necessary

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            //TODO: set server status if necessary
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            } if (buffReader != null){
                try{
                    buffReader.close();
                } catch (final IOException e){
                    Log.e(LOG_TAG, "Error closing stream", e);
                }

            }
        }
        return;
    }
    private void getStoriesDataFromJson(String storiesJsonStr) throws JSONException {


        // names of JSON objects to be extracted - TODO: check these when you have wifi.
        final String ER_TITLE = "title";
        final String ER_BODY = "body";
        final String ER_DATE = "date";
        final String ER_URL = "url";
        final String ER_SOURCE = "source";
        final String ER_RESULTS = "results";


        //TODO: Don't fully understand the fucntion of this or whether there is a corresponding code in
        //the ER API. Ask on Discourse?

        final String ER_MESSAGE_CODE = "cod";

        try {
            JSONObject CardStoriesJson = new JSONObject(storiesJsonStr);

            //errors?
            if(CardStoriesJson.has(ER_MESSAGE_CODE)) {
                int errorCode = CardStoriesJson.getInt(ER_MESSAGE_CODE);

                switch (errorCode){
                    case HttpURLConnection.HTTP_OK:
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        return;
                }
            }

            JSONArray storiesArray = CardStoriesJson.getJSONArray(ER_RESULTS);
            Vector<ContentValues> cvVector = new Vector<>(storiesArray.length());
            for(int i = 0; i < storiesArray.length(); i++) {
                //title, body, url
                String title;
                String body;
                String date
                String url;
                String source;

                JSONObject StoryObject = storiesArray.getJSONObject(i);
                title = StoryObject.getString(ER_TITLE);
                body = StoryObject.getString(ER_BODY);
                url = StoryObject.getString(ER_URL);
                date = StoryObject.getString(ER_DATE);

                //get array object for the source, then get the source
                JSONObject sourceObject = StoryObject.getJSONObject(ER_SOURCE);
                source = sourceObject.getString(ER_TITLE);

                ContentValues storyValues = new ContentValues();

                storyValues.put(CardsContract.CardEntry.COLUMN_TITLE, title);
                storyValues.put(CardsContract.CardEntry.COLUMN_CONTENT, body);
                storyValues.put(CardsContract.CardEntry.COLUMN_DATE, date);
                storyValues.put(CardsContract.CardEntry.COLUMN_URL, url);
                storyValues.put(CardsContract.CardEntry.COLUMN_SOURCE, source);

                // pull from the same variable we use to get the original json query in the onPerformSync above

//                storyValues.put(CardsContract.CardEntry.COLUMN_CARD_KEYWORDS, keywordsQuery);
                cvVector.add(storyValues);

            }
            // bulkInsert to db --> how do we handle duplicates? Maybe need to take out this bulk insert.
            // Or put in a limit on the timespan.
            int inserted = 0;
            if ( cvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);

                getContext().getContentResolver().bulkInsert(CardsContract.CardEntry.CONTENT_URI, cvArray);

            }
            Log.d(LOG_TAG, "ER SyncService complete: " + cvVector.size() + "inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }

    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        TracktiveSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
    }


}

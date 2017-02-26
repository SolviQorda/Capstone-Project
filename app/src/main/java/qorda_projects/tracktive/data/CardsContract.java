package qorda_projects.tracktive.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import static qorda_projects.tracktive.sync.TracktiveSyncAdapter.LOG_TAG;


/**
 * Created by sorengoard on 09/01/2017.
 */

public class CardsContract {



    //TODO check this
    public static final String CONTENT_AUTHORITY = "qorda_projects.tracktive.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //possible paths
    public static final String PATH_CARDS = "cards";
    public static final class CardEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CARDS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARDS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARDS;

        public static final String TABLE_NAME = "cards";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_SOURCE = "source";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_CONTENT = "body";
        public static final String COLUMN_CARD_KEYWORDS = "card_keywords";
        public static final String COLUMN_BOOKMARKED = "bookmarked";
        public static final String COLUMN_TAB_NUMBER = "tab_number";

        public static Uri buildCardsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildSingleStoryUri(int storyId) {
            String storyIdString = Integer.toString(storyId);
            return CONTENT_URI.buildUpon().appendPath(storyIdString).build();
        }

        public static String getIdFromUri(Uri uri) {
            String id = uri.getPathSegments().get(1);
            Log.v(LOG_TAG, "getidfromUri is: " + id);
            return id;
            }
    }



}

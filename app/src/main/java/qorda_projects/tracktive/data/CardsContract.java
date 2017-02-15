package qorda_projects.tracktive.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by sorengoard on 09/01/2017.
 */

public class CardsContract {

    //TODO check this
    public static final String CONTENT_AUTHORITY = "qorda_projects.tracktive.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String BOOKMARKS = "bookmarks";

    //possible paths
    public static final String PATH_CARDS = "cards";
    public static final String PATH_DIARY = "diary";
    public static final String PATH_SINGLE_CARD = "single_card";

    public static final class CardEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CARDS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARDS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARDS + "/" + PATH_SINGLE_CARD;


        public static final String TABLE_NAME = "cards";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_SOURCE = "source";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_CONTENT = "body";
        public static final String COLUMN_CARD_KEYWORDS = "card_keywords";
        public static final String COLUMN_BOOKMARKED = "bookmarked";

        public static Uri buildCardsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //either string keywords, or do the call to the Utility method based on passing an int to this method. remember needs context.
        public static Uri buildSingleCardUri(String keywords) {
            return CONTENT_URI.buildUpon().appendPath(keywords).build();
        }

        public static Uri buildBookmarkedStoriesUri(String keywords) {
            return CONTENT_URI.buildUpon().appendPath(BOOKMARKS).appendPath(keywords).build();
        }

        public static String getKeywordsFromUri(Uri uri) {
            //TODO: find path segments that correspond to keywords  - what does 1 do?
            return uri.getPathSegments().get(1);
        }
    }

    public static final class DiaryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DIARY).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIARY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIARY;

        public static final String TABLE_NAME = "diary";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CARD_KEYWORDS = "card_keywords";


        public static Uri buildDiaryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

//
//        public static String getDiaryKeywordsFromUri(Uri uri) {
//            //return uri.getPathSegments().get(//TODO: find path segments that correspond to keywords  );
//        }

    }


}

package qorda_projects.tracktive.data;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by sorengoard on 07/01/2017.
 */

public class ContentProvider extends android.content.ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CardDbHelper mCardDbHelper;
    private static final String LOG_TAG = ContentProvider.class.getSimpleName().toString();

    static final int CARDS = 100;
    static final int SINGLE_CARD = 101;
    static final int SINGLE_STORY = 102;
    static final int BOOKMARKED = 103;
    static final int DIARY = 200;
    static final int SINGLE_ENTRY = 201;

    private static final SQLiteQueryBuilder sCardsByKeywordsQueryBuilder;
    private static final SQLiteQueryBuilder sDiaryByKeywordsBuilder;

    static { sCardsByKeywordsQueryBuilder = new SQLiteQueryBuilder();
        sCardsByKeywordsQueryBuilder.setTables(CardsContract.CardEntry.TABLE_NAME);

    }
    static { sDiaryByKeywordsBuilder = new SQLiteQueryBuilder();}



    //keywords = ?
    private static final String sKeywordsForCardSetting =
            CardsContract.CardEntry.TABLE_NAME + "." + CardsContract.CardEntry.COLUMN_CARD_KEYWORDS + " = ? ";

//    private static final String sIdForSingleSingleStorySetting =
//            CardsContract.CardEntry.TABLE_NAME + "." + CardsContract.CardEntry._ID + " = ? ";

    private static final String sDiaryForCardSetting =
            CardsContract.DiaryEntry.TABLE_NAME + "." + CardsContract.DiaryEntry.COLUMN_CARD_KEYWORDS + " = ? ";

    private static final String sSingleDiaryEntrySetting =
            CardsContract.DiaryEntry.TABLE_NAME + "." + CardsContract.DiaryEntry._ID + " = ? ";



    private Cursor getCardStoriesByKeywordsSetting(Uri uri, String[] projection, String sortOrder) {
        // Get keywords from card title corresponding to current card.
        String cardsSetting = CardsContract.CardEntry.getKeywordsFromUri(uri);
        Log.v(LOG_TAG, "keywords from cardsetting CP: " + cardsSetting);

        String[] selectionArgs;

        String selection = sKeywordsForCardSetting;

        //TODO: this is coming from a an array list (or a string?) so we need to make sure it ends up as an array

        selectionArgs = new String[]{cardsSetting};

        return sCardsByKeywordsQueryBuilder.query(mCardDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

    }

//    private Cursor getSingleStorybyIdSetting(Uri uri, String[] projection, String sortOrder) {
//
//        String id = CardsContract.CardEntry.getIdFromUri(uri);
//
//        String[] selectionArgs;
//        String selection = sIdForSingleSingleStorySetting;
//
//        selectionArgs = new String[]{id};
//
//        return sStoryByIdBuilder.query(mCardDbHelper.getReadableDatabase(),
//            projection,
//            selection,
//            selectionArgs,
//            null,
//            null,
//            sortOrder);
//    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CardsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, CardsContract.PATH_CARDS, CARDS);
        //matcher for a single card based on keywords
        matcher.addURI(authority, CardsContract.PATH_CARDS + "/*" , SINGLE_CARD);
        //matcher for a single story
        matcher.addURI(authority, CardsContract.PATH_CARDS + "/story" + "/*", SINGLE_STORY);
        //matcher for bookmarked stories in a card
        matcher.addURI(authority, CardsContract.PATH_CARDS + "/bookmarks" + "/*", BOOKMARKED);
        // matcher for a diary based on keywords
        matcher.addURI(authority, CardsContract.PATH_DIARY + "/*", DIARY);
        //matcher for diary entry based on id
        matcher.addURI(authority, CardsContract.PATH_DIARY + "/entry" + "/*", SINGLE_ENTRY);

        //TODO: add other cases, eg diary

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mCardDbHelper = new CardDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            //TODO: fill in other cases CARDS BY KEYWORD
            case CARDS:
                Log.v(LOG_TAG, "multiple cards card type called in GetType");
                return CardsContract.CardEntry.CONTENT_TYPE;
            case SINGLE_CARD:
                Log.v(LOG_TAG, "single card type called in GetType");
                return CardsContract.CardEntry.CONTENT_TYPE;
            case SINGLE_STORY:
                Log.v(LOG_TAG, "single story type called in GetType");
                return CardsContract.CardEntry.CONTENT_ITEM_TYPE;
            case BOOKMARKED:
                Log.v(LOG_TAG, "bookmarked type called in GetType");
                return CardsContract.CardEntry.CONTENT_TYPE;
            case DIARY:
                Log.v(LOG_TAG, "diary type called in GetType");
                return CardsContract.DiaryEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

//    private Cursor getDiaryByCardKeyword(Uri uri, String[] projection, String sortOrder) {
//        String diaryEntrySetting = CardsContract.DiaryEntry.getDiaryKeywordsFromUri(uri);
//        String[] selectionArgs = new String[]{diaryEntrySetting};
//        return sDiaryByKeywordsBuilder.query(mCardDbHelper.getReadableDatabase(),
//                projection,
//                sDiaryForCardSetting,
//                selectionArgs,
//                null,
//                null,
//                sortOrder
//                );
//        }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch(sUriMatcher.match(uri)) {

            case CARDS:
                Log.v(LOG_TAG, "multiple cards card type called in query");

            {
                retCursor = mCardDbHelper.getReadableDatabase().query(
                        CardsContract.CardEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case SINGLE_CARD:
                Log.v(LOG_TAG, "single cards card type called in query");

            {
                retCursor = getCardStoriesByKeywordsSetting(uri, projection, sortOrder);
                break;
            }
//            case SINGLE_STORY:
//                Log.v(LOG_TAG, "single story type called in query");
//            {
//                retCursor = getSingleStorybyIdSetting(uri, projection, sortOrder);
//                break;
//            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            //TODO: other cases here.
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
     }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase sqlDb = mCardDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match) {
            case CARDS: {
                long _id =sqlDb.insert(CardsContract.CardEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = CardsContract.CardEntry.buildCardsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
                //TODO: diary case
            }
            case DIARY: {
                long _id = sqlDb.insert(CardsContract.DiaryEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = CardsContract.DiaryEntry.buildDiaryUri(_id);
                else
                    throw new android.database.SQLException("failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqlDb = mCardDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection ) selection = "1";
        switch(match) {
            case CARDS:
                rowsDeleted = sqlDb.delete(
                        CardsContract.CardEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DIARY:
                rowsDeleted = sqlDb.delete(
                        CardsContract.DiaryEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //A null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final SQLiteDatabase sqlDb = mCardDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CARDS:
                rowsUpdated = sqlDb.update(CardsContract.CardEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case DIARY:
                rowsUpdated = sqlDb.update(CardsContract.DiaryEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase sqlDb = mCardDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case CARDS:
                sqlDb.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = sqlDb.insert(CardsContract.CardEntry.TABLE_NAME, null, value);
                        if(_id != -1) {
                            returnCount++;
                        }
                    }
                    sqlDb.setTransactionSuccessful();
                } finally {
                    sqlDb.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mCardDbHelper.close();
        super.shutdown();
    }

}

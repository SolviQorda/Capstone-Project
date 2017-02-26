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
    static final int SINGLE_STORY = 102;

    private static final SQLiteQueryBuilder sStoryByIdBuilder;

    static { sStoryByIdBuilder = new SQLiteQueryBuilder();
        sStoryByIdBuilder.setTables(CardsContract.CardEntry.TABLE_NAME);}

    private static final String sIdForSingleSingleStorySetting =
            CardsContract.CardEntry.TABLE_NAME + "." + CardsContract.CardEntry._ID + " = ? ";


    private Cursor getSingleStorybyIdSetting(Uri uri, String[] projection, String sortOrder) {

        String id = CardsContract.CardEntry.getIdFromUri(uri);

        String[] selectionArgs;
        String selection = sIdForSingleSingleStorySetting;

        selectionArgs = new String[]{id};

        return sStoryByIdBuilder.query(mCardDbHelper.getReadableDatabase(),
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CardsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, CardsContract.PATH_CARDS, CARDS);
        //matcher for a single story
        matcher.addURI(authority, CardsContract.PATH_CARDS + "/#", SINGLE_STORY);

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
            case SINGLE_STORY:
                Log.v(LOG_TAG, "single story type called in GetType");
                return CardsContract.CardEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


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
            case SINGLE_STORY:
                Log.v(LOG_TAG, "single story type called in query");
            {
                retCursor = getSingleStorybyIdSetting(uri, projection, sortOrder);
                break;
            }
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
            case SINGLE_STORY:
                rowsDeleted = sqlDb.delete(
                        CardsContract.CardEntry.TABLE_NAME, selection, selectionArgs);
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
            case SINGLE_STORY:
                rowsUpdated = sqlDb.update(CardsContract.CardEntry.TABLE_NAME, contentValues, selection, selectionArgs);
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

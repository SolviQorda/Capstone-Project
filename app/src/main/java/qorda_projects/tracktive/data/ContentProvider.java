package qorda_projects.tracktive.data;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by sorengoard on 07/01/2017.
 */

public class ContentProvider extends android.content.ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CardDbHelper mCardDbHelper;

    static final int CARDS = 100;
    static final int DIARY = 200;

    private static final SQLiteQueryBuilder sCardsByKeywordsQueryBuilder;
    private static final SQLiteQueryBuilder sDiaryByKeywordsBuilder;

    static { sCardsByKeywordsQueryBuilder = new SQLiteQueryBuilder();}
    static { sDiaryByKeywordsBuilder = new SQLiteQueryBuilder();}

    //keywords = ?
    private static final String sKeywordsForCardSetting =
            CardsContract.CardEntry.TABLE_NAME + "." + CardsContract.CardEntry.COLUMN_CARD_KEYWORDS + " = ? ";

    private static final String sDiaryForCardSetting =
            CardsContract.DiaryEntry.TABLE_NAME + "." + CardsContract.DiaryEntry.COLUMN_CARD_KEYWORDS + " = ? ";



    private Cursor getCardStoriesByKeywordsSetting(Uri uri, String[] projection, String sortOrder) {
        String cardsSetting = CardsContract.CardEntry.getKeywordsFromUri(uri);

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

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CardsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, CardsContract.PATH_CARDS, CARDS);

        matcher.addURI(authority, CardsContract.PATH_DIARY, DIARY);

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
                return CardsContract.CardEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private Cursor getDiaryByCardKeyword(Uri uri, String[] projection, String sortOrder) {
        String diaryEntrySetting = CardsContract.DiaryEntry.getDiaryKeywordsFromUri(uri);
        String[] selectionArgs = new String[]{diaryEntrySetting};
        return sDiaryByKeywordsBuilder.query(mCardDbHelper.getReadableDatabase(),
                projection,
                sDiaryForCardSetting,
                selectionArgs,
                null,
                null,
                sortOrder
                );
        }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch(sUriMatcher.match(uri)) {

            case CARDS:
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

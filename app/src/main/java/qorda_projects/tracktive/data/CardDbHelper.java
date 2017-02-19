package qorda_projects.tracktive.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sorengoard on 09/01/2017.
 */

public class CardDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "tracktive.db";

    public CardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlitedatabase) {

        final String SQL_CREATE_CARDS_TABLE = "CREATE TABLE " + CardsContract.CardEntry.TABLE_NAME +  " (" +
                CardsContract.CardEntry._ID + " INTEGER PRIMARY KEY," +
                CardsContract.CardEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                CardsContract.CardEntry.COLUMN_SOURCE + " TEXT NOT NULL," +
                CardsContract.CardEntry.COLUMN_DATE + " TEXT NOT NULL," +
                CardsContract.CardEntry.COLUMN_URL + " TEXT NOT NULL," +
                CardsContract.CardEntry.COLUMN_CONTENT + " TEXT NOT NULL," +
                CardsContract.CardEntry.COLUMN_CARD_KEYWORDS + " TEXT NOT NULL," +
                CardsContract.CardEntry.COLUMN_BOOKMARKED + " TEXT NOT NULL," +
                CardsContract.CardEntry.COLUMN_TAB_NUMBER + " INTEGER NOT NULL" +
                " );";

        final String SQL_CREATE_DIARY_TABLE = "CREATE TABLE " + CardsContract.DiaryEntry.TABLE_NAME + " (" +
                CardsContract.DiaryEntry._ID + " INTEGER PRIMARY KEY," +
                CardsContract.DiaryEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                CardsContract.DiaryEntry.COLUMN_DATE + " TEXT NOT NULL," +
                CardsContract.DiaryEntry.COLUMN_CONTENT + " TEXT NOT NULL," +
                CardsContract.DiaryEntry.COLUMN_CARD_KEYWORDS + " TEXT NOT NULL " +
                " );";

        sqlitedatabase.execSQL(SQL_CREATE_CARDS_TABLE);
        sqlitedatabase.execSQL(SQL_CREATE_DIARY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CardsContract.CardEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CardsContract.DiaryEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}

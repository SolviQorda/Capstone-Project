package qorda_projects.tracktive;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import qorda_projects.tracktive.data.CardsContract;

/**
 * Created by sorengoard on 14/02/2017.
 */

public class StoryDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public final String LOG_TAG = StoryDetailFragment.class.getSimpleName().toString();
    private Uri mUri;
    private final String DETAIL_URI = "detailUri";
    private static final int STORY_DETAIL_LOADER = 0;
    private String mBookmarked;


    private static final String[] STORY_DETAIL_COLUMNS = {
            CardsContract.CardEntry.TABLE_NAME + "." + CardsContract.CardEntry._ID,
            CardsContract.CardEntry.COLUMN_TITLE,
            CardsContract.CardEntry.COLUMN_CONTENT,
            CardsContract.CardEntry.COLUMN_DATE,
            CardsContract.CardEntry.COLUMN_SOURCE,
            CardsContract.CardEntry.COLUMN_BOOKMARKED,
            CardsContract.CardEntry.COLUMN_URL,
            CardsContract.CardEntry.COLUMN_TAB_NUMBER
    };

    private static int COL_STORY_ID = 0;
    private static int COL_STORY_TITLE = 1;
    private static int COL_STORY_CONTENT = 2;
    private static int COL_STORY_DATE = 3;
    private static int COL_STORY_SOURCE = 4;
    private static int COL_STORY_BOOKMARKED = 5;
    private static int COL_STORY_URL = 6;
    private static int COL_STORY_TAB_NUMBER = 7;

    private TextView mTitleView;
    private TextView mContentView;
    private TextView mDateView;
    private TextView mSourceView;
    private ImageButton mOpenBrowserButton;
    private ImageButton mBookmarkButton;
    private ImageButton mDeleteButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null ){
            mUri = args.getParcelable(DETAIL_URI);
        }

    View rootView = inflater.inflate(R.layout.story_detail_fragment, container, false);
        mTitleView = (TextView) rootView.findViewById(R.id.story_detail_title_text);
        mContentView = (TextView) rootView.findViewById(R.id.story_detail_content_text);
        mDateView = (TextView) rootView.findViewById(R.id.story_detail_date_text);
        mSourceView = (TextView) rootView.findViewById(R.id.story_detail_source_text);
        mOpenBrowserButton = (ImageButton) rootView.findViewById(R.id.story_detail_browser_icon);
        mBookmarkButton = (ImageButton) rootView.findViewById(R.id.story_detail_bookmark_icon);
        mDeleteButton = (ImageButton) rootView.findViewById(R.id.story_detail_delete_icon);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(STORY_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {

            return new CursorLoader(getActivity(),
                    mUri,
                    STORY_DETAIL_COLUMNS,
                    null,
                    null,
                    null);
        }
        return null;

    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()) {
            String title = data.getString(COL_STORY_TITLE);
            String content = data.getString(COL_STORY_CONTENT);
            final String storyurl = data.getString(COL_STORY_URL);
            String source = data.getString(COL_STORY_SOURCE);
            String date = data.getString(COL_STORY_DATE);
            String bookmarked = data.getString(COL_STORY_BOOKMARKED);

            mTitleView.setText(title);
            mContentView.setText(content);
            mSourceView.setText(source);
            mDateView.setText(date);
            setBookmarkedButtonContent(bookmarked);

            mOpenBrowserButton.setContentDescription(getResources().getString(R.string.open_story_in_browser));
            mOpenBrowserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent watchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(storyurl));
                    getContext().startActivity(watchIntent);


                }
            });
        }

        mDeleteButton.setContentDescription(getResources().getString(R.string.delete_story));

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteStory();
                }
            });
        }

    /**
     * Sets the drawable resource for the bookmark ImageButton based on the value return from DB.
     * Then changes drawable and updates DB if onClick triggered.
     * @param bookmarked
     *
     */


            public void setBookmarkedButtonContent(String bookmarked) {
            if (bookmarked.equals("0")) {
                mBookmarkButton.setContentDescription(getResources().getString(R.string.add_to_bookmarks));
                mBookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_black_48dp));

            } else if (bookmarked.equals("1")) {
                mBookmarkButton.setContentDescription(getResources().getString(R.string.remove_from_bookmarks));
                mBookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_green));
            }


            mBookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ContentValues bookmarkValue = new ContentValues();
                    //_id here seems the only means to establish a single story.
                    String mSelectectionClause = CardsContract.CardEntry._ID + " = ?";
                    String mStoryId = mUri.getPathSegments().get(1);
                    String[] selectionArgs = {mStoryId};

                    //check numbers on this. assuming 1 = bookmark and 0 = not bookmark
                    if (mBookmarked.equals("0")) {
                        bookmarkValue.put(CardsContract.CardEntry.COLUMN_BOOKMARKED, "1");
                        getContext().getContentResolver().update(mUri, bookmarkValue, mSelectectionClause, selectionArgs);
                        mBookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_green));
                        Toast.makeText(getContext(), getResources().getString(R.string.story_detail_added_to_bookmarks), Toast.LENGTH_SHORT).show();

                        mBookmarked = "1";
                        bookmarkValue.clear();

                    } else if (mBookmarked.equals("1")) {
                        bookmarkValue.put(CardsContract.CardEntry.COLUMN_BOOKMARKED, "0");
                        getContext().getContentResolver().update(mUri, bookmarkValue, mSelectectionClause, selectionArgs);
                        mBookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_black_48dp));
                        Toast.makeText(getContext(), getResources().getString(R.string.story_detail_removed_from_bookmarks), Toast.LENGTH_SHORT).show();
                        mBookmarked = "0";
                        bookmarkValue.clear();

                    }

                }
            });
        }

    /**
     * Deletes the selected story
     */


    public void deleteStory() {
        //delete story from db
        String mSelectectionClause = CardsContract.CardEntry._ID + " = ?";
        String mStoryId = mUri.getPathSegments().get(1);
        String[] selectionArgs = {mStoryId};
        getContext().getContentResolver().delete(mUri, mSelectectionClause, selectionArgs);
        Toast.makeText(getContext(), getResources().getString(R.string.story_deleted), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        return;
    }

}

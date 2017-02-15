package qorda_projects.tracktive;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.ArrayList;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import qorda_projects.tracktive.data.CardsContract;

import static android.util.Log.v;

/**
 * Created by sorengoard on 09/01/2017.
 */

public class CardFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = CardFragment.class.getSimpleName().toString();
    public final String DIALOG_TAG = "new card dialog";
    static final String CARD_URI = "cardUri";
    public static Uri mUri;
    private Uri mCardSpecificUri;
    public static int mCardPosition;

    private StoryAdapter mStoryAdapter;
    private RecyclerView mCardRecyclerView;
    private int mChoiceMode;
    private ArrayList<Story> mStories;


    private static final int STORY_LOADER = 0;



    private static final String[] STORY_COLUMNS = {
            CardsContract.CardEntry.TABLE_NAME + "." + CardsContract.CardEntry._ID,
            CardsContract.CardEntry.COLUMN_TITLE,
            CardsContract.CardEntry.COLUMN_DATE,
            CardsContract.CardEntry.COLUMN_CONTENT,
            CardsContract.CardEntry.COLUMN_SOURCE,
            CardsContract.CardEntry.COLUMN_BOOKMARKED,
            CardsContract.CardEntry.COLUMN_CARD_KEYWORDS,
            CardsContract.CardEntry.COLUMN_URL
    };

    static final int COL_CARD_ID = 0;
    static final int COL_STORY_TITLE = 1;
    static final int COL_STORY_DATE = 2;
    static final int COL_STORY_CONTENT = 3;
    static final int COL_STORY_SOURCE = 4;
    static final int COL_STORY_BOOKMARKED = 5;
    static final int COL_STORY_KEYWORDS = 6;
    static final int COL_STORY_URL = 7;


    public interface Callback {
        public void onItemSelected(Uri movieUri);
    }


    public static final CardFragment newInstance(String title, String keywords) {

        CardFragment cardFragment = new CardFragment();
        mUri = CardsContract.CardEntry.buildSingleCardUri(keywords);
        v(LOG_TAG, "uri @newInstance" + mUri);

        Bundle args = new Bundle();
        args.putParcelable("localCardUri", mUri);

        return cardFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(mStories != null) {
            Story story = mStories.get(0);
            String keywords = story.getKeywords();
            mCardSpecificUri = CardsContract.CardEntry.buildSingleCardUri(keywords);
            Log.v(LOG_TAG, "uri using mStories " + mCardSpecificUri);

        } else {
            if(mUri != null){
                mCardSpecificUri = mUri;
                Log.v(LOG_TAG, "no SIS uri, using uri from instance");
            } else {
                mCardSpecificUri = CardsContract.CardEntry.CONTENT_URI;
                Log.v(LOG_TAG, "OCV called standard URI");
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView mCardRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_cards);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mCardRecyclerView.setLayoutManager(llm);

        //Testing to see if bundle is passed from MainActivity

        //empty view
        View emptyView = rootView.findViewById(R.id.recyclerview_stories_empty);

        mCardRecyclerView.setHasFixedSize(true);

        mStoryAdapter =  new StoryAdapter(getActivity(), new StoryAdapter.StoryAdapterOnClickHandler() {
            @Override
            public void onClick(StoryAdapter.StoryAdapterViewHolder viewHolder) {

                ((Callback) getActivity()).onItemSelected(mUri);

            }
        }, emptyView, AbsListView.CHOICE_MODE_NONE);

        mCardRecyclerView.setAdapter(mStoryAdapter);

        if (savedInstanceState!= null) {
            mStoryAdapter.onRestoreInstanceState(savedInstanceState);
        }

        FabSpeedDial makeCardFab = (FabSpeedDial) rootView.findViewById(R.id.make_card_fab);
        makeCardFab.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if(id == R.id.new_card){
                    v(LOG_TAG, "add card called");
                    openNewCardDialog();
                    return true;
                }
                else if (id == R.id.diary_view) {
                    Intent diaryIntent = new Intent(getContext(), DiaryActivity.class);
                    getContext().startActivity(diaryIntent);
                    return true;
                }
                else if (id == R.id.new_diary_entry) {
//                    openNewDiaryEntryDialog
                            return true;
                }
                return super.onMenuItemSelected(menuItem);
            }

        });

        makeCardFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return  rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        getLoaderManager().initLoader(STORY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        mStoryAdapter.onSaveInstanceState(outState);
//        outState.putParcelable("localCardUri", mCardSpecificUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int it, Bundle bundle) {
        String sortOrder = CardsContract.CardEntry.COLUMN_DATE+ " ASC";
        return new CursorLoader(getActivity(),
            mCardSpecificUri,
            STORY_COLUMNS,
            null,
            null,
            sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        v(LOG_TAG, "storyAdapter count: " + mStoryAdapter.getItemCount());
        if (mStories != null) {
            mStories.clear();
        } else {
            mStories = new ArrayList<Story>();
        }
        if(cursor!= null && cursor.moveToFirst()){
            for(int i = 0;i < cursor.getCount();i++) {
                cursor.moveToPosition(i);
                String title = cursor.getString(COL_STORY_TITLE);
                String content = cursor.getString(COL_STORY_CONTENT);
                String date = cursor.getString(COL_STORY_DATE);
                String source = cursor.getString(COL_STORY_SOURCE);
                String url = cursor.getString(COL_STORY_URL);
                String bookmarked = cursor.getString(COL_STORY_BOOKMARKED);
                String keywords = cursor.getString(COL_STORY_KEYWORDS);

                Story story = new Story(title, content, date, source, url, bookmarked, keywords);
                mStories.add(story);
            }
            Log.v("LOG_TAG", "mStories in OLF: " + mStories);
        }


        mStoryAdapter.swapCursor(cursor);

//        updateEmptyView(0;
        if( cursor.getCount() == 0) {
            getActivity().supportStartPostponedEnterTransition();

        } else {
            //loop through existing stories
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Bundle args = new Bundle();
//        args.putParcelable("localCardUri", mCardSpecificUri);
//        onSaveInstanceState(args);
        if(null != mCardRecyclerView) {
            mCardRecyclerView.clearOnScrollListeners();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {mStoryAdapter.swapCursor(null);}

    public void openNewCardDialog() {
        DialogFragment newCardDialog = new KeywordsEntryDialog();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        newCardDialog.show(manager, DIALOG_TAG);

    }

}

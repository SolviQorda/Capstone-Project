package qorda_projects.tracktive;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import qorda_projects.tracktive.data.CardsContract;

/**
 * Created by sorengoard on 09/01/2017.
 */

public class CardFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = CardFragment.class.getSimpleName().toString();
    public final String DIALOG_TAG = "new card dialog";
    static final String CARD_URI = "URI";
    private Uri mUri;

    private StoryAdapter mStoryAdapter;
    private RecyclerView mCardRecyclerView;
    private int mChoiceMode;

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

//    @Override
//    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
//        super.onInflate(activity, attrs, savedInstanceState);
//        TypedArray a = activity.obtainStyledAttributes(attrs, R.styleable.CardFragment, 0, 0);
//    }
//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            int cardPosition = savedInstanceState.getInt("cardPosition");
            Log.v(LOG_TAG, "card position from bundle: " + cardPosition);
            String keywords = Utility.getKeywordsFromElementNumber(cardPosition, getContext());
            mUri = CardsContract.CardEntry.buildSingleCardUri(keywords);
        } else {
            mUri = CardsContract.CardEntry.CONTENT_URI;
        }




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

//                ((Callback) getActivity()).onItemSelected(viewHolder);

            }
        }, emptyView, AbsListView.CHOICE_MODE_NONE);

        mCardRecyclerView.setAdapter(mStoryAdapter);

        if (savedInstanceState!= null) {
            mStoryAdapter.onRestoreInstanceState(savedInstanceState);
        }

        FloatingActionButton makeCardFab = (FloatingActionButton) rootView.findViewById(R.id.make_card_fab);
        makeCardFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "OnClick called");
                openNewCardDialog();
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
        mStoryAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int it, Bundle bundle) {
        String sortOrder = CardsContract.CardEntry.COLUMN_DATE+ " ASC";
//        Uri cardsForKeywordUri;
//        if(bundle != null) {
//            int cardPosition = bundle.getInt("cardPosition");
//            Log.v(LOG_TAG, "bundle int value in onCreateLoader" + cardPosition);
//            String keywords = Utility.getKeywordsFromElementNumber(cardPosition, getContext());
//            Log.v(LOG_TAG, "string taken from keywods in OCL: " + keywords);
//
//             cardsForKeywordUri = CardsContract.CardEntry.buildSingleCardUri(keywords);
//            Log.v(LOG_TAG, "cards Uri:" + cardsForKeywordUri);
//        } else {
//            cardsForKeywordUri = CardsContract.CardEntry.CONTENT_URI;
//        }

        return new CursorLoader(getActivity(),
            mUri,
            STORY_COLUMNS,
            null,
            null,
            sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.v(LOG_TAG, "storyAdapter count: " + mStoryAdapter.getItemCount());
        mStoryAdapter.swapCursor(cursor);

//        updateEmptyView(0;
        if( cursor.getCount() == 0) {
            getActivity().supportStartPostponedEnterTransition();

        } else {
            //see Sunshine for the other option -- would be good to understand what it does
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

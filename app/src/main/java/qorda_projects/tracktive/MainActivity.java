package qorda_projects.tracktive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import qorda_projects.tracktive.data.CardsContract;
import qorda_projects.tracktive.sync.TracktiveSyncAdapter;

public class MainActivity extends AppCompatActivity implements KeywordsEntryDialog.keywordsDialogListener, LoaderManager.LoaderCallbacks<Cursor>, CardFragment.Callback {

    public final String DIALOG_TAG = "new card dialog";
    public final String DETAIL_URI = "detailUri";
    private final String LOG_TAG = MainActivity.class.getSimpleName().toString();
    private static final String STORYDETAILFRAGMENT_TAG = "SDFTAG";
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private boolean mTwoPane;
    private SharedPreferences mSharedPreferences;
    private ArrayList<Card> mTitlesAndKeywords;
    private List<CardFragment> mFragments;
    private ArrayList<Story> mStories;
    private Uri mUri;

    private static final int STORY_LOADER = 0;

    private static final String[] STORY_COLUMNS = {
            CardsContract.CardEntry.TABLE_NAME + "." + CardsContract.CardEntry._ID,
            CardsContract.CardEntry.COLUMN_TITLE,
            CardsContract.CardEntry.COLUMN_DATE,
            CardsContract.CardEntry.COLUMN_CONTENT,
            CardsContract.CardEntry.COLUMN_SOURCE,
            CardsContract.CardEntry.COLUMN_BOOKMARKED,
            CardsContract.CardEntry.COLUMN_CARD_KEYWORDS,
            CardsContract.CardEntry.COLUMN_URL,
            CardsContract.CardEntry.COLUMN_TAB_NUMBER
    };

    static final int COL_CARD_ID = 0;
    static final int COL_STORY_TITLE = 1;
    static final int COL_STORY_DATE = 2;
    static final int COL_STORY_CONTENT = 3;
    static final int COL_STORY_SOURCE = 4;
    static final int COL_STORY_BOOKMARKED = 5;
    static final int COL_STORY_KEYWORDS = 6;
    static final int COL_STORY_URL = 7;
    static final int COL_STORY_TAB_NUMBER = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyApplication) getApplication()).startTracking();

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        getSupportLoaderManager().initLoader(0, null, this);

        //Get contentUri from an intent if it exists
        Uri contentUri = getIntent() != null ? getIntent().getData() : null;

        //If no pre-existing data then need to open up the dialog.
       ArrayList<Card> existingCardDetails = getExistingCardDetails(this);

        if (existingCardDetails == null) {
            DialogFragment newCardDialog = new KeywordsEntryDialog();
            FragmentManager manager = this.getSupportFragmentManager();
            newCardDialog.show(manager, DIALOG_TAG);
        }

        //handle whether to go to 2-pane mode or not here
        if(findViewById(R.id.story_detail_container) != null) {
            //if thsi view is present, then activity should be in 2-pane mode
            mTwoPane = true;
//        if (savedInstanceState == null) {
//            StoryDetailFragment detailFragment = new StoryDetailFragment();
//            if (contentUri != null) {
//                Bundle uriArgs = new Bundle();
//                uriArgs.putParcelable(DETAIL_URI, contentUri);
//                detailFragment.setArguments(uriArgs);
//
//            }
//            getSupportFragmentManager().beginTransaction()
//                .replace(R.id.story_detail_container, detailFragment, STORYDETAILFRAGMENT_TAG);
        } else {
            mTwoPane = false;
        }


        mTabLayout = getTabLayout();
        mViewPager = getViewPager();

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(
                mTabLayout));
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        //cycle through card titles (for tabs).

if (existingCardDetails != null ) {
    for (int i = 0; i < existingCardDetails.size(); i++) {
        Card card = existingCardDetails.get(i);
        String tabTitle = card.getTitle();
        addTab(tabTitle);
    }
}
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //need to deliver this to the card fragment, how do I set it?

                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}

        });

        if (existingCardDetails != null) {

            TracktiveSyncAdapter.initializeSyncAdapter(this);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //Content Loader classes


    @Override
    public Loader<Cursor> onCreateLoader(int it, Bundle bundle) {
        String sortOrder = CardsContract.CardEntry.COLUMN_DATE+ " ASC";
        Uri uri = CardsContract.CardEntry.CONTENT_URI;
        return new CursorLoader(this,
                uri,
                STORY_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        ArrayList<Story> stories = new ArrayList<Story>();

        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                String title = cursor.getString(COL_STORY_TITLE);
                String content = cursor.getString(COL_STORY_CONTENT);
                String date = cursor.getString(COL_STORY_DATE);
                String source = cursor.getString(COL_STORY_SOURCE);
                String url = cursor.getString(COL_STORY_URL);
                String bookmarked = cursor.getString(COL_STORY_BOOKMARKED);
                String keywords = cursor.getString(COL_STORY_KEYWORDS);
                int tabNumber = cursor.getInt(COL_STORY_TAB_NUMBER);
                int dbId = cursor.getInt(COL_CARD_ID);

                Story story = new Story(title, content, date, source, url, bookmarked, keywords, tabNumber, dbId);
                //TODO: don't update instance variable here
                stories.add(story);
            }

            if (stories != null) {
                ArrayList<Card> existingCards = getExistingCardDetails(this);
                ArrayList<CardFragment> fragments = updateCardFragments(existingCards, stories);
                setPagerAdapter(fragments);

                Log.v("LOG_TAG", "mStories in OLF: " + stories);
            }

            if (stories == null) {
                TracktiveSyncAdapter.syncImmediately(this);
            }

            if (cursor.getCount() == 0) {
                this.supportStartPostponedEnterTransition();

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        mStoryAdapter.swapCursor(null);
    }

    public void initLoaderForNewCard() {

        getSupportLoaderManager().initLoader(0, null, this);
        //recreating the activity for the new card.
    }

    private void setPagerAdapter(ArrayList<CardFragment> fragments) {
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_pager);
        PagerAdapter pagerAdapter = new CardPagerAdapter(
                    getSupportFragmentManager(), fragments);
            viewPager.setAdapter(pagerAdapter);

    }

    private void addTab(String title) {
        TabLayout tabLayout = getTabLayout();
        tabLayout.addTab(tabLayout.newTab().setText(title), true);
        ViewPager viewPager = getViewPager();
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setContentDescription(title);

    }

    private TabLayout getTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        return tabLayout;
    }

    private ViewPager getViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_pager);
        return viewPager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<CardFragment> updateCardFragments(ArrayList<Card> cardDetails, ArrayList<Story> stories) {
        clearTabs();
        ArrayList<CardFragment> cardFragmentList = new ArrayList<CardFragment>();

        if (cardDetails != null) {
            Log.v(LOG_TAG, "cardDetailsArray contains: " + cardDetails);
            for (int i = 0; i < cardDetails.size(); i++) {
                Card card = cardDetails.get(i);
                String cardTitle = card.getTitle();
                String cardKeywords = card.getKeywords();
                Log.v(LOG_TAG, "at position " + i + " cardTitle: " + cardTitle);
                //add tab
                addTab(cardTitle);
                // get relevant story data
                Bundle args  = new Bundle();
                ArrayList<Story> storiesForFragment = getStoriesForTabNumber(i, stories);

                if(storiesForFragment!=null) {
                    args.putParcelableArrayList("cardStoriesArrayList", storiesForFragment);
                }
                    // add cardFragment to list of fragments
                CardFragment cardFragment = CardFragment.newInstance(cardTitle, cardKeywords);
                cardFragment.setArguments(args);
                cardFragmentList.add(cardFragment);
            }
        }
        return cardFragmentList;
    }

    private ArrayList<Story> getStoriesForTabNumber(int tabNumber, ArrayList<Story> allStories) {
        ArrayList<Story> storiesForFragment = new ArrayList<Story>();
        if (allStories!=null) {
            for (int q = 0; q < allStories.size(); q++) {
                Story story = allStories.get(q);
                int storyTabNumber = story.getTabNumber();
                if (storyTabNumber == tabNumber) {
                    storiesForFragment.add(story);
                }
            }
        }
        return storiesForFragment;
    }
    private void clearTabs() {
        mTabLayout.removeAllTabs();
    }

    public static ArrayList<Card> getExistingCardDetails(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String existingCardJson = sharedPreferences.getString(context.getString(R.string.pref_card_titles_key), null);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        //Convert JSON to Cards and add new card

        ArrayList<Card> mTitlesAndKeywords = (ArrayList<Card>) gson.fromJson(existingCardJson, new TypeToken<ArrayList<Card>>(){}.getType());
        return mTitlesAndKeywords;
    }

    @Override
    public void onItemSelected(Uri singleStoryUri, StoryAdapter.StoryAdapterViewHolder vh) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            Log.v(LOG_TAG, "mTwoPane: " + mTwoPane);

            args.putParcelable(DETAIL_URI, singleStoryUri);

            StoryDetailFragment fragment = new StoryDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.story_detail_container, fragment, STORYDETAILFRAGMENT_TAG)
                    .commit();
        } else {

            Log.v(LOG_TAG, "single story uri in MA: " + singleStoryUri);
            Intent intent = new Intent(this, StoryDetailActivity.class)
                    .setData(singleStoryUri);
            startActivity(intent);
        }
    }


}
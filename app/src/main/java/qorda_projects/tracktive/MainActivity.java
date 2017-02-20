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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import qorda_projects.tracktive.data.CardsContract;
import qorda_projects.tracktive.sync.TracktiveSyncAdapter;

public class MainActivity extends AppCompatActivity implements KeywordsEntryDialog.keywordsDialogListener, LoaderManager.LoaderCallbacks<Cursor> {

    public final String DIALOG_TAG = "new card dialog";
    private final String LOG_TAG = MainActivity.class.getSimpleName().toString();
    private ViewPager mViewPager;
    public PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private SharedPreferences mSharedPreferences;
    public ArrayList<Card> mTitlesAndKeywords;
    public List<CardFragment> mFragments;
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

    public interface Callback {
        public void onItemSelected(Uri movieUri);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyApplication) getApplication()).startTracking();

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mUri = CardsContract.CardEntry.CONTENT_URI;

        getSupportLoaderManager().initLoader(0, null, this);


        //If no pre-existing data then need to open up the dialog.
       mTitlesAndKeywords = getExistingCardDetails(this);

        if (mTitlesAndKeywords == null) {
            mFragments = new ArrayList<CardFragment>();
            DialogFragment newCardDialog = new KeywordsEntryDialog();
            FragmentManager manager = this.getSupportFragmentManager();
            newCardDialog.show(manager, DIALOG_TAG);
        }



        //handle whether to go to 2-pane mode or not here
//        if (savedInstanceState == null) {
//            CardFragment cardFragment = new CardFragment();
//            if (cardUri != null) {
//                Bundle uriArgs = new Bundle();
//                uriArgs.putParcelable(CardFragment.CARD_URI, cardUri);
//                cardFragment.setArguments(uriArgs);
//            }
//            getSupportFragmentManager().beginTransaction()
//        }
//        }
//        CardFragment cardFragment = ((CardFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.stories_fragment));


        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);

        mViewPager = (ViewPager) findViewById(R.id.main_pager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


        //cycle through card titles (for tabs).

if (mTitlesAndKeywords != null ) {
    for (int i = 0; i < mTitlesAndKeywords.size(); i++) {
        Card card = mTitlesAndKeywords.get(i);
        String tabTitle = card.getTitle();
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setContentDescription(tabTitle);

//        Log.v(LOG_TAG, "bundle: " + titlePosition);
//                cardFragment.onCreateLoader(0, titlePosition);
    }



}


        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //need to deliver this to the card fragment, how do I set it?

                mViewPager.setCurrentItem(tab.getPosition());
//                int tabNumber = tab.getPosition();
//                ArrayList<Story> storiesForFragment = new ArrayList<Story>();
//                for(int i = 0; i <mStories.size(); i++) {
//                    Story story = mStories.get(i);
//                    int storyTabNumber = story.getTabNumber();
//                    if(storyTabNumber == tabNumber) {
//                        storiesForFragment.add(story);
//                    }
//                    CardFragment cardFragment = mFragments.get(tabNumber);
//                    Bundle args = new Bundle();
//                    args.putParcelableArrayList("cardStoriesArrayList", storiesForFragment);
//                    cardFragment.setArguments(args);


//                }
                //set cardFragment with bundle arrayList


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


        if (mTitlesAndKeywords != null) {

            TracktiveSyncAdapter.initializeSyncAdapter(this);

            TracktiveSyncAdapter.syncImmediately(this);
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

    public void onItemSelected(Uri contentUri) {
//        if(mTwoPane) {
//            Bundle args = new Bundle();
//
//            args.putParcelable("URI", contentUri);
//
//            DetailFragment fragment = new DetailFragment();
//            fragment.setArguments(args);
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
//                    .commit();
//        } else {
            Intent intent = new Intent(this, StoryDetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
//        }
    }

    //Content Loader classes


    @Override
    public Loader<Cursor> onCreateLoader(int it, Bundle bundle) {
        String sortOrder = CardsContract.CardEntry.COLUMN_DATE+ " ASC";
        return new CursorLoader(this,
                mUri,
                STORY_COLUMNS,
                null,
                null,
                sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
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
                int tabNumber = cursor.getInt(COL_STORY_TAB_NUMBER);

                Story story = new Story(title, content, date, source, url, bookmarked, keywords, tabNumber);
                mStories.add(story);
            }
            if(mStories!= null) {
                getCardFragments();

            }
            if(mPagerAdapter == null) {
                mPagerAdapter = new CardPagerAdapter(
                        getSupportFragmentManager(), mFragments);
            }
            mViewPager.setAdapter(mPagerAdapter);

            Log.v("LOG_TAG", "mStories in OLF: " + mStories);
        }


//        mStoryAdapter.swapCursor(cursor);
        if( cursor.getCount() == 0) {
            this.supportStartPostponedEnterTransition();

        } else {
            //loop through existing stories
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        mStoryAdapter.swapCursor(null);
    }




    public void addCard(ArrayList<Card> cardDetailsArrayList, Uri cardUri) {

        Bundle args = new Bundle();
        getSupportLoaderManager().initLoader(0, null, this);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mViewPager = (ViewPager) findViewById(R.id.main_pager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        int cardPosition = mTabLayout.getSelectedTabPosition();
        Log.v(LOG_TAG, "cardPosition" + cardPosition);

        mTitlesAndKeywords = cardDetailsArrayList;

        int newCardPosition = mTitlesAndKeywords.size() - 1;
        Log.v(LOG_TAG, "cardDetailsArrayList in AddCard is: " + mTitlesAndKeywords);

        Card card = mTitlesAndKeywords.get(newCardPosition);
        String cardKeywords = card.getKeywords();
        String tabTitle = card.getTitle();
        Log.v(LOG_TAG, "new card Position for " + tabTitle + " is " + newCardPosition);

        if(mPagerAdapter != null) {
            mPagerAdapter.notifyDataSetChanged();
        }

        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle), true);
        if(mPagerAdapter != null) {
            mPagerAdapter.notifyDataSetChanged();

        }
        CardFragment cardFragment = CardFragment.newInstance(tabTitle, cardKeywords);

        //add mStories as an arrayList parcelable

        ArrayList<Story> storiesForFragment = new ArrayList<Story>();
        if (mStories!=null) {
            for (int q = 0; q < mStories.size(); q++) {
                Story story = mStories.get(q);
                int storyTabNumber = story.getTabNumber();
                if (storyTabNumber == newCardPosition) {
                    storiesForFragment.add(story);
                }
            }
        }
        if(storiesForFragment!=null) {
            args.putParcelableArrayList("cardStoriesArrayList", storiesForFragment);
        }

        cardFragment.setArguments(args);

        mFragments.add(cardFragment);

        if(mPagerAdapter != null) {
            mPagerAdapter.notifyDataSetChanged();
            Log.v(LOG_TAG, "dataset notified of change");

        }

        Toast.makeText(this, "Card made successfully!", Toast.LENGTH_LONG).show();
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

    private void getCardFragments() {
        List<CardFragment> cardFragmentList = new ArrayList<CardFragment>();

        mTitlesAndKeywords = getExistingCardDetails(this);
        if (mTitlesAndKeywords != null) {
            Log.v(LOG_TAG, "cardDetailsArray contains: " + mTitlesAndKeywords);
            for (int i = 0; i < mTitlesAndKeywords.size(); i++) {
                Card card = mTitlesAndKeywords.get(i);
                String cardTitle = card.getTitle();
                //refactor into
                String cardKeywords = card.getKeywords();
                Log.v(LOG_TAG, "at position " + i + " cardTitle: " + cardTitle);
                // get relevant story data
                ArrayList<Story> storiesForFragment = new ArrayList<Story>();
                if (mStories!=null) {
                    for (int q = 0; q < mStories.size(); q++) {
                        Story story = mStories.get(q);
                        int storyTabNumber = story.getTabNumber();
                        if (storyTabNumber == i) {
                            storiesForFragment.add(story);
                        }
                    }
                }
                 Bundle args  = new Bundle();
                if(storiesForFragment!=null) {
                    args.putParcelableArrayList("cardStoriesArrayList", storiesForFragment);
                }

                    // add cardFragment to list of fragments
                CardFragment cardFragment = CardFragment.newInstance(cardTitle, cardKeywords);
                cardFragment.setArguments(args);
                cardFragmentList.add(cardFragment);



                //TODO: is this where to handle a loader? Want to loop through.

                mFragments = cardFragmentList;
            }
//            return cardFragmentList;
//        } else {
//            return null;
        }

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

    public static Uri getcardUriFromFragmentPosition(int position, Context context) {

        ArrayList<Card> titlesAndKeywords = getExistingCardDetails(context);
        Card card = titlesAndKeywords.get(position);
        String keywords = card.getKeywords();
        Uri uriFromKeywords = CardsContract.CardEntry.buildSingleCardUri(keywords);
        Log.v("utility", "Uri From fragment in position " + position + " is " + uriFromKeywords);
        return uriFromKeywords;
    }


}
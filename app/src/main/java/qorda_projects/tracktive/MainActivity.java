package qorda_projects.tracktive;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import qorda_projects.tracktive.sync.TracktiveSyncAdapter;

public class MainActivity extends AppCompatActivity implements KeywordsEntryDialog.keywordsDialogListener {

    public final String DIALOG_TAG = "new card dialog";
    private final String LOG_TAG = MainActivity.class.getSimpleName().toString();
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private SharedPreferences mSharedPreferences;
    private ArrayList<String> mTitleArrayList;
    private ArrayList<String> mKeywordArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Uri cardUri = getIntent() != null ? getIntent().getData() : null;

        getExistingTitles();
        getExistingKeywords();
        List<Fragment> fragments = getCardFragments();

        if (fragments == null) {
            //If no pre-existing data then need to open up the dialog.
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
//          TODO: implement a set for titles and in the onClick

        mViewPager = (ViewPager) findViewById(R.id.main_pager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        mPagerAdapter = new CardPagerAdapter(
                getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mPagerAdapter);


        //cycle through card titles (for tabs).
if (mTitleArrayList != null ) {
    for (int i = 0; i < mTitleArrayList.size(); i++) {
        String tabTitle = mTitleArrayList.get(i).toString();
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setContentDescription(tabTitle);
        Bundle titlePosition = new Bundle();
        titlePosition.putInt("cardPosition", i);
        Log.v(LOG_TAG, "bundle: " + titlePosition);
//                cardFragment.onCreateLoader(0, titlePosition);
    }

}


        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


        if (fragments != null) {
            TracktiveSyncAdapter.initializeSyncAdapter(this);

            TracktiveSyncAdapter.syncImmediately(this);
        }

    }


    public void addCard(Uri cardUri) {

        Bundle args = new Bundle();
        args.putParcelable("URI", cardUri);
        Log.v(LOG_TAG, "Card uri when added:" + cardUri);

        CardFragment cardFragment = new CardFragment();

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mViewPager = (ViewPager) findViewById(R.id.main_pager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        int cardPosition = mTabLayout.getSelectedTabPosition();
        Log.v(LOG_TAG, "cardPosition" + cardPosition);
        args.putInt("position", cardPosition + 1);

        if (mTitleArrayList == null) {
            mTitleArrayList = new ArrayList<String>();
        }

        //Hasn't add all been performed already? if so would suggest that this line of code would duplicate.
        mTitleArrayList.addAll(mSharedPreferences.getStringSet(getResources().getString(R.string.pref_card_titles_key), null));
        int newCardPosition = 0;
        if (cardPosition > 0) {
            newCardPosition = cardPosition + 1;
        }
        String cardKeywords = mKeywordArrayList.get(newCardPosition);
        String tabTitle = mTitleArrayList.get(newCardPosition);
        Log.v(LOG_TAG, "new card Position for " + tabTitle + " is " +newCardPosition);

        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle), newCardPosition, true);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        List<Fragment> cardFragmentList = getCardFragments();

        cardFragmentList.add(CardFragment.newInstance(tabTitle, cardKeywords));


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

    private List<Fragment> getCardFragments() {
        List<Fragment> cardFragmentList = new ArrayList<Fragment>();

        mKeywordArrayList = getExistingKeywords();
        if (mTitleArrayList != null) {
            Log.v(LOG_TAG, "TitleArray contains: " + mTitleArrayList + "keywordsArray contains: " + mKeywordArrayList);
            Log.v(LOG_TAG, "titleArray size is: " + mTitleArrayList.size() + "keywordArray size is: " + mKeywordArrayList.size());
            for (int i = 0; i < mTitleArrayList.size(); i++) {
                String cardTitle = mTitleArrayList.get(i);
                //refactor into
                String cardKeywords = "";
                if(i < mKeywordArrayList.size() ) {
                    cardKeywords = mKeywordArrayList.get(i);
                } else {
                    cardKeywords = "keywords error";
                }
                Log.v(LOG_TAG, "at position " + i + " cardTitle: " + cardTitle);
                cardFragmentList.add(CardFragment.newInstance(cardTitle, cardKeywords));
            }
            return cardFragmentList;
        } else {
            return null;
        }

    }

    private ArrayList<String> getExistingTitles() {
        Set<String> titleSet = mSharedPreferences.getStringSet(getResources().getString(R.string.pref_card_titles_key), null);
        if (titleSet != null) {
            mTitleArrayList = new ArrayList<String>();
            mTitleArrayList.addAll(titleSet);
            return mTitleArrayList;
        } else {
            return null;
        }

    }

    private ArrayList<String> getExistingKeywords() {
        Set<String> keywordSet = mSharedPreferences.getStringSet(getResources().getString(R.string.pref_keywords_key), null);
        //Cast Set to arrayList to iterate
        if (keywordSet != null) {
            mKeywordArrayList = new ArrayList<String>();
            mKeywordArrayList.addAll(keywordSet);
            return mKeywordArrayList;
        } else {
            ArrayList<String> keywordArrayList = new ArrayList<String>();
            keywordArrayList.add("");
            return keywordArrayList;
        }
    }

}
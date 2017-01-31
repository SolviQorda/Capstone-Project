package qorda_projects.tracktive;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import qorda_projects.tracktive.sync.TracktiveSyncAdapter;

public class MainActivity extends AppCompatActivity implements KeywordsEntryDialog.keywordsDialogListener{

    public final String DIALOG_TAG = "new card dialog";
    private final String LOG_TAG = MainActivity.class.getSimpleName().toString();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        Uri cardUri = getIntent() != null ? getIntent().getData() : null;

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
        CardFragment cardFragment = ((CardFragment)getSupportFragmentManager()
        .findFragmentById(R.id.stories_fragment));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
//          TODO: implement a set for titles and in the onClick
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.main_pager);
        final PagerAdapter adapter = new CardPagerAdapter(
                getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


        Set<String> titleSet = sharedPrefs.getStringSet(getResources().getString(R.string.pref_card_titles_key), null);

        //Cast Set to arrayList to iterate
        ArrayList<String> titleArrayList = new ArrayList<String>();

        if(titleSet == null) {
            //If no pre-existing data then need to open up the dialog.
            //TODO: what comes after this? Really we need to call onCreate again?
            // TODO: Maybe need to open dialog before calling the adapter.
            DialogFragment newCardDialog = new KeywordsEntryDialog();
            FragmentManager manager = this.getSupportFragmentManager();
            newCardDialog.show(manager, DIALOG_TAG);
        }

        //Hacked solution --. will need to refactor
        if (titleSet != null){
            titleArrayList.addAll(titleSet);
        } else {
            titleArrayList.add("");
        }


            //TODO: find a way to cycle through card titles.

            for (int i = 0; i < titleArrayList.size(); i++) {
                String tabTitle = titleArrayList.get(i).toString();
                tabLayout.addTab(tabLayout.newTab().setText(tabTitle));
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                tabLayout.setContentDescription(tabTitle);
                Bundle titlePosition = new Bundle();
                titlePosition.putInt("cardPosition", i);
            }

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }

            });

            TracktiveSyncAdapter.initializeSyncAdapter(this);


    }


    public void addCard() {
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
        if(id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

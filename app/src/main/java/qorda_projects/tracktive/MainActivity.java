package qorda_projects.tracktive;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

        //handle whether to go to 2-pane mode or not here
        CardFragment cardFragment = ((CardFragment)getSupportFragmentManager()
        .findFragmentById(R.id.stories_fragment));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
//          TODO: implement a set for titles and in the onClick
        SharedPreferences sharedPrefs = this.getSharedPreferences(getResources().getString(R.string.pref_card_titles_label), Context.MODE_PRIVATE);

        Set<String> keywordSet = sharedPrefs.getStringSet(getResources().getString(R.string.pref_card_titles_key), null);
        //Cast Set to arrayList to iterate
        ArrayList<String> keywordsArrayList = new ArrayList<String>();

        if(keywordSet != null) {
            keywordsArrayList.addAll(keywordSet);

            //TODO: find a way to cycle through card titles.

            for (int i = 0; i < keywordSet.size(); i++) {
                String tabTitle = keywordsArrayList.get(i).toString();
                tabLayout.addTab(tabLayout.newTab().setText(tabTitle));
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            }

            final ViewPager viewPager = (ViewPager) findViewById(R.id.main_pager);
            final PagerAdapter adapter = new CardPagerAdapter(
                    getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
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
        } else {
            //If no pre-existing data then need to open up the dialog.
            //TODO: what comes after this? Really we need to call onCreate again?
            // TODO: Maybe need to open dialog before calling the adapter.
            DialogFragment newCardDialog = new KeywordsEntryDialog();
            FragmentManager manager = this.getSupportFragmentManager();
            newCardDialog.show(manager, DIALOG_TAG);
        }

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

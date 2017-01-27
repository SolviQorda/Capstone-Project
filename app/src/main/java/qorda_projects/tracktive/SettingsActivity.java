package qorda_projects.tracktive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by sorengoard on 24/01/2017.
 */

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_keywords_key)));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {

        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference,
                PreferenceManager
        .getDefaultSharedPreferences(preference.getContext())
        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {

        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if(prefIndex >= 0)
            {
                preference.setSummary(listPreference.getEntries() [prefIndex]);
            }
            else {
                preference.setSummary(stringValue);
            }
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)  {
            if (key.equals(getString(R.string.pref_keywords_key))) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //need to edit pre-existing set. can you edit a set in a textEdit?
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


}

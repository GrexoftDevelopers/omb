package com.oneminutebefore.workout;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.oneminutebefore.workout.helpers.IntentUtils;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
//
//    /**
//     * Helper method to determine if the device has an extra-large screen. For
//     * example, 10" tablets are extra-large.
//     */
//    private static boolean isXLargeTablet(Context context) {
//        return (context.getResources().getConfiguration().screenLayout
//                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
//    }
//
//    /**
//     * Binds a preference's summary to its value. More specifically, when the
//     * preference's value is changed, its summary (line of text below the
//     * preference title) is updated to reflect the value. The summary is also
//     * immediately updated upon calling this method. The exact display format is
//     * dependent on the type of preference.
//     *
//     * @see #sBindPreferenceSummaryToValueListener
//     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public boolean onIsMultiPane() {
//        return isXLargeTablet(this);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public void onBuildHeaders(List<Header> target) {
//        loadHeadersFromResource(R.xml.pref_headers, target);
//    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
//    protected boolean isValidFragment(String fragmentName) {
//        return PreferenceFragment.class.getName().equals(fragmentName)
//                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
//    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_workout_time);
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_00_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_01_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_02_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_03_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_04_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_05_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_06_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_07_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_08_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_09_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_10_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_11_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_12_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_13_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_14_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_15_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_16_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_17_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_18_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_19_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_20_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_21_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_22_59)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.list_key_13_59)));
            setHasOptionsMenu(true);

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        IntentUtils.scheduleWorkoutNotifications(SettingsActivity.this);
        super.onDestroy();
    }
}

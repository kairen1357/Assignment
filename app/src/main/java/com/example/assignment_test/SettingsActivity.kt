package com.example.assignment_test

import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.assignment_test.databinding.SettingsActivityBinding


class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener{
            onBackPressed()
        }

    }


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val fragmentBottomsheetcontact = Fragment_bottom_sheet_contact()
            val contactUs = findPreference<Preference>("contactUs")
            contactUs?.setOnPreferenceClickListener {
                fragmentBottomsheetcontact.show(requireFragmentManager(), "BottomSheetDialog")
                true // return true to indicate that the click event was consumed
            }

            val darktheme = findPreference<SwitchPreferenceCompat>("theme")
            darktheme?.setOnPreferenceClickListener {
                val currentTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                val newTheme = if (currentTheme == Configuration.UI_MODE_NIGHT_NO) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                AppCompatDelegate.setDefaultNightMode(newTheme)

                // Recreate the activity or fragment to apply the new theme
                if (context is AppCompatActivity) {
                    (context as AppCompatActivity).recreate()
                }
                true
            }

        }
    }
}
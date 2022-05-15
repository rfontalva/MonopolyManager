package com.example.monopolymanager

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.monopolymanager.utils.switchLocal


private var PREF_NAME = "MONOPOLY"

class Settings : AppCompatActivity() {
    var activity = this
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
    }

    class SettingsFragment() : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val sharedPref: SharedPreferences =
                requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val stayLoggedPref = sharedPref.getBoolean("stayLoggedIn", false)
            var languagePref = sharedPref.getString("language", "es")

            val stayLoggedIn = findPreference<SwitchPreferenceCompat>("stayLoggedIn")
            stayLoggedIn?.isChecked = stayLoggedPref
//            stayLoggedIn?.setDefaultValue(stayLoggedPref)
            stayLoggedIn?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val editor = sharedPref.edit()
                    editor.putBoolean("stayLoggedIn", newValue as Boolean)
                    editor.apply()
                    true
                }

            val language = findPreference<ListPreference>("language")
            if (languagePref == "es")
                language?.value = "es"
            else if (languagePref == "en")
                language?.value = "en"

            language?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    languagePref = newValue as String
                    val editor = sharedPref.edit()
                    editor.putString("language", newValue as String)
                    editor.apply()
                    switchLocal(requireContext(), languagePref!!, requireActivity())
                    true
                }
        }
    }
}
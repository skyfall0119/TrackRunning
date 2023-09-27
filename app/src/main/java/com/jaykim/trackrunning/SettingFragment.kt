package com.jaykim.trackrunning


import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager



class SettingFragment : PreferenceFragmentCompat() {
    lateinit var pref : SharedPreferences
    lateinit var prefListener : SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)

        pref = PreferenceManager.getDefaultSharedPreferences(requireActivity())

        prefListener = SharedPreferences.OnSharedPreferenceChangeListener{
                sharedPreferences: SharedPreferences?, key: String? ->

            when (key){
                "language" -> {
                    when (pref.getString("language","")){

                        "한국어" ->
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags("ko-rKR")
                            )



                        "English" ->
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags("en")
                            )

                    }
                }
            }
        }


        pref.registerOnSharedPreferenceChangeListener(prefListener)

    }


    override fun onDestroy() {
        super.onDestroy()
        pref.unregisterOnSharedPreferenceChangeListener(prefListener)
    }

}
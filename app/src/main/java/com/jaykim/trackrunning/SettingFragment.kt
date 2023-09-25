package com.jaykim.trackrunning

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast

import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import java.util.Locale
import kotlin.system.exitProcess


class SettingFragment : PreferenceFragmentCompat() {
    lateinit var configuration: Configuration
    lateinit var pref : SharedPreferences
    lateinit var prefListener : SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
        configuration = Configuration()
        pref = PreferenceManager.getDefaultSharedPreferences(requireActivity())
//        val lan = pref.getString("language","")
//        when (lan){
//            "English" -> {
//                Toast.makeText(requireContext(),"English",Toast.LENGTH_SHORT).show()
//                configuration.setLocale(Locale.KOREA)
//
//            }
//            "Korean" -> {
//                Toast.makeText(requireContext(),"Korean",Toast.LENGTH_SHORT).show()
//                configuration.setLocale(Locale.US)
//
//            }
//        }
//        resources.updateConfiguration(configuration,resources.displayMetrics)

        prefListener = SharedPreferences.OnSharedPreferenceChangeListener{
                sharedPreferences: SharedPreferences?, key: String? ->
            when (key){
                "language" -> {
                    val lan = pref.getString("language","")
                    when (lan){
                        "English" -> {
//                            Toast.makeText(requireContext(),"English",Toast.LENGTH_SHORT).show()
                            configuration.setLocale(Locale.KOREA)

                        }
                        "Korean" -> {
//                            Toast.makeText(requireContext(),"Korean",Toast.LENGTH_SHORT).show()
                            configuration.setLocale(Locale.US)

                        }
                    }
                    Toast.makeText(requireContext(),"Restart the apply the change",Toast.LENGTH_SHORT).show()
                    resources.updateConfiguration(Configuration(),resources.displayMetrics)
                }
            }


        }
    }

    override fun onResume() {
        super.onResume()
        pref.registerOnSharedPreferenceChangeListener(prefListener)
    }

    override fun onPause() {
        super.onPause()
        pref.unregisterOnSharedPreferenceChangeListener(prefListener)
    }






}
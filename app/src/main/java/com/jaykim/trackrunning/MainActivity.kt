package com.jaykim.trackrunning

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.jaykim.trackrunning.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityMainBinding
    private lateinit var drawerLayout : DrawerLayout
    private var isBackPressedOnce = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        Locale.getDefault().displayLanguage

        initNav(savedInstanceState)
        initAd()




    }

    private fun initAd() {
        MobileAds.initialize(this) {}
        val mAdView = findViewById<AdView>(R.id.ad_banner)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

    }


    private fun initNav(savedInstanceState: Bundle?) {
        drawerLayout =  binding.drawerLayout
        val navigationView = binding.navView

        setSupportActionBar(binding.toolbar)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //initial open fragment
        if (savedInstanceState == null){
            replaceFragment(RunningFragment())
            navigationView.setCheckedItem(R.id.nav_running)
            supportActionBar!!.setTitle(R.string.menu_running)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.nav_running->{
                replaceFragment(RunningFragment())
                supportActionBar!!.setTitle(R.string.menu_running)
            }
            R.id.nav_preset->{
                replaceFragment(PresetFragment())
                supportActionBar!!.setTitle(R.string.menu_preset)
            }
            R.id.nav_Activities->{

                replaceFragment(ActivitiesFragment())
                supportActionBar!!.setTitle(R.string.menu_activities)
            }
            R.id.nav_setting->{
                replaceFragment(SettingFragment())
                supportActionBar!!.setTitle(R.string.menu_setting)
            }

            R.id.nav_help->{
                replaceFragment(HelpFragment())
                supportActionBar!!.setTitle(R.string.menu_help)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment(fragment:Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()

    }


    // presetAddFragment 에서 title EditText 에서 키보드 내리기.
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        if(currentFocus is EditText) {
            currentFocus!!.clearFocus()
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else {

            if (supportFragmentManager.backStackEntryCount == 0){
                if (isBackPressedOnce + 1500 > System.currentTimeMillis()){
                    finish()
                }
                else{
                    Toast.makeText(applicationContext, getString(R.string.main_exit), Toast.LENGTH_SHORT).show()
                    isBackPressedOnce = System.currentTimeMillis()
                }
            }
            else{
//                onBackPressedDispatcher.onBackPressed()
                super.onBackPressed()

            }
        }
    }
}
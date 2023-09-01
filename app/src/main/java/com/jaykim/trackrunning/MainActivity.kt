package com.jaykim.trackrunning

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.jaykim.trackrunning.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityMainBinding
    private lateinit var drawerLayout : DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


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


        //call oncraete for fragments



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
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment(fragment:Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()

    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}
package com.felipedeveloper.shareplacesapp.ui.main

import android.Manifest.permission
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.felipedeveloper.shareplacesapp.PREFERENCES_REPOSITORY
import com.felipedeveloper.shareplacesapp.R
import com.felipedeveloper.shareplacesapp.databinding.ActivityMainBinding
import com.felipedeveloper.shareplacesapp.ui.SplashActivity
import com.felipedeveloper.shareplacesapp.ui.authentication.LoginActivity
import com.felipedeveloper.shareplacesapp.ui.base.BaseActivity
import com.felipedeveloper.shareplacesapp.utilities.Utils
import com.felipedeveloper.shareplacesapp.utilities.goToActivity
import com.felipedeveloper.shareplacesapp.worker.AppWorker
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var toolbar: Toolbar

    private val GPS_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbarIncluded.root

        toolbarToLoad(toolbar)
        toolbarTitle(getString(R.string.main))

        setNavDrawer()

        if (savedInstanceState == null) {
            fragmentTransaction(PlacesFragment())
            binding.navView.menu.getItem(0).isChecked = true
        }

        startWorker()

        setUpHeaderUi()
    }

    override fun onResume() {
        super.onResume()
        val mGoogleApiClient: GoogleApiClient = Utils.buildGoogleApiClient(this)
        mGoogleApiClient.connect()

        Utils.showGpsDialog(mGoogleApiClient) {
            when (it.status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> Log.d(TAG, "Gps enable")
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> startResolution(it.status)
            }
        }
    }

    private fun startResolution(status: Status) {
        try {
            startIntentSenderForResult(
                status.resolution.intentSender, GPS_REQUEST_CODE,
                null, 0, 0, 0, null
            )
        } catch (e: IntentSender.SendIntentException) {
            Log.d(SplashActivity.TAG, "wrong gps request")
        }
    }

    private fun startWorker() {

        try {
            if (isWorkScheduled(WorkManager.getInstance().getWorkInfosByTag(WORKER_TAG).get())) {
                Log.d(TAG, "working")
            } else {
                val periodicWork = PeriodicWorkRequest
                    .Builder(AppWorker::class.java, INTERVAL_REPEAT, TimeUnit.MINUTES)
                    .addTag(WORKER_TAG)
                    .build()

                WorkManager.getInstance()
                    .enqueueUniquePeriodicWork(
                        "Location",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        periodicWork
                    )
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    private fun isWorkScheduled(workInfos: List<WorkInfo>?): Boolean {
        var running = false
        if (workInfos == null || workInfos.isEmpty()) return false
        for (workStatus in workInfos) {
            running =
                workStatus.state == WorkInfo.State.RUNNING || workStatus.state == WorkInfo.State.ENQUEUED
        }
        return running
    }

    private fun setUpHeaderUi() {
        binding.navView.getHeaderView(0).apply {
            PREFERENCES_REPOSITORY.userEmail.asLiveData().observe(this@MainActivity) { email ->
                findViewById<TextView>(R.id.tv_email).text = email
            }
        }
    }

    private fun loadFragmentById(id: Int) {
        when (id) {
            R.id.nav_main -> fragmentTransaction(PlacesFragment())
            R.id.nav_logout -> logoutUser()
        }
    }

    private fun logoutUser() {

        WorkManager.getInstance().cancelAllWorkByTag(WORKER_TAG)

        lifecycleScope.launch {
            PREFERENCES_REPOSITORY.clearDataStore()
            goToActivity<LoginActivity>()
            finish()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        loadFragmentById(item.itemId)
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun fragmentTransaction(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    private fun setNavDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        toggle.isDrawerIndicatorEnabled = true
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
    }

    companion object {

        const val TAG = "MainActivity"

        const val WORKER_TAG = "LocationUpdate"

        const val INTERVAL_REPEAT: Long = 30
    }

}
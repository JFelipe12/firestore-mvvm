package com.felipedeveloper.shareplacesapp.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.felipedeveloper.shareplacesapp.PREFERENCES_REPOSITORY
import com.felipedeveloper.shareplacesapp.R
import com.felipedeveloper.shareplacesapp.ui.authentication.LoginActivity
import com.felipedeveloper.shareplacesapp.ui.main.MainActivity
import com.felipedeveloper.shareplacesapp.utilities.goToActivity
import com.felipedeveloper.shareplacesapp.utilities.toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        observeUiPreferences()
    }

    private fun checkLocationPermission(isLogged: Boolean) {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        validateIntent(isLogged)
                    } else {
                        toast("Por favor acepte todos los permisos necesarios")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun validateIntent(isLogged: Boolean) {
        if (isLogged) goToActivity<MainActivity>()
        else goToActivity<LoginActivity>()
        finish()
    }

    private fun observeUiPreferences() {

        PREFERENCES_REPOSITORY.isLogged.asLiveData().observe(this) { isLogged ->
            checkLocationPermission(isLogged)
        }

        lifecycleScope.launch {
            PREFERENCES_REPOSITORY.uid.collect { value ->
                Log.d("service", value)
            }
        }
    }

    companion object {
        val TAG = "SplashActivity"
    }
}
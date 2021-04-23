package com.felipedeveloper.shareplacesapp.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import com.felipedeveloper.shareplacesapp.App
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.single.PermissionListener
import java.io.*
import java.util.*

class Utils {

    companion object {

        @Throws(IOException::class)
        fun getBitmapFromUri(uri: Uri, context: Context = App.appContext): Bitmap? {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()
            return image
        }

        @Throws(Exception::class)
        fun createTransactionID(): String {
            return UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.getDefault())
        }

        fun getCompleteAddress(mContext: Context, lat: Double, lng: Double): String {
            var strAdd = ""
            val geocoder = Geocoder(mContext, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (addresses != null) {
                    val returnedAddress = addresses[0]
                    val strReturnedAddress = StringBuilder()
                    for (i in 0..returnedAddress.maxAddressLineIndex) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                    }
                    strAdd = strReturnedAddress.toString()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return strAdd
        }

        fun checkSelfPermission(activity: Context) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        fun buildGoogleApiClient(context: Context) = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .build()


        fun showGpsDialog(
            mGoogleApiClient: GoogleApiClient,
            actionEvt: (v: LocationSettingsResult) -> Unit = {}
        ) {

            val locationRequest: LocationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 30 * 1000
            locationRequest.fastestInterval = 5 * 1000
            val builderApi: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            builderApi.setAlwaysShow(true)

            val result: PendingResult<LocationSettingsResult> =
                LocationServices.SettingsApi.checkLocationSettings(
                    mGoogleApiClient,
                    builderApi.build()
                )

            result.setResultCallback(actionEvt)
        }

    }

}
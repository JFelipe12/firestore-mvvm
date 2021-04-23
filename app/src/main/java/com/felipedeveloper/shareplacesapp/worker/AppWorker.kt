package com.felipedeveloper.shareplacesapp.worker

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.felipedeveloper.shareplacesapp.PREFERENCES_REPOSITORY
import com.felipedeveloper.shareplacesapp.data.models.remote.LocationData
import com.felipedeveloper.shareplacesapp.data.repository.remote.FirestoreRepository
import com.felipedeveloper.shareplacesapp.utilities.Resource
import com.felipedeveloper.shareplacesapp.utilities.Utils
import com.google.android.gms.location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class AppWorker(private val mContext: Context, workerParams: WorkerParameters) :
    Worker(mContext, workerParams) {

    private lateinit var mLocation: Location

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private lateinit var mLocationCallback: LocationCallback

    private val repository by lazy { FirestoreRepository() }

    companion object {

        private const val TAG = "AppWorker"

        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }

    override fun doWork(): Result {

        Log.d(TAG, "workManager starting")

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
            }
        }

        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        try {
            mFusedLocationClient.lastLocation?.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    mLocation = task.result!!
                    updateLocation(mLocation)
                    Log.d(TAG, "finished with location $mLocation")
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                } else {
                    Log.w(TAG, "Failed to get location.")
                }
            }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission.$unlikely")
        }
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, null)
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Missing permission $unlikely")
        }

        return Result.success()
    }


    private fun updateLocation(mLocation: Location?) {

        val map: MutableMap<String, Double> = HashMap()
        val address: String

        mLocation?.apply {
            map["lat"] = latitude
            map["lng"] = longitude

            address = Utils.getCompleteAddress(mContext, latitude, longitude)

            val locationData = LocationData(latitude, longitude, address)

            GlobalScope.launch {
                PREFERENCES_REPOSITORY.uid.collect { value ->
                    Log.d(TAG, "$value : $locationData")
                    updateLocationOnFirestore(value, locationData)
                }
            }
        }

    }

    private suspend fun updateLocationOnFirestore(uid: String, data: LocationData) {
        repository.updateUserLocation(uid, data).collect { state ->
            when (state) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Log.d(TAG, "location updated successfully")
                }
                is Resource.Failure -> {
                    Log.e(TAG, state.message)
                }
            }
        }
    }
}


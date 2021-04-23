package com.felipedeveloper.shareplacesapp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.felipedeveloper.shareplacesapp.data.models.local.Places
import com.felipedeveloper.shareplacesapp.data.models.remote.PlacesData
import com.felipedeveloper.shareplacesapp.data.repository.local.DatabaseRepository
import com.felipedeveloper.shareplacesapp.data.repository.remote.FirestoreRepository
import com.felipedeveloper.shareplacesapp.data.repository.remote.RealtimeDataListener
import com.felipedeveloper.shareplacesapp.data.repository.remote.StorageRepository
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class PlaceViewModel @ViewModelInject constructor(
    var firestoreRepository: FirestoreRepository,
    var storageRepository: StorageRepository,
    var databaseRepository: DatabaseRepository
) : ViewModel() {


    fun addData(data: Any, collectionName: String) =
        firestoreRepository.addData(data, collectionName)

    fun getData(collectionName: String) = firestoreRepository.getData(collectionName)

    fun uploadFromFile(data: ByteArray) = storageRepository.uploadFromFile(data)

    fun getDownloadUri(task: UploadTask.TaskSnapshot) = storageRepository.getDownloadUri(task)

    fun listenDataChanges(listener: RealtimeDataListener<ArrayList<PlacesData?>>) = firestoreRepository.listenForUpdates(listener)

    //Room query
    fun insertPlacesIntoLocalDb(data: Places) = liveData(Dispatchers.Main) {
        emit(databaseRepository.insertPlace(data))
    }


}
package com.felipedeveloper.shareplacesapp.data.repository.remote

import android.net.Uri
import com.felipedeveloper.shareplacesapp.utilities.Resource
import com.felipedeveloper.shareplacesapp.utilities.Utils
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

const val PLACES_STORAGE_REFERENCE = "places"

class StorageRepository @Inject constructor() {

    val storage: FirebaseStorage = FirebaseStorage.getInstance()

    fun uploadFromFile(data: ByteArray) = flow<Resource<UploadTask.TaskSnapshot>> {

        emit(Resource.Loading())

        val storageRef = storage.reference
        val ref = Utils.createTransactionID()

        val imageRef: StorageReference =
            storageRef.child(PLACES_STORAGE_REFERENCE).child("IMAGE_$ref")

        val response = imageRef.putBytes(data).await()

        emit(Resource.Success(response))

    }.catch {
        emit(Resource.Failure("${it.message}"))
    }.flowOn(Dispatchers.IO)


    fun getDownloadUri(task: UploadTask.TaskSnapshot) = flow<Resource<Uri>> {
        val response = task.storage.downloadUrl.await()
        emit(Resource.Success(response))

    }

}
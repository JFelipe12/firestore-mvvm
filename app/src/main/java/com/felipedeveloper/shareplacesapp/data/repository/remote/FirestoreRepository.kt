package com.felipedeveloper.shareplacesapp.data.repository.remote

import com.felipedeveloper.shareplacesapp.data.models.remote.PlacesData
import com.felipedeveloper.shareplacesapp.utilities.Resource
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

const val USERS_COLLECTION_NAME = "users"
const val PLACES_COLLECTION_NAME = "places"


class FirestoreRepository @Inject constructor() {

    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val placeReference = firebaseFirestore.collection(PLACES_COLLECTION_NAME)
    private val userReference = firebaseFirestore.collection(USERS_COLLECTION_NAME)

    fun addData(data: Any, collectionName: String) = flow<Resource<DocumentReference>> {

        emit(Resource.Loading())

        val response = firebaseFirestore.collection(collectionName).add(data).await()

        emit(Resource.Success(response))

    }.catch {
        emit(Resource.Failure("${it.message}"))
    }.flowOn(Dispatchers.IO)

    fun addDataWithCustomId(id: String, data: Any, collectionName: String) =
        flow<Resource<Void>> {

            emit(Resource.Loading())

            val response =
                firebaseFirestore.collection(collectionName).document(id).set(data).await()

            emit(Resource.Success(response))

        }.catch {
            emit(Resource.Failure("${it.message}"))
        }.flowOn(Dispatchers.IO)


    fun getData(collectionName: String) = flow<Resource<ArrayList<PlacesData>>> {

        emit(Resource.Loading())

        val response = firebaseFirestore.collection(collectionName)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await()

        try {
            val places = response.toObjects(PlacesData::class.java) as ArrayList<PlacesData>
            emit(Resource.Success(places))
        } catch (e: RuntimeException) {
            emit(Resource.Failure("${e.message}"))
        }
    }.catch {
        emit(Resource.Failure("${it.message}"))
    }.flowOn(Dispatchers.IO)

    fun updateUserLocation(id: String, location: Any) = flow<Resource<Void>> {

        emit(Resource.Loading())

        val response = firebaseFirestore.collection(USERS_COLLECTION_NAME).document(id)
            .update("location", location).await()

        emit(Resource.Success(response))

    }.catch {
        emit(Resource.Failure("${it.message}"))
    }.flowOn(Dispatchers.IO)


    fun listenForUpdates(listener: RealtimeDataListener<ArrayList<PlacesData?>>) {

        val reference = firebaseFirestore.collection(PLACES_COLLECTION_NAME)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        reference.addSnapshotListener { snapshot, e ->

            if (e != null) listener.onError(e)

            val placesDataList: ArrayList<PlacesData?> = ArrayList()

            if (snapshot != null && !snapshot.isEmpty) {
                for (documentSnapshot in snapshot.documents) {
                    val places: PlacesData? = documentSnapshot.toObject(PlacesData::class.java)
                    placesDataList.add(places)
                }
                listener.onDataChange(placesDataList)
            }
        }
    }
}
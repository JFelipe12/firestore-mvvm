package com.felipedeveloper.shareplacesapp.viewmodels.register

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.felipedeveloper.shareplacesapp.data.repository.remote.FirebaseAuthRepository
import com.felipedeveloper.shareplacesapp.data.repository.remote.FirestoreRepository

class RegisterViewModel @ViewModelInject constructor(
    var firestoreRepository: FirestoreRepository, var authRepository: FirebaseAuthRepository
) : ViewModel() {


    fun createUser(email: String, password: String) = authRepository.createUser(email, password)

    fun addDataWithCustomId(id: String, data: Any, collectionName: String) = firestoreRepository.addDataWithCustomId(id, data, collectionName)

}
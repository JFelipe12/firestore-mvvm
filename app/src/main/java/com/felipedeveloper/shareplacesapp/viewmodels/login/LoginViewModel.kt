package com.felipedeveloper.shareplacesapp.viewmodels.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.felipedeveloper.shareplacesapp.data.repository.remote.FirebaseAuthRepository
import com.felipedeveloper.shareplacesapp.data.repository.remote.FirestoreRepository

class LoginViewModel @ViewModelInject constructor(
    var authRepository: FirebaseAuthRepository
) : ViewModel() {


    fun loginUser(email: String, password: String) = authRepository.loginUser(email, password)


}
package com.felipedeveloper.shareplacesapp.viewmodels.login

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.felipedeveloper.shareplacesapp.utilities.isValidEmail
import com.felipedeveloper.shareplacesapp.utilities.isValidPassword

class ValidateLoginViewModel : ViewModel() {

    val etEmail = MutableLiveData<String?>()
    val etPassword = MutableLiveData<String?>()

    val wrongPassword: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val wrongEmail: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val mLoginMediator = MediatorLiveData<Boolean>()

    init {
        mLoginMediator.addSource(etEmail) { validateForm() }
        mLoginMediator.addSource(etPassword) { validateForm() }
    }

    private fun validateForm() {

        etEmail.value?.let { wrongEmail.value = !isValidEmail(it) }
        etPassword.value?.let { wrongPassword.value = !isValidPassword(it) }

        mLoginMediator.value =etPassword.value != null && etEmail.value != null && !wrongPassword.value!! && !wrongEmail.value!!

    }
}
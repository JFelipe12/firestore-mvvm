package com.felipedeveloper.shareplacesapp.viewmodels.register

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.felipedeveloper.shareplacesapp.utilities.isValidEmail
import com.felipedeveloper.shareplacesapp.utilities.isValidPassword

class ValidateRegisterViewModel : ViewModel() {

    val etName = MutableLiveData("")
    val etEmail = MutableLiveData<String?>()
    val etPassword = MutableLiveData<String?>()

    val wrongPassword: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val wrongEmail: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val mRegisterMediator = MediatorLiveData<Boolean>()

    init {
        mRegisterMediator.addSource(etName) { validateForm() }
        mRegisterMediator.addSource(etEmail) { validateForm() }
        mRegisterMediator.addSource(etPassword) { validateForm() }
    }

    private fun validateForm() {

        etEmail.value?.let { wrongEmail.value = !isValidEmail(it) }
        etPassword.value?.let { wrongPassword.value = !isValidPassword(it) }

        if (etName.value?.isBlank()!!) {
            mRegisterMediator.value = false
        } else {
            mRegisterMediator.value =
                etName.value != null && etPassword.value != null
                        && etEmail.value != null && !wrongPassword.value!! && !wrongEmail.value!!
        }

    }
}
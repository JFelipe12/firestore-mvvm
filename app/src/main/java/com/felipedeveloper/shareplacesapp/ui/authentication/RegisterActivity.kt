package com.felipedeveloper.shareplacesapp.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.felipedeveloper.shareplacesapp.PREFERENCES_REPOSITORY
import com.felipedeveloper.shareplacesapp.R
import com.felipedeveloper.shareplacesapp.data.models.remote.UserData
import com.felipedeveloper.shareplacesapp.data.repository.remote.USERS_COLLECTION_NAME
import com.felipedeveloper.shareplacesapp.databinding.ActivityRegisterBinding
import com.felipedeveloper.shareplacesapp.ui.base.BaseActivity
import com.felipedeveloper.shareplacesapp.ui.main.MainActivity
import com.felipedeveloper.shareplacesapp.utilities.*
import com.felipedeveloper.shareplacesapp.viewmodels.register.RegisterViewModel
import com.felipedeveloper.shareplacesapp.viewmodels.register.ValidateRegisterViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : BaseActivity() {

    private val validate: ValidateRegisterViewModel by viewModels()

    private val viewmodel: RegisterViewModel by viewModels()

    private lateinit var binding: ActivityRegisterBinding

    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        binding.viewmodel = validate

        binding.lifecycleOwner = this

        setUp()

        listenViewModels()

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.btnNext.setOnClickListener {
            lifecycleScope.launch {
                createUserWithEmailAndPassword()
            }
        }
    }

    private fun listenViewModels() {

        validate.mRegisterMediator.observe(this, { validationResult ->
            binding.btnNext.isEnabled = validationResult
            if (validationResult) {
                binding.btnNext.setBackgroundResource(R.drawable.custom_button_enable)
            } else {
                binding.btnNext.setBackgroundResource(R.drawable.custom_button_disable)
            }
        })

        validate.wrongPassword.observe(this, { validationResult ->
            if (validationResult) binding.tiPassword.error = "Contraseña demasiado corta"
            else binding.tiPassword.isErrorEnabled = false
        })

        validate.wrongEmail.observe(this, { validationResult ->
            if (validationResult) binding.tiEmail.error = "Email no válido"
            else binding.tiEmail.isErrorEnabled = false
        })

    }

    private suspend fun registerUserOnDatabase() {

        val key = user?.uid
        val userData: UserData

        binding.apply {
            userData = UserData(
                etName.text.toString(), etEmail.text.toString(), etPassword.text.toString()
            )
        }

        viewmodel.addDataWithCustomId(key!!, userData, USERS_COLLECTION_NAME).collect { state ->
            when (state) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {

                    savePreferences(key)

                    goToActivity<MainActivity> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }

                    hideProgressDialog()
                }

                is Resource.Failure -> {
                    hideProgressDialog()
                    toast(state.message)
                    Log.e(MainActivity.TAG, state.message)
                }
            }
        }
    }


    private suspend fun createUserWithEmailAndPassword() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPassword.text.toString()

        viewmodel.createUser(email, pass).collect { state ->
            when (state) {
                is Resource.Loading -> showProgressDialog("Registrando")
                is Resource.Success -> {
                    user = state.data.user
                    Log.d(TAG, "${user?.uid}")
                    registerUserOnDatabase()
                }

                is Resource.Failure -> {
                    hideProgressDialog()
                    toast(state.message)
                    Log.e(MainActivity.TAG, state.message)
                }
            }
        }
    }


    private fun savePreferences(id: String) {
        lifecycleScope.launch {
            PREFERENCES_REPOSITORY.setUid(id)
            PREFERENCES_REPOSITORY.setLoggedMode(true)
            PREFERENCES_REPOSITORY.setUserEmail(binding.etEmail.text.toString())
        }
    }

    private fun setUp() {
        toolbarToLoad(binding.toolbar)
        toolbarTitle("Registrar cuenta")
        enableHomeDisplay(true)
    }

    companion object {
        private val TAG = "RegisterActivity"
    }
}
package com.felipedeveloper.shareplacesapp.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.felipedeveloper.shareplacesapp.PREFERENCES_REPOSITORY
import com.felipedeveloper.shareplacesapp.R
import com.felipedeveloper.shareplacesapp.databinding.ActivityLoginBinding
import com.felipedeveloper.shareplacesapp.ui.base.BaseActivity
import com.felipedeveloper.shareplacesapp.ui.main.MainActivity
import com.felipedeveloper.shareplacesapp.ui.main.MainActivity.Companion.TAG
import com.felipedeveloper.shareplacesapp.utilities.*
import com.felipedeveloper.shareplacesapp.viewmodels.login.LoginViewModel
import com.felipedeveloper.shareplacesapp.viewmodels.login.ValidateLoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private val validate: ValidateLoginViewModel by viewModels()

    private val viewmodel: LoginViewModel by viewModels()

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.viewmodel = validate

        binding.lifecycleOwner = this

        clickListeners()

        listenViewModels()

        setUp()
    }

    private fun listenViewModels() {
        validate.mLoginMediator.observe(this, { validationResult ->
            binding.btnLogin.isEnabled = validationResult
            if (validationResult) {
                binding.btnLogin.setBackgroundResource(R.drawable.custom_button_enable)
            } else {
                binding.btnLogin.setBackgroundResource(R.drawable.custom_button_disable)
            }
        })

        validate.wrongPassword.observe(this, { validationResult ->
            if (validationResult) binding.tiPassword.error = getString(R.string.invalid_password)
            else binding.tiPassword.isErrorEnabled = false
        })

        validate.wrongEmail.observe(this, { validationResult ->
            if (validationResult) binding.tiEmail.error = getString(R.string.invalid_email)
            else binding.tiEmail.isErrorEnabled = false
        })
    }

    private fun clickListeners() {

        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch { loginUser() }
        }

        binding.floatingRegister.setOnClickListener {
            goToActivity<RegisterActivity>()
        }
    }

    private suspend fun loginUser() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPassword.text.toString()

        viewmodel.loginUser(email, pass).collect { result ->
            when (result) {
                is Resource.Loading -> showProgressDialog("Ingresando")
                is Resource.Success -> {
                    toast("Login success")

                    val uid = result.data.user.uid

                    savePreferences(uid)

                    goToActivity<MainActivity> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }

                    hideProgressDialog()
                }

                is Resource.Failure -> {
                    hideProgressDialog()
                    toast(result.message)
                    Log.e(TAG, result.message)
                }
            }
        }
    }

    private fun savePreferences(uid: String) {
        lifecycleScope.launch {
            PREFERENCES_REPOSITORY.setUid(uid)
            PREFERENCES_REPOSITORY.setLoggedMode(true)
            PREFERENCES_REPOSITORY.setUserEmail(binding.etEmail.text.toString())
        }
    }

    private fun setUp() {
        toolbarToLoad(binding.toolbar)
        toolbarTitle(getString(R.string.login))
    }
}
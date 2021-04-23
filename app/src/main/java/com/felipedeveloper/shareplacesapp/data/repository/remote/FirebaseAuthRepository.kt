package com.felipedeveloper.shareplacesapp.data.repository.remote

import com.felipedeveloper.shareplacesapp.utilities.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAuthRepository @Inject constructor() {

    private val mAuth = FirebaseAuth.getInstance()

    fun loginUser(email: String, password: String) = flow<Resource<AuthResult>> {

        emit(Resource.Loading())

        val response = mAuth.signInWithEmailAndPassword(email, password).await()

        emit(Resource.Success(response))

    }.catch {
        emit(Resource.Failure("${it.message}"))
    }.flowOn(Dispatchers.IO)


    fun createUser(email: String, password: String) = flow<Resource<AuthResult>> {

        emit(Resource.Loading())

        val response = mAuth.createUserWithEmailAndPassword(email, password).await()

        emit(Resource.Success(response))

    }.catch {
        emit(Resource.Failure("${it.message}"))
    }.flowOn(Dispatchers.IO)

}
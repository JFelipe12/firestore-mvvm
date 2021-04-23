package com.felipedeveloper.shareplacesapp.ui

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.felipedeveloper.shareplacesapp.R
import com.felipedeveloper.shareplacesapp.data.models.remote.PhotosData
import com.felipedeveloper.shareplacesapp.data.repository.remote.*
import com.felipedeveloper.shareplacesapp.databinding.ActivityAddPlaceBinding
import com.felipedeveloper.shareplacesapp.ui.base.BaseActivity
import com.felipedeveloper.shareplacesapp.utilities.*
import com.felipedeveloper.shareplacesapp.viewmodels.PlaceViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.UploadTask
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@AndroidEntryPoint
class AddPlaceActivity : BaseActivity() {

    private lateinit var binding: ActivityAddPlaceBinding

    private val viewmodel: PlaceViewModel by viewModels()

    private var imagePath = arrayListOf<ByteArray>()

    private val photos = ArrayList<PhotosData>()

    private val REQUEST_IMAGE_GALLERY = 2000

    private var isFirstImageClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUp()

        clickListeners()
    }

    private fun clickListeners() {

        binding.ivImageOne.setOnClickListener {
            isFirstImageClicked = true
            checkPermission()
        }

        binding.ivImageTwo.setOnClickListener {
            isFirstImageClicked = false
            checkPermission()
        }

        binding.floatingSend.setOnClickListener {

            if (imagePath.size == 0) {
                toast("Sube una imagen como mínimo")
                return@setOnClickListener
            }

            if (binding.etDescription.text.isBlank()) {
                toast("Agrega una descripción")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                imagePath.map {
                    uploadFileOnStorage(it)
                }
            }
        }
    }

    private suspend fun uploadFileOnStorage(data: ByteArray) {

        viewmodel.uploadFromFile(data).collect { state ->
            when (state) {
                is Resource.Loading -> {
                    showProgressDialog("Agregando Lugar")
                }
                is Resource.Success -> {
                    getDownloadUrl(state.data)
                }

                is Resource.Failure -> {
                    hideProgressDialog()
                    toast(state.message)
                    Log.e(TAG, state.message)
                }
            }
        }
    }

    private suspend fun getDownloadUrl(state: UploadTask.TaskSnapshot) {
        viewmodel.getDownloadUri(state).collect { uri ->
            when (uri) {
                is Resource.Success -> {
                    photos.add(PhotosData(uri.data.toString()))
                    if (imagePath.size == photos.size) addPlaceOnFirestore()
                }
                else -> {
                }
            }
        }
    }

    private suspend fun addPlaceOnFirestore() {

        val placeData: HashMap<String, Any> = HashMap()

        placeData["placesId"] = UUID.randomUUID().toString()
        placeData["userId"] = FirebaseAuth.getInstance().currentUser?.uid!!
        placeData["description"] = binding.etDescription.text.toString()
        placeData["createdAt"] = Timestamp.now().seconds
        placeData["photos"] = photos

        viewmodel.addData(placeData, PLACES_COLLECTION_NAME).collect { state ->
            when (state) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    onBackPressed()
                    toast("Lugar agregado")
                    hideProgressDialog()
                }

                is Resource.Failure -> {
                    hideProgressDialog()
                    toast(state.message)
                    Log.e(TAG, state.message)
                }
            }
        }
    }


    private fun checkPermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        dispatchTakeGallery()
                    } else {
                        toast("Por favor acepte todos los permisos necesarios")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun setUp() {
        toolbarToLoad(binding.toolbar)
        enableHomeDisplay(true)
        toolbarTitle(getString(R.string.add_place_title))
    }

    private fun dispatchTakeGallery() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, REQUEST_IMAGE_GALLERY)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val uri: Uri? = data?.data

            when (requestCode) {
                REQUEST_IMAGE_GALLERY -> {
                    var bmp: Bitmap? = null

                    try {
                        bmp = Utils.getBitmapFromUri(uri!!)
                    } catch (e: IOException) {
                        Log.e(TAG, "${e.cause?.message}")
                    }

                    val baos = ByteArrayOutputStream()
                    bmp?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val byteArray: ByteArray = baos.toByteArray()

                    imagePath.add(byteArray)

                    if (isFirstImageClicked) binding.ivImageOne.setImageBitmap(bmp)
                    else binding.ivImageTwo.setImageBitmap(bmp)
                }
            }
        }
    }

    companion object {
        private val TAG = "AddPlaceActivity"
    }
}
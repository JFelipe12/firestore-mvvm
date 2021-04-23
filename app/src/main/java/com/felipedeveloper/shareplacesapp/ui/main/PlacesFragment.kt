package com.felipedeveloper.shareplacesapp.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.felipedeveloper.shareplacesapp.data.models.local.Places
import com.felipedeveloper.shareplacesapp.data.models.remote.PlacesData
import com.felipedeveloper.shareplacesapp.data.repository.remote.PLACES_COLLECTION_NAME
import com.felipedeveloper.shareplacesapp.data.repository.remote.RealtimeDataListener
import com.felipedeveloper.shareplacesapp.databinding.FragmentPlacesBinding
import com.felipedeveloper.shareplacesapp.ui.AddPlaceActivity
import com.felipedeveloper.shareplacesapp.ui.main.MainActivity.Companion.TAG
import com.felipedeveloper.shareplacesapp.ui.main.adapter.PlacesAdapter
import com.felipedeveloper.shareplacesapp.utilities.*
import com.felipedeveloper.shareplacesapp.viewmodels.PlaceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlacesFragment : Fragment() {

    private var _binding: FragmentPlacesBinding? = null

    private lateinit var mAdapter: PlacesAdapter

    private val binding get() = _binding!!

    private val viewmodel: PlaceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlacesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = PlacesAdapter(object : BaseRecyclerClickListener<PlacesData> {
            override fun onClick(type: PlacesData, position: Int) {

            }
        })

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }

        lifecycleScope.launch {

            getPlaces()
        }

        listenDataChanges()

        clickListeners()

    }

     private fun listenDataChanges() {
         viewmodel.listenDataChanges(object : RealtimeDataListener<ArrayList<PlacesData?>> {
             override fun onDataChange(updatedData: ArrayList<PlacesData?>) {
                 Log.d(TAG, "places list $updatedData")
                 mAdapter.submitList(updatedData)
                 binding.recyclerView.scrollToPosition(0)
             }

             override fun onError(exception: Exception) {
                 Log.e(TAG, "${exception.message}")
             }
         })

     }

    private suspend fun getPlaces() {
        viewmodel.getData(PLACES_COLLECTION_NAME).collect { result ->
            when (result) {
                is Resource.Loading -> activity?.showProgressDialog("Cargando lugares")

                is Resource.Success -> {
                    Log.d(TAG, "places list ${result.data}")
                    mAdapter.submitList(result.data)
                    hideProgressDialog()
                }

                is Resource.Failure -> {
                    hideProgressDialog()
                    activity?.toast(result.message)
                    Log.e(TAG, result.message)
                }
            }
        }
    }


    private fun insertPlacesIntoLocalDb(place: Places) {
        viewmodel.insertPlacesIntoLocalDb(place).observe(viewLifecycleOwner, { id ->
            Log.d(TAG, "room inserted")
        })
    }

    private fun clickListeners() {
        binding.floatingAddPlace.setOnClickListener {
            activity?.goToActivity<AddPlaceActivity>()
        }
    }

}
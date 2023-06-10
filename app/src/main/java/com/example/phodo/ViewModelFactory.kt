package com.example.phodo

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.phodo.PhotoGuide.PhotoGuideDetail
import com.example.phodo.PhotoGuide.PhotoGuideDetailViewModel
import com.example.phodo.PhotoMap.MapViewModel
import com.example.phodo.Repository.HomeRepository
import com.example.phodo.Repository.MapRepository
import com.example.phodo.Repository.PhotoGuideRepository
import com.example.phodo.Repository.PhotoMakerRepository
import com.example.phodo.data.RemoteDataSourceImp
import com.example.phodo.photoMaker.PhotoMakerViewModel
import com.example.phodo.ui.home.CameraViewModel

class ViewModelFactory( private val context: Context): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PhotoGuideListViewModel::class.java) -> {
                val repository = PhotoGuideRepository( RemoteDataSourceImp(RetrofitInstance))
                PhotoGuideListViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PhotoMakerViewModel::class.java) -> {
                val repository = PhotoMakerRepository(RemoteDataSourceImp(RetrofitInstance))
                PhotoMakerViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                val repository = MapRepository( RemoteDataSourceImp(RetrofitInstance))
                MapViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PhotoGuideDetailViewModel::class.java) -> {
                val repository = PhotoGuideRepository( RemoteDataSourceImp(RetrofitInstance))
                PhotoGuideDetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                val repository = HomeRepository( RemoteDataSourceImp(RetrofitInstance))
                HomeViewModel(repository) as T
            }
            /*
            modelClass.isAssignableFrom(CameraViewModel::class.java) -> {
                val repository = HomeRepository( RemoteDataSourceImp(RetrofitInstance))
                CameraViewModel(repository) as T
            }

             */

            else -> {
                throw IllegalArgumentException("Failed to create ViewModel : ${modelClass.name}")
            }
        }
    }
}
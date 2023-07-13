package com.example.phodo

import android.app.Application
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

class ViewModelFactory(private val remoteDataSource : RemoteDataSourceImp): ViewModelProvider.Factory { //private val application: Application,

    // 액티비티 별로 서로 다른 뷰모델을 만들기 위해서 ViewModelProvider.Factory 를 implements 함
    // 뷰모델을 만드는 create() 메서드를 오버라이딩
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PhotoGuideListViewModel::class.java) -> {
                val repository = PhotoGuideRepository(remoteDataSource)
                PhotoGuideListViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PhotoMakerViewModel::class.java) -> {
                val repository = PhotoMakerRepository(remoteDataSource)
                //뷰모델에서 context 사용하는 경우
                //modelClass.getConstructor(Application::class.java, PhotoMakerRepository::class.java)
                //    .newInstance(application, repository)
                //PhotoMakerViewModel(application, repository) as T
                PhotoMakerViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                val repository = MapRepository(remoteDataSource)
                MapViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PhotoGuideDetailViewModel::class.java) -> {
                val repository = PhotoGuideRepository(remoteDataSource)
                PhotoGuideDetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                val repository = HomeRepository(remoteDataSource)
                HomeViewModel(repository) as T
            }
            /*
            modelClass.isAssignableFrom(CameraViewModel::class.java) -> {
                val repository = HomeRepository(remoteDataSource)
                CameraViewModel(repository) as T
            }

             */

            else -> {
                throw IllegalArgumentException("Failed to create ViewModel : ${modelClass.name}")
            }
        }
    }
}
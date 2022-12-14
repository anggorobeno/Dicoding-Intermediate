package com.example.submission1androidintermediate.ui.home.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.di.CoroutinesQualifier
import com.example.domain.model.stories.ImageModel
import com.example.domain.model.stories.StoriesUploadModel
import com.example.domain.usecase.stories.StoriesUseCase
import com.example.domain.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SharedStoriesViewModel @Inject constructor(private val useCase: StoriesUseCase) :
    ViewModel() {
    @Inject
    @CoroutinesQualifier.IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    @CoroutinesQualifier.MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher
    private var _imageBitmap = MutableLiveData<ImageModel>()
    val imageBitmap: LiveData<ImageModel> get() = _imageBitmap

    private var _storiesUploadResult = MutableLiveData<NetworkResult<StoriesUploadModel>>()
    val storiesUploadResult: LiveData<NetworkResult<StoriesUploadModel>> get() = _storiesUploadResult

    fun uploadImage(description: String, file: File, lat: String, lon: String) {
        viewModelScope.launch(ioDispatcher) {
            val desc = description.toRequestBody("text/plain".toMediaType())
            val latitude = lat.toRequestBody("text/plain".toMediaType())
            val longitude = lon.toRequestBody("text/plain".toMediaType())
            val requestImageFile =
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            withContext(mainDispatcher) {
                useCase.uploadStories(desc, latitude, longitude, imageMultipart).collectLatest {
                    _storiesUploadResult.value = it
                }
            }

        }

    }

    fun saveImageResult(image: File, isBackCamera: Boolean) {
        viewModelScope.launch {
            val imageModel = ImageModel(image, isBackCamera)
            _imageBitmap.value = imageModel
        }
    }
}
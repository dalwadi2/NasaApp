package dev.harshdalwadi.nasaapp.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.harshdalwadi.nasaapp.api.ApiServices
import dev.harshdalwadi.nasaapp.api.Resource
import dev.harshdalwadi.nasaapp.base.BaseViewModel
import dev.harshdalwadi.nasaapp.models.RespNasaDataItem
import dev.harshdalwadi.nasaapp.utils.CoroutineDispatchers
import dev.harshdalwadi.nasaapp.utils.extensions.resultApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject


@HiltViewModel
class ImageListViewModel @Inject constructor(
    private val apiServices: ApiServices,
    private val dispatchers: CoroutineDispatchers,
    private val context: Application,
    private val gson: Gson,
) : BaseViewModel(context) {

    private val nasaImageModelState = MutableStateFlow<Resource<List<RespNasaDataItem>>>(Resource.Idle)
    val nasaImageModel = nasaImageModelState.asStateFlow()

    init {
        fetchImages()
    }

    fun fetchImagesFromAPI() = viewModelScope.launch {
        nasaImageModelState.value = Resource.Loading
        resultApiCall(dispatchers) {
            apiServices.fetchNasaData()
        }.fold(
            onSuccess = { nasaImageModelState.value = Resource.Success(it) },
            onFailure = { nasaImageModelState.value = Resource.Error(throwable = it) }
        )
    }

    fun fetchImages() = viewModelScope.launch {
        nasaImageModelState.value = Resource.Loading
        resultApiCall(dispatchers) {
            loadJSONFromAsset(context)
        }.fold(
            onSuccess = {
                nasaImageModelState.value = Resource.Success(
                    gson.fromJson(it, object : TypeToken<List<RespNasaDataItem?>?>() {}.type)
                )
            },
            onFailure = { nasaImageModelState.value = Resource.Error(throwable = it) }
        )
    }

    private fun loadJSONFromAsset(context: Context): String? {
        val json: String? = try {
            val stream: InputStream = context.assets.open("data.json")
            val size: Int = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}
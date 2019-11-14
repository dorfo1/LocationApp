package br.com.locationapp.viewmodel

import android.app.Application
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.*
import br.com.locationapp.repositories.LocationRepository
import java.util.*


class MainViewModel(application: Application) : AndroidViewModel(application) {

    val context = application

    val location: LiveData<Location> get() = LocationRepository.location

    val latitude: LiveData<String> = Transformations.switchMap(location) { location ->
        transformLiveData(location.latitude)
    }
    val longitude: LiveData<String> = Transformations.switchMap(location) { location ->
        transformLiveData(location.longitude)
    }


    val endereco: LiveData<String> = Transformations.switchMap(location) { location ->
        gerarEnderecoLatLong(location.latitude,location.longitude)
    }


    val localizando: MutableLiveData<Boolean> = MutableLiveData()


    init {
        localizando.value = false
    }

    private fun transformLiveData(double: Double): LiveData<String> {
        return MutableLiveData<String>().apply {
            value = double.toString()
        }
    }

    fun setLocalizando(b: Boolean) {
        localizando.value = b
    }

    private fun gerarEnderecoLatLong(latitude: Double, longitude: Double) : LiveData<String> {
        val geocoder = Geocoder(context, Locale.getDefault())
        val endereco = geocoder.getFromLocation(latitude,longitude,1)[0]

        return MutableLiveData<String>().apply {
            value = endereco.getAddressLine(0)
        }
    }


}
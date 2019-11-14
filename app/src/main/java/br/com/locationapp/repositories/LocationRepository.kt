package br.com.locationapp.repositories

import android.location.Location
import androidx.lifecycle.MutableLiveData

object LocationRepository{

    private var _location : MutableLiveData<Location> = MutableLiveData()
    val location
        get() = _location



    fun setLocation(location: Location){
        _location.postValue(location)
    }

}
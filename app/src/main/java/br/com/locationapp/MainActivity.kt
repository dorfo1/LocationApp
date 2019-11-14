package br.com.locationapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.locationapp.databinding.ActivityMainBinding
import br.com.locationapp.service.LocationService
import br.com.locationapp.utils.PermissionUtils
import br.com.locationapp.viewmodel.MainViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    val TAG = "TAG"
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            lifecycleOwner = this@MainActivity
            viewmodel = mainViewModel
            listener = clickListener

        }

        val fragmentMap =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fragmentMap.getMapAsync(this)

        setupObserver()
    }

    private fun setupObserver() {
        mainViewModel.location.observe(this, Observer {
            val latLng = LatLng(it.latitude, it.longitude)
            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Eu")
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        })
    }

    val clickListener = object : LocalizarClick {
        override fun onLocalizarClick() {
            if (verificaPermissao()) {
                if (!mainViewModel.localizando.value!!)
                    startLocationService()
                else
                    stopLocationService()
            } else {
                PermissionUtils.validarPermissoes(
                    listOf(Manifest.permission.ACCESS_FINE_LOCATION), this@MainActivity, 1
                )
            }
        }
    }

    private fun startLocationService() {
        Log.d(TAG, "Começando serviço")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this@MainActivity, LocationService::class.java))
        } else {
            startService(Intent(this@MainActivity, LocationService::class.java))
        }
        mainViewModel.setLocalizando(true)
    }


    private fun stopLocationService() {
        Log.d(TAG, "Parando serviço")
        stopService(Intent(this@MainActivity, LocationService::class.java))
        mainViewModel.setLocalizando(false)
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            mMap = it
        }
    }


    private fun verificaPermissao(): Boolean {
        return ContextCompat.checkSelfPermission(
            this@MainActivity,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    interface LocalizarClick {
        fun onLocalizarClick()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                startService(Intent(this@MainActivity, LocationService::class.java))
            } else {
                Toast.makeText(this@MainActivity, "Permissão negada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

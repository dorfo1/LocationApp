package br.com.locationapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import br.com.locationapp.R
import br.com.locationapp.repositories.LocationRepository
import com.google.android.gms.location.*

private const val TAG = "TAG"
private const val INTEREVALO =  10 * 1000L
private const val INTERVALO_RAPIDO = 5 * 1000L
private const val channel_name = "channel_name"
private const val channel_id = "channel_id"

class LocationService : Service() {



    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                channel_id, channel_name, NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableLights(true)
                enableVibration(true)
            }


            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)


            val notification = NotificationCompat.Builder(this, channel_id)
                .setContentTitle("Localização")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText("Seu gps está pegando sua localização").build()

            notificationManager.notify(1, notification)
            startForeground(1, notification)
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        setupLocationUpdates()
        startLocationUpdates()
        return START_NOT_STICKY
    }

    private fun setupLocationUpdates() {
        locationRequest = LocationRequest()
        locationRequest.interval = INTEREVALO
        locationRequest.fastestInterval = INTERVALO_RAPIDO
        locationRequest.smallestDisplacement = 170f
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    LocationRepository.setLocation(location)
                    Log.d(TAG, "${location.latitude}/${location.longitude} ")
                }
            }
        }
    }

    private fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, null
        )
    }


    override fun stopService(name: Intent?): Boolean {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        return true
    }


}
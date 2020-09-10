package com.example.geoscr

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.OnSuccessListener

class MainActivity : AppCompatActivity()
{
    private val permisoFineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    private val permisoCoarseLociation = Manifest.permission.ACCESS_COARSE_LOCATION
    private val CODIGO_SOLICITUD_PERMISO = 100
    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationRequest: LocationRequest? = null


    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    fusedLocationClient = FusedLocationProviderClient(this)
    inicializarLocationRequest()
    }
    private fun inicializarLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest?.interval = 10000
        locationRequest?.fastestInterval = 5000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    private fun validarPermisosUbicacion():Boolean{
        val hayUbicacionPrecisa = ActivityCompat.checkSelfPermission(this, permisoFineLocation) == PackageManager.PERMISSION_GRANTED
        val hayUbicacionOrdinaria = ActivityCompat.checkSelfPermission(this, permisoCoarseLociation) == PackageManager.PERMISSION_GRANTED
        return hayUbicacionPrecisa && hayUbicacionOrdinaria
    }
    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion(){

        /*fusedLocationClient?.lastLocation?.addOnSuccessListener(this, object: OnSuccessListener<Location> {
             override fun onSuccess(location: Location?) {
                if(location != null){
                    Toast.makeText(applicationContext, location?.latitude.toString() + " - " + location?.longitude.toString(), Toast.LENGTH_LONG).show()
                }
            }
        })*/
       val callback = object: LocationCallback(){
           override fun onLocationResult(locationResult: LocationResult?) {
               super.onLocationResult(locationResult)
               for(ubicacion in locationResult?.locations!!){
                    Toast.makeText(applicationContext, ubicacion.latitude.toString() + " , " + ubicacion.longitude.toString(),Toast.LENGTH_LONG).show()
               }
           }
       }
        fusedLocationClient?.requestLocationUpdates(locationRequest, callback, null)


    }
    private fun pedirPermisos() {
        val proveerContexto = ActivityCompat.shouldShowRequestPermissionRationale(this, permisoFineLocation)
        if (proveerContexto) {
            //Enviar un mensaje al usuario con informacion adicional
            solicitudPermiso()
        } else {
            solicitudPermiso()
        }
    }


    private fun solicitudPermiso() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(permisoFineLocation, permisoCoarseLociation), CODIGO_SOLICITUD_PERMISO)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CODIGO_SOLICITUD_PERMISO ->{
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // Obtener ubicacion
                    obtenerUbicacion()
                } else {
                    Toast.makeText(this, "No diste permiso para acceder a tu ubicacion", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    override fun onStart() {
      super.onStart()
      if(validarPermisosUbicacion()){
          obtenerUbicacion()
      } else{
          pedirPermisos()
      }
    }



    }

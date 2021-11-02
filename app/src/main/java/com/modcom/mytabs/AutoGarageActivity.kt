package com.modcom.mytabs

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.AppOpsManagerCompat
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.modcom.mytabs.databinding.ActivityAutoGarageBinding
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class AutoGarageActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityAutoGarageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAutoGarageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //GPS - Global Positioninf System
        //GPS works withn satelites
        //GPS uses lat and long


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        GPS()

        //kotlin to access python at this point
        //retrieve all latitudes and logitudes from databases
        //server will run at 127.0.0.1:5000/locations
        //we can host your online - best

        val client = AsyncHttpClient(true, 80,443)
        client.get("https://modcom.pythonanywhere.com/api/all", object : JsonHttpResponseHandler()
        {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?,
                response: JSONArray) {
                //we use a for loop
                for(i in 0 until response.length()){
                     val jsonObject = response.getJSONObject(i)
                     val lat = jsonObject.optString("lat").toDouble()
                     val lon = jsonObject.optString("lon").toDouble()
                     val name = jsonObject.optString("name").toString()
                    //map the cordinates and loop again
                    val gr = LatLng(lat, lon)
                    mMap.addMarker(MarkerOptions()
                        .position(gr)
                        .title(name)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)))
                    //modcom.co.ke/android_oct
                    //GPS
                }
            }//end success
            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?,
                throwable: Throwable?) {
                Toast.makeText(applicationContext,"Failed to Load",Toast.LENGTH_LONG).show()
            }//end failure
        })
    }

    //GPS function
    fun GPS(){
        //check permissions
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }//end if9


        //Get user position
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) {
            location->
            if(location!=null) {
                val currentLocation = LatLng(location.latitude, location.longitude)
                mMap.addMarker(
                    MarkerOptions()
                        .position(currentLocation)
                        .title("Am here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                )//close marker
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
            }//end if
            else {
                Toast.makeText(applicationContext,"No Location, Activate GPS", Toast.LENGTH_LONG).show()
            }
        }//end listener
    }//end GPS function

}
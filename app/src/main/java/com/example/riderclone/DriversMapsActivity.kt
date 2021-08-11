package com.example.riderclone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.Window
import android.widget.Switch

import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.example.riderclone.databinding.ActivityDriversMapsBinding
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DriversMapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,com.google.android.gms.location.LocationListener
{
    private var CurrentLogoutDriver:Boolean = false
    private lateinit var mMap: GoogleMap
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var location: Location
    private lateinit var locationRequest: LocationRequest
    private lateinit var binding: ActivityDriversMapsBinding
    private var mAuth: FirebaseAuth? = null
    private  var DriverId:String = ""
    private  var CoustomerId:String =  ""
    private lateinit var pickMarker: Marker
    private lateinit var AssignedCustomer:DatabaseReference
    private lateinit var AssignedCustomerPickupreffencLis:ValueEventListener
    private lateinit var AssignedCustomerPickupreffence:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Hide Action Bar....
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar()!!.hide(); // hide the title bar
        DriverId = mAuth!!.currentUser!!.uid

        //enable full screen
        val logout_driver:MaterialButton = findViewById(R.id.logout_Driver)
        var setting_Button:MaterialButton = findViewById(R.id.Setting_button)
        binding = ActivityDriversMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setting_Button.setOnClickListener {

            val intent = Intent(this@DriversMapsActivity,settting::class.java)
            intent.putExtra("type", "Customers")
            startActivity(intent)

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mAuth = FirebaseAuth.getInstance()
        mAuth!!.currentUser
        logout_driver.setOnClickListener{
            CurrentLogoutDriver = true
            DisconnectDriver()
            mAuth!!.signOut()
            LogOutDriver()
        }
        GetAssignedCustomerRequest()

    }
  // in this function  we basecally recipe coustomer for id ......
    private fun GetAssignedCustomerRequest() {
    AssignedCustomer = FirebaseDatabase.getInstance().getReference()
        .child("Users").child("Drivers").child(DriverId).child("CustomerRideId")
  AssignedCustomer.addValueEventListener(object:ValueEventListener{
      override fun onDataChange(p0: DataSnapshot) {
          if (p0.exists())
          {
              CoustomerId =p0.value.toString()
              getAssignedCustomerPickuplocation()
          }
          else {
              CoustomerId = ""
              if (pickMarker!=null)
              {
                  pickMarker.remove()
              }
              if (AssignedCustomerPickupreffencLis!=null)
              {
                  AssignedCustomerPickupreffence.removeEventListener(AssignedCustomerPickupreffencLis)

              }
          }
      }

      override fun onCancelled(p0: DatabaseError) {

      }

  })

    }

    private fun getAssignedCustomerPickuplocation() {
        AssignedCustomerPickupreffence = FirebaseDatabase.getInstance().getReference().child("customer request").
        child(CoustomerId).child("l")
        AssignedCustomerPickupreffencLis =AssignedCustomerPickupreffence.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    val CustomerLocatioMap:List<String> = p0.getValue() as List<String>

                    var locatiolat:Double = 0.0
                    var locatiolatop:Double = 0.0





                    if (CustomerLocatioMap[0] != null )
                    {
                        locatiolat= (CustomerLocatioMap[0].toString()).toDouble()

                    }
                    if (CustomerLocatioMap.get(1)!= null )
                    {
                        locatiolatop = (CustomerLocatioMap[1].toString()).toDouble()
                    }
                    var Driverlating = LatLng(locatiolat, locatiolatop)
                   mMap.addMarker(MarkerOptions().position(Driverlating).title(" customer pickup location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_person_24)))


                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        BuildgoogleApi()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        mMap.isMyLocationEnabled = true


    }
fun  LogOutDriver()
{

    val intent_logout =  Intent(this,MainActivity::class.java )
    intent_logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK )
    startActivity(intent_logout)
    finish()
}
    override fun onConnected(p0: Bundle?) {
        locationRequest.setInterval(1000)
        locationRequest.setFastestInterval(1000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this )

    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(location221: Location) {

        if (applicationContext != null) {

            var location221 = location
            val latudelocation = LatLng(location221.latitude, location221.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latudelocation))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12f))

            var UserIdDriver: String = FirebaseAuth.getInstance().currentUser!!.uid
            var driverAvality: DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Drives Available")

            var gooDriverAvility  = GeoFire(driverAvality)
            var DRIVERWorkingref:DatabaseReference = FirebaseDatabase.getInstance().getReference().child(" Driver Working")
            var gooDriverAvilityWorking  = GeoFire(DRIVERWorkingref)
             var switchcase = when(CoustomerId)
             {
                 "" ->{
                     gooDriverAvilityWorking.removeLocation(UserIdDriver)
                     gooDriverAvility .setLocation(UserIdDriver, GeoLocation(location221.latitude, location221.longitude))

                 }
                 else  ->
                 {
                     gooDriverAvility.removeLocation(UserIdDriver)
                     gooDriverAvilityWorking.setLocation(UserIdDriver, GeoLocation(location221.latitude, location221.longitude))

                 }
             }

        }
    }



    fun BuildgoogleApi()

{
    var googleapiclient =GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API)
        .build()
    googleapiclient.connect()


}
  override fun onStop()

{
    super.onStop()
  if (! CurrentLogoutDriver)
  {
      DisconnectDriver()
  }

}


fun DisconnectDriver()
{
    var UserIdDriver:String = FirebaseAuth.getInstance().currentUser!!.uid
    var driverAvality: DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Drives Available")

    var goo=GeoFire(driverAvality)
    goo.removeLocation(UserIdDriver)
}



}
package com.example.riderclone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.riderclone.databinding.ActivityCustomerMapsBinding
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import com.google.firebase.database.DataSnapshot as DataSnapshot1
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.database.DatabaseReference
import com.squareup.picasso.Picasso
import android.widget.RelativeLayout

import android.widget.TextView





class customermap : AppCompatActivity(), OnMapReadyCallback , GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,com.google.android.gms.location.LocationListener {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityCustomerMapsBinding
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var location: Location
    private lateinit var locationRequest: LocationRequest
    private var mAuth: FirebaseAuth? = null
    private lateinit var DriverAvilableLocationReference: DatabaseReference
    private lateinit var CustomerRef: DatabaseReference
    private lateinit var pickuplocation: LatLng
    private var radius: Int = 1
    private var Driver_Found: Boolean = false
    private var reQuestType: Boolean = false
    private lateinit var DriverLocationRef: ValueEventListener
    private lateinit var DriverId: String
    private lateinit var geoQuery: GeoQuery
    private lateinit var CustomerId: String
    private lateinit var goo: GeoFire
    private lateinit var databaseRef_For_Driver: DatabaseReference
    private lateinit var cab_hire_button: MaterialButton
    private lateinit var Drive_location_ref: DatabaseReference
    private lateinit var Drive_ref: DatabaseReference
    private lateinit var pickMarker: Marker
    private var txtName: TextView? = findViewById(R.id.name)
    private var txtPhone: TextView? = findViewById(R.id.phone_number)
    private var txtCarName: TextView? = findViewById(R.id.driver_car_name)
    private val profilePic: ImageView? = findViewById(R.id.profile_image)
    private val relativeLayout: RelativeLayout? = null
    private lateinit var DriverMarker: Marker
    private lateinit var location_for_frist: Location
    private lateinit var location_for_two: Location

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        var Setting_button_customer: MaterialButton = findViewById(R.id.Setting_button)
        binding = ActivityCustomerMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar!!.hide(); // hide the title bar
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mAuth = FirebaseAuth.getInstance()
        mAuth!!.getCurrentUser()
        CustomerId = FirebaseAuth.getInstance().getCurrentUser()!!.getUid();
        Drive_location_ref = FirebaseDatabase.getInstance().getReference().child("Drivers Working ")
        DriverAvilableLocationReference =
            FirebaseDatabase.getInstance().getReference().child("Driver Available....")
        CustomerRef = FirebaseDatabase.getInstance().getReference().child("customers Request")

        Setting_button_customer.setOnClickListener {

            val intent = Intent(this@customermap, settting::class.java)
            intent.putExtra("type", "Customers")
            startActivity(intent)

        }


        var customer_logout_Button: MaterialButton = findViewById(R.id.logout_Customer)
        customer_logout_Button.setOnClickListener {
            mAuth!!.signOut()
            LogoutCustomer()
        }
        cab_hire_button = findViewById(R.id.cab_hire_button)
        cab_hire_button.setOnClickListener {

            if (reQuestType) {
                reQuestType = false
                geoQuery.removeAllListeners()
                Drive_location_ref.removeEventListener(DriverLocationRef)

                if (Driver_Found != null) {
                    Drive_ref = FirebaseDatabase.getInstance().getReference().child("Users")
                        .child("Drivers").child(DriverId).child("CustomerRideId")
                    Drive_ref.setValue(true)
                    Drive_ref.removeValue()
                    DriverId = null.toString()

                }
                Driver_Found = false
                radius = 1
                CustomerId = FirebaseAuth.getInstance().currentUser!!.uid
                goo = GeoFire(CustomerRef)
                goo.removeLocation(CustomerId)
                if (pickMarker != null) {
                    pickMarker.remove()
                }
                if (DriverMarker != null) {
                    DriverMarker.remove()
                }
                cab_hire_button.setText("Call a Cab")


            } else {
                reQuestType = true
                CustomerId = FirebaseAuth.getInstance().currentUser!!.uid
                goo = GeoFire(CustomerRef)
                val lastlocation = location
                goo.setLocation(
                    CustomerId,
                    GeoLocation(lastlocation.latitude, lastlocation.longitude)
                )
                pickuplocation = LatLng(lastlocation.latitude, lastlocation.longitude)
                mMap.addMarker(
                    MarkerOptions().position(pickuplocation).title("My Location  ")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_pickup_people_24))
                )


                cab_hire_button.text = "Getting your Driver....."
                GetCloserDriverCab()
            }
        }
    }


    private fun GetCloserDriverCab() {
        val gool1 = GeoFire(DriverAvilableLocationReference)
        geoQuery = gool1.queryAtLocation(
            GeoLocation(pickuplocation.latitude, pickuplocation.longitude),
            radius.toDouble()

        )
        geoQuery.removeAllListeners()

        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String?, location: GeoLocation?) {


                if (!Driver_Found && reQuestType) {

                    Driver_Found = true
                    if (key != null) {
                        DriverId = key
                    }
                    Drive_ref =
                        FirebaseDatabase.getInstance().reference.child("User").child("Drivers")
                            .child(DriverId)
                    val hashMap: HashMap<String, Any> =
                        HashMap<String, Any>() //define empty hashmap
                    hashMap.put("CustomerRideId", CustomerId)
                    Drive_ref.updateChildren(hashMap)

                    GetInputDriverlocation()
                    cab_hire_button.text = "Looking for Driver Location "
                }

            }

            override fun onKeyExited(key: String?) {

            }

            override fun onKeyMoved(key: String?, location: GeoLocation?) {

            }

            override fun onGeoQueryReady() {
                if (!Driver_Found) {
                    radius += 1
                    GetCloserDriverCab()
                }

            }

            override fun onGeoQueryError(error: DatabaseError?) {

            }

        })
    }

    //and then we get to the driver location - to tell customer where is the driver
    private fun GetInputDriverlocation() {
        DriverLocationRef =
            Drive_location_ref.child(DriverId).child("l").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(p0: DataSnapshot1) {
                    if (p0.exists() && reQuestType) {
                        // datasnap list  problem .....

                        val Driverlodationmap: List<String> = p0.getValue() as List<String>
                        var locatiolat: Double = 0.0
                        var locatiolatop: Double = 0.0
                        cab_hire_button.setText("Drivers found ....")

                        getAssignedDriverInformation()


                        if (Driverlodationmap[0] != null) {
                            locatiolat = (Driverlodationmap[0].toString()).toDouble()

                        }
                        if (Driverlodationmap.get(1) != null) {
                            locatiolatop = (Driverlodationmap[1].toString()).toDouble()
                        }
                        var Driverlating = LatLng(locatiolat, locatiolatop)

                        if (DriverMarker != null) {
                            DriverMarker.remove()
                        } else {


                            location_for_frist.latitude = pickuplocation.latitude
                            location_for_frist.longitude = pickuplocation.longitude

                            location_for_two.longitude = Driverlating.longitude
                            location_for_two.latitude = Driverlating.latitude

                            var Distance: Float = location_for_frist.distanceTo(location_for_two)
                            if (Distance < 90) {

                                cab_hire_button.setText("Driver Reached")

                            } else {
                                cab_hire_button.setText("Driver found   $Distance.toString()")
                            }




                            DriverMarker = mMap.addMarker(
                                MarkerOptions().position(Driverlating).title("YOur drive is here ")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_directions_car_24))
                            )
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
    }


    // override _1
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

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
        BuildgoogleApi()
        mMap.isMyLocationEnabled = true


    }

    // override -- 2
    override fun onConnected(p0: Bundle?) {

        locationRequest.interval = 1000
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        // Logout Button  for Customer .......


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
        LocationServices.FusedLocationApi.requestLocationUpdates(
            googleApiClient,
            locationRequest,
            this
        )

    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(location221: Location) {

        //getting the updated location
        location = location221
        val latudelocation = LatLng(location221.latitude, location221.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latudelocation))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12f))


    }

    //create this method -- for useing apis
    fun BuildgoogleApi() {
        val googleapiclient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        googleapiclient.connect()


    }

    override fun onStop() {
        super.onStop()


    }


    fun LogoutCustomer() {
        val intent_logout = Intent(this, MainActivity::class.java)
        intent_logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent_logout)
        finish()
    }


    // Driver Information ...... name and driver
    private fun getAssignedDriverInformation() {
        val reference = FirebaseDatabase.getInstance().reference
            .child("Users").child("Drivers").child(DriverId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val name = dataSnapshot.child("name").value.toString()
                    val phone = dataSnapshot.child("phone").value.toString()
                    val car = dataSnapshot.child("car").value.toString()
                    txtName!!.setText(name)
                    txtPhone!!.setText(phone)
                    txtCarName!!.setText(car)
                    if (dataSnapshot.hasChild("image")) {
                        val image = dataSnapshot.child("image").value.toString()
                        Picasso.get().load(image).into(profilePic)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}

//private fun GeoQuery.addGeoQueryEventListener:GeoQueryEventListener {}() {

//}



package com.example.riderclone


import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.storage.StorageReference

import com.google.firebase.storage.StorageTask

import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.DatabaseReference

import android.widget.TextView

import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import android.content.Intent
import android.view.View
import android.text.TextUtils


import com.google.android.gms.tasks.OnCompleteListener

import android.app.ProgressDialog
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask

import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage


class settting : AppCompatActivity() {

    private var getType: String? = null


    private var nameEditText: EditText? = null
    private  var phoneEditText:EditText? = null
    private var profileImageView:ImageView? = null
     private var driverCarName:EditText? =null
    private var profileChangeBtn: TextView? = findViewById(R.id.change_picture_btn)

    private var databaseReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null

    private var checker = ""
    private var imageUri: Uri? = null
    private var myUrl = ""
    private var uploadTask: StorageTask<*>? = null
    private var storageProfilePicsRef: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settting)
        getType = getIntent().getStringExtra("type")
        Toast.makeText(this, getType, Toast.LENGTH_SHORT).show()


        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(
            getType!!
        )
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures")





     profileImageView= findViewById(R.id.profile_image)

        nameEditText = findViewById(R.id.name);
        phoneEditText = findViewById(R.id.phone_number)

         driverCarName = findViewById(R.id.driver_car_name)


        if (getType.equals("Drivers"))
        {
            with(driverCarName) { this?.setVisibility(View.VISIBLE) }
        }

       var closeButton:ImageView = findViewById(R.id.close_button)
        var saveButton:ImageView = findViewById(R.id.save_button)






//close button ....


        closeButton.setOnClickListener {

                if (getType == "Drivers") {
                    startActivity(Intent(this@settting, DriversMapsActivity::class.java))
                } else {
                    startActivity(Intent(this@settting, customermap::class.java))
                }
            }

        saveButton.setOnClickListener {
            if (checker == "clicked") {
                validateControllers()
            } else {
                validateAndSaveOnlyInformation()
            }
        }
        profileChangeBtn!!.setOnClickListener(View.OnClickListener {
            checker = "clicked"
            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this@settting)
        })

        getUserInformation()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==CropImage) {
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            imageUri = result.getUri()
            profileImageView!!.setImageURI(imageUri)
        } else {
            if (getType == "Drivers") {
                startActivity(Intent(this@settting ,DriversMapsActivity::class.java))
            } else {
                startActivity(Intent(this@settting , customermap::class.java))
            }
            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun validateControllers() {
        if (TextUtils.isEmpty(nameEditText!!.text.toString())) {
            Toast.makeText(this, "Please provide your name.", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(phoneEditText!!.text.toString())) {
            Toast.makeText(this, "Please provide your phone number.", Toast.LENGTH_SHORT).show()
        } else if (getType == "Drivers" && TextUtils.isEmpty(driverCarName?.getText().toString())) {
            Toast.makeText(this, "Please provide your car Name.", Toast.LENGTH_SHORT).show()
        } else if (checker == "clicked") {
            uploadProfilePicture()
        }
    }

    private fun uploadProfilePicture() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Settings Account Information")
        progressDialog.setMessage("Please wait, while we are settings your account information")
        progressDialog.show()
        if (imageUri != null) {
            val fileRef = storageProfilePicsRef
                ?.child(mAuth!!.currentUser!!.uid + ".jpg")
            uploadTask = fileRef!!.putFile(imageUri!!)



            (uploadTask as UploadTask).continueWithTask(object:Continuation(),
                Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                override fun then(p0: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                    if (!p0.isSuccessful())
                    {
                        throw p0.exception!!;
                    }

                    return fileRef.getDownloadUrl();
                }


            }).addOnCompleteListener (object:OnCompleteListener<Uri>{
                override fun onComplete(p0: Task<Uri>) {

                    if(p0.isSuccessful)
                    {
                        val downloadUrl: Uri = p0.getResult()
                        myUrl = downloadUrl.toString()
                    }
                    val userMap: HashMap<String, Any> = HashMap()
                    userMap["uid"] = mAuth!!.currentUser!!.uid
                    userMap["name"] = nameEditText!!.text.toString()
                    userMap["phone"] = phoneEditText!!.text.toString()
                    userMap["image"] = myUrl


                    if (getType == "Drivers") {
                        userMap["car"] = driverCarName!!.text.toString()
                    }

                    databaseReference!!.child(mAuth!!.currentUser!!.uid).updateChildren(userMap)

                    progressDialog.dismiss()

                    if (getType == "Drivers") {
                        startActivity(Intent(this@settting, DriversMapsActivity::class.java))
                    } else {
                        startActivity(
                            Intent(
                                this@settting,
                                customermap::class.java
                            )
                        )
                    }

                }

                })
            }
        else
        {
            Toast.makeText(this, "Image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }


    private fun validateAndSaveOnlyInformation() {
        if (TextUtils.isEmpty(nameEditText!!.text.toString())) {
            Toast.makeText(this, "Please provide your name.", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(phoneEditText!!.text.toString())) {
            Toast.makeText(this, "Please provide your phone number.", Toast.LENGTH_SHORT).show()
        } else if (getType == "Drivers" && TextUtils.isEmpty(driverCarName!!.text.toString())) {
            Toast.makeText(this, "Please provide your car Name.", Toast.LENGTH_SHORT).show()
        } else {
            val userMap: HashMap<String, Any> = HashMap()
            userMap["uid"] = mAuth!!.currentUser!!.uid
            userMap["name"] = nameEditText!!.text.toString()
            userMap["phone"] = phoneEditText!!.text.toString()
            if (getType == "Drivers") {
                userMap["car"] = driverCarName!!.text.toString()
            }
            databaseReference!!.child(mAuth!!.currentUser!!.uid).updateChildren(userMap)
            if (getType == "Drivers") {
                startActivity(Intent(this@settting, DriversMapsActivity::class.java))
            } else {
                startActivity(Intent(this@settting, customermap::class.java))
            }
        }
    }
    private fun getUserInformation() {
        databaseReference!!.child(mAuth!!.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                        val name = dataSnapshot.child("name").value.toString()
                        val phone = dataSnapshot.child("phone").value.toString()
                        nameEditText!!.setText(name)
                        phoneEditText!!.setText(phone)
                        if (getType == "Drivers") {
                            val car = dataSnapshot.child("car").value.toString()
                            driverCarName!!.setText(car)
                        }
                        if (dataSnapshot.hasChild("image")) {
                            val image:String = dataSnapshot.child("image").value.toString()
                            Picasso.get().load(image).into(profileImageView);
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {


                }
            })
    }
}

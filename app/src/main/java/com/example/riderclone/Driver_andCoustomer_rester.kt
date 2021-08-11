package com.example.riderclone

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class Driver_andCoustomer_rester : AppCompatActivity() {

// Driver ragister .... from firebase ...
    private var mAuth: FirebaseAuth? = null
    private lateinit var DriverDatabaseRef: DatabaseReference
    private lateinit var OnlineDriverId:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_and_coustomer_rester)
        ragister_all_Driver()
        OnlineDriverId = mAuth!!.currentUser!!.uid
        mAuth = FirebaseAuth.getInstance()
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar()!!.hide(); // hide the title bar

    }
    fun ragister_all_Driver()

    {
        val password_ragister:TextInputEditText = findViewById(R.id.pawword_Ragister_Edittext)
        val email_ragister:TextInputEditText =  findViewById(R.id.Ragister_Email_edittext)
        val Ragister_Button:TextInputEditText = findViewById(R.id.Setting_button)
        Ragister_Button.setOnClickListener{
            val email_Ragister_Edittext:String = email_ragister.getText().toString()
            val password_ragister:String = password_ragister.getText().toString()

            ragister_Driver_Customer(email_Ragister_Edittext, password_ragister)

        }


    }

    private fun ragister_Driver_Customer(emailRagisterEdittext: String, passwordRagister: String) {
        /* empty edit text .... if user is not enter email */
        if (TextUtils.isEmpty(emailRagisterEdittext))
        {
             Toast.makeText(this, "PLease Enter your email sir ", Toast.LENGTH_SHORT).show()
        }
        if (TextUtils.isEmpty(passwordRagister))
        {
            Toast.makeText(this, "PLease Enter your password sir ", Toast.LENGTH_SHORT).show()
        }
        else {
            val ProgressDilog = ProgressDialog(this@Driver_andCoustomer_rester)
            ProgressDilog.setTitle(" Driver Ragister ")
            ProgressDilog.setMessage("Please Wait...")
            ProgressDilog.show()

            mAuth!!.createUserWithEmailAndPassword(emailRagisterEdittext!!, passwordRagister!!)
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful)

                    {
                        OnlineDriverId = mAuth!!.currentUser!!.uid
                        DriverDatabaseRef = FirebaseDatabase.getInstance().reference.child("User").child("Drivers").child(OnlineDriverId)
                        DriverDatabaseRef.setValue(true)
                 Toast.makeText(this, "Ragister successfully " , Toast.LENGTH_LONG).show()
                 ProgressDilog.dismiss()
                        val intent_Driver_map_activity = Intent(this, DriversMapsActivity::class.java)
                        startActivity(intent_Driver_map_activity)

                    }
                    else {
                        Toast.makeText(this, "Ragister  is not successfully " , Toast.LENGTH_LONG).show()
                        ProgressDilog.dismiss()
                    }
                }
        }
    }

}
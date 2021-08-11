package com.example.riderclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Customer_Ragister_Account : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private lateinit var CustomerDatabaseRef: DatabaseReference
    private lateinit var OnlineCustomerId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_ragister_account)
        ragister_for_all_Customer()
        mAuth = FirebaseAuth.getInstance()

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar!!.hide(); // hide the title bar


    }

    fun ragister_for_all_Customer()

    {
        val password_ragister: TextInputEditText = findViewById(R.id.pawword_Ragister_Edittext)
        val email_ragister: TextInputEditText =  findViewById(R.id.Ragister_Email_edittext)
        val Ragister_Button: TextInputEditText = findViewById(R.id.Setting_button)
        Ragister_Button.setOnClickListener{
            val email_Ragister_Edittext:String = email_ragister.getText().toString()
            val password_ragister:String = password_ragister.getText().toString()

            ragister_Customer(email_Ragister_Edittext, password_ragister)

        }


    }

    private fun ragister_Customer(emailRagisterEdittext: String, passwordRagister: String) {
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
            val ProgressDilog = ProgressDialog(this@Customer_Ragister_Account)
            ProgressDilog.setTitle(" Customer Ragister ")
            ProgressDilog.setMessage("Please Wait...")
            ProgressDilog.show()

            mAuth!!.createUserWithEmailAndPassword(emailRagisterEdittext!!, passwordRagister!!)
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful)
                    {
                        OnlineCustomerId = mAuth!!.currentUser!!.uid
                        CustomerDatabaseRef= FirebaseDatabase.getInstance().reference.child("Users").child("Customers").child(OnlineCustomerId)
                        CustomerDatabaseRef.setValue(true)
                        val IntentCustomer = Intent(this ,customermap::class.java )
                        startActivity(IntentCustomer)
                        Toast.makeText(this, "Ragister successfully " , Toast.LENGTH_LONG).show()
                        ProgressDilog.dismiss()
                    }
                    else {
                        Toast.makeText(this, "Ragister  is not successfully " , Toast.LENGTH_LONG).show()
                        ProgressDilog.dismiss()
                    }
                }
        }
    }


}
package com.example.riderclone

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class driverandcustomerelcome: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar()!!.hide(); // hide the title bar


        val Customer_start_button: MaterialButton = findViewById(R.id.LogIn_As_Customer)
        val Driver_start_Button:MaterialButton = findViewById(R.id.login_as_Driver)
        Customer_start_button.setOnClickListener{
            var Customer_intent = Intent( this , Customerlogin::class.java )
        }

        Driver_start_Button.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java )


                startActivity(intent)

        }

    }
}
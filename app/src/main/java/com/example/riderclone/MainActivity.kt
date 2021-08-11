package com.example.riderclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar()!!.hide(); // hide the title bar
        val start_BUtton:MaterialButton = findViewById(R.id.Start_button_for_App)
        start_BUtton.setOnClickListener{
            val intent =  Intent(this,driverandcustomerelcome::class.java )
            startActivity(intent)

        }

    }

}
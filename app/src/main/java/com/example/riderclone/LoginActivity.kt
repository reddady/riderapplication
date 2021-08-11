package com.example.riderclone

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class LoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private lateinit var DriverDatabaseRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar()!!.hide(); // hide the title bar
        mAuth = FirebaseAuth.getInstance()
        // sing in and sing up button .....
        val Button_SingIn: MaterialButton = findViewById(R.id.Sing_IN_Driver)
        val button_singup: MaterialButton = findViewById(R.id.Setting_button)
        val Driver_Email_edittext: TextInputEditText = findViewById(R.id.Driver_Email_edittext)
        val Driver_Edittext_password: TextInputEditText =
            findViewById(R.id.driver_password_edittext)

        // forget password ....

        // sing up  page go for creat account  ...... as driver and customer .....
        button_singup.setOnClickListener {
            val intent_Resister_customer_Driver =
                Intent(this, Driver_andCoustomer_rester::class.java)
            startActivity(intent_Resister_customer_Driver)
        }
        Button_SingIn.setOnClickListener {
            val email = Driver_Email_edittext.getText().toString()
            val password = Driver_Edittext_password.getText().toString()
            loginDriver(email, password)
        }
    }

    private fun loginDriver(email: String, password: String) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "PLease Enter your email sir ", Toast.LENGTH_SHORT).show()
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "PLease Enter your password sir ", Toast.LENGTH_SHORT).show()
        } else {
            val ProgressDilog = ProgressDialog(this@LoginActivity)
            ProgressDilog.setTitle(" Driver login ")
            ProgressDilog.setMessage("Please Wait...")
            ProgressDilog.show()
            mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // next page ......
                        Toast.makeText(this, "Driver log in successfully ", Toast.LENGTH_LONG).show()
                        ProgressDilog.dismiss()
                        val intent_Driver_map_activity = Intent(this, DriversMapsActivity::class.java)
                        startActivity(intent_Driver_map_activity)
                    } else {
                        Toast.makeText(this, "log in  is not successfully ", Toast.LENGTH_LONG)
                            .show()
                        ProgressDilog.dismiss()
                    }
                }
        }

    }
}
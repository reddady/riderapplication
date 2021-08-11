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



class Customerlogin : AppCompatActivity() {

        private var mAuth: FirebaseAuth? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            mAuth = FirebaseAuth.getInstance()
            requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
            supportActionBar!!.hide(); // hide the title bar
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_customer_login)
            val Button_SingIn: MaterialButton = findViewById(R.id.customer_singIN_button)
            val button_singup: MaterialButton = findViewById(R.id.custom_singUP_button)
            val Custoer_email_login: TextInputEditText = findViewById(R.id.Customer_Email_login)
            val Customer_password_login: TextInputEditText =
                findViewById(R.id.Customer_password_Edittext)
            button_singup.setOnClickListener {
                val intent_cutomer = Intent(this, Customer_Ragister_Account::class.java)
                startActivity(intent_cutomer)
            }
            Button_SingIn.setOnClickListener {
                val email_Customer = Custoer_email_login.getText().toString()
                val password_Customer = Customer_password_login.getText().toString()
                loginCustomer(email_Customer, password_Customer)
            }
        }

        private fun loginCustomer(email_Customer: String, password_Customer: String) {
            if (TextUtils.isEmpty(email_Customer)) {
                Toast.makeText(this, "PLease Enter your email sir ", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(password_Customer)) {
                Toast.makeText(this, "PLease Enter your password sir ", Toast.LENGTH_SHORT).show()
            } else {
                val ProgressDilog = ProgressDialog(this@Customerlogin)
                ProgressDilog.setTitle(" Customer login ")
                ProgressDilog.setMessage("Please Wait...")
                ProgressDilog.show()
                mAuth!!.signInWithEmailAndPassword(email_Customer, password_Customer)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // next page ......
                            val intent_cutomer_googlemap =
                                Intent(this, customermap::class.java)
                            startActivity(intent_cutomer_googlemap)
                            Toast.makeText(this, "Customer log in successfully ", Toast.LENGTH_LONG)
                                .show()
                            ProgressDilog.dismiss()


                        } else {
                            Toast.makeText(this, "log in  is not successfully ", Toast.LENGTH_LONG)
                                .show()
                            ProgressDilog.dismiss()
                        }
                    }
            }
        }







}




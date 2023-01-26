package com.example.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.login)
        btnSignUp = findViewById(R.id.signup)
        if (mAuth.currentUser!= null) {
            intent = Intent(this@Login,MainActivity::class.java)
            startActivity( intent)
            finish()
        }
        btnSignUp.setOnClickListener{
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().replace("\\s".toRegex(), "")
            val password = edtPassword.text.toString().replace(" ", "")
            if(email=="") {
                Toast.makeText(this@Login, "Email is empty", Toast.LENGTH_SHORT).show()
            } else if (password==""){
                Toast.makeText(this@Login,"password is empty",Toast.LENGTH_SHORT).show()
            } else if (isValidEmail(email)) {
                login(email,password);
            } else {
                Toast.makeText(this@Login,"Email not valid",Toast.LENGTH_SHORT).show()
            }
//            if(isValidEmail(email)){
//                login(email,password);
//            } else {
//                Toast.makeText(this@Login,"Email not valid",Toast.LENGTH_SHORT).show()
//            }

        }
    }
    fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
    private fun login(email:String, password:String) {
        // logic logging user
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, jumping to home
                    val intent = Intent(this@Login,MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@Login, "Some error occured ", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
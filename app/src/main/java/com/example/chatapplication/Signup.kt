package com.example.chatapplication

import android.content.Intent
import java.text.Normalizer
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class Signup : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignUp: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    public val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mAuth = FirebaseAuth.getInstance()

        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignUp = findViewById(R.id.signup)

        btnSignUp.setOnClickListener {
            var name = edtName.text.toString()
            val email = edtEmail.text.toString().replace("\\s".toRegex(), "")
            val password = edtPassword.text.toString().replace(" ", "")
            val fi = name.unaccent()
            if (name==""){
                Toast.makeText(this@Signup,"Name is empty",Toast.LENGTH_SHORT).show()
            } else if (name!=fi){
                Toast.makeText(this@Signup,"Name cannot be accented with",Toast.LENGTH_SHORT).show()
            } else if (email==""){
                Toast.makeText(this@Signup,"Email is empty",Toast.LENGTH_SHORT).show()
            } else if (password==""||password.length<6){
                Toast.makeText(this@Signup,"password is not valid",Toast.LENGTH_SHORT).show()
            }else if(isValidEmail(email)){
                val re = Regex("[^A-Za-z0-9 ]")
                name = re.replace(name, "")
                signUp(name,email,password);
            } else {
                Toast.makeText(this@Signup,"Email not valid",Toast.LENGTH_SHORT).show()
            }

        }
    }
    fun CharSequence.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }
    fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
    private fun signUp (name:String,email:String, password:String) {
        // logic for create a user
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name,email,mAuth.currentUser?.uid!!)
                    // code for jumping to home
                    val intent = Intent(this@Signup,MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@Signup, "Some error occured ",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(name: String,email: String,uid:String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()

        mDbRef.child("user").child(uid).setValue(User(name,email,uid))
    }
}
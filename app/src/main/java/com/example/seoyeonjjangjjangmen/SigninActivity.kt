package com.example.seoyeonjjangjjangmen

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SigninActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var goSignupTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        //회원가입 화면 이동
        goSignupTextView = findViewById<TextView>(R.id.goSignupTextView)
        goSignupTextView.setOnClickListener{
            val intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }

        //firebase
        auth = Firebase.auth

        var signinBtn = findViewById<Button>(R.id.signinBtn)
        signinBtn.setOnClickListener{
            val db = Firebase.firestore
            //firebase
            auth = Firebase.auth
            //변수 선언
            var email = findViewById<EditText>(R.id.email).text.toString()
            var pw = findViewById<EditText>(R.id.pw).text.toString()
            var signinBtn = findViewById<Button>(R.id.signinBtn)
            auth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser

                        //id 전달
                        if (user != null) {
                            val intent = Intent(this, PostActivity::class.java)
                            startActivity(intent)
                        } else {
                        }


//                        updateUI(user)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
//                        updateUI(null)
                    }
                }
        }
    }
}
//이서연 짱짱멘
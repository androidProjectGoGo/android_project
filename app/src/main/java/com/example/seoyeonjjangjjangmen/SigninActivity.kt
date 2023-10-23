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
    private lateinit var email : String
    private lateinit var id : String //메인으로 id 건네주기 용
    private lateinit var pw : String
    private lateinit var signinBtn : Button
    private lateinit var goSignupTextView: TextView
    val db = Firebase.firestore
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

        //변수 선언
        email = findViewById<EditText>(R.id.email).toString()
        pw = findViewById<EditText>(R.id.pw).toString()
        signinBtn = findViewById<Button>(R.id.signinBtn)


        signinBtn.setOnClickListener{
            auth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser

                        //id 전달
                        val docRef = db.collection("user").document(email)
                        // 문서의 필드 가져오기
                        docRef.get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.exists()) {
                                    // 문서가 존재하는 경우
                                    val data = documentSnapshot.data
                                    if (data != null) {
                                        // 필드 이름으로부터 필드 값을 가져오기
                                        id = data["userID"].toString()
                                        if (id != null) {
                                            // fieldValue 사용 예시
                                            println("field_name: $id")
                                        }
                                    }
                                } else {
                                    // 문서가 존재하지 않는 경우
                                    println("문서가 존재하지 않습니다.")
                                }
                            }
                            .addOnFailureListener { e ->
                                // 문서를 가져오는 중에 오류 발생
                                println("문서 가져오기 실패: $e")
                            }
//                        updateUI(user)

                        //화면전환
                        val intent = Intent(this, PostActivity::class.java)
                        intent.putExtra("userID",id)
                        if(id !=null) {
                            startActivity(intent)
                        }
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
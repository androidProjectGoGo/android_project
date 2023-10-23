package com.example.seoyeonjjangjjangmen

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class SignupActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var email : String
    private lateinit var id : String
    private lateinit var pw : String
    private lateinit var check_pw : String
    private lateinit var signupBtn : Button
    val db = Firebase.firestore
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 뒤로 가기 버튼 표시 (ActionBar에 표시)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        auth = Firebase.auth

        //변수 선언
        email = findViewById<EditText>(R.id.email).toString()
        id = findViewById<EditText>(R.id.id).toString()
        pw = findViewById<EditText>(R.id.pw).toString()
        check_pw = findViewById<EditText>(R.id.check_pw).toString()
        signupBtn = findViewById<Button>(R.id.signUpBtn)

        //회원가입 조건 변수 선언
        var isEmail :Boolean
        var isHasID :Boolean
        var isCorrectPw :Boolean
        signupBtn.setOnClickListener{
            //email 형식 검사
            val pattern: Pattern = Patterns.EMAIL_ADDRESS
            isEmail = pattern.matcher(email).matches()
            //중복 id 검사
//            val isExists = isDocumentNameExists("user", id)
//            isHasID = isExists
            //pw와 check_pw 같은지 검사
            isCorrectPw = pw==check_pw
            //회원가입 버튼 누른 후 데이터 저장
            if (isEmail==true && isCorrectPw == true){
                auth.createUserWithEmailAndPassword(email, pw)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser
                            val uid = user?.uid
                            val userInfo = hashMapOf(
                                "email" to email,
                                "pw" to pw,
                                "uid" to uid,
                                )
                            db.collection("user").document(id)
                                .set(userInfo)
                                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
//                            updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
//                            updateUI(null)
                        }
                    }
            }
        }
    }
    //중복 id 검사 메소드
    fun isDocumentNameExists(collectionName: String, documentName: String): Boolean {
        // CollectionReference를 참조합니다.
        val collectionReference = db.collection(collectionName)

        // 문서 이름을 가진 문서가 존재하는지 확인하는 쿼리
        val query = collectionReference.whereEqualTo(FieldPath.documentId(), documentName)

        // Query 결과를 가져옵니다.
        val querySnapshotTask: Task<QuerySnapshot> = query.get()

        // Tasks.await()를 사용하여 Query 결과를 동기적으로 얻습니다.
        val querySnapshot = await(querySnapshotTask)

        // 문서가 존재하는지 여부를 판별
        return !querySnapshot.isEmpty
    }
}
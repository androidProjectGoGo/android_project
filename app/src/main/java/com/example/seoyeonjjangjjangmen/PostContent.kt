package com.example.seoyeonjjangjjangmen

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostContent : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var chatStartBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.postcontent)

        val db = Firebase.firestore
        val auth = Firebase.auth

        val titleTv = findViewById<TextView>(R.id.title_tv)
        val contentTv = findViewById<TextView>(R.id.content_tv)
        val priceTv = findViewById<TextView>(R.id.price_tv)
        val isSell = findViewById<TextView>(R.id.isSell_tv)
        var chatStartBtn = findViewById<Button>(R.id.chatStartBtn)
        val useridTv = findViewById<TextView>(R.id.userid_tv)

        //userID 받기
        val userID = intent.getStringExtra("userID")
        val title = intent.getStringExtra("title")
        val isSellValue = intent.getBooleanExtra("isSell", false)
        val price = intent.getLongExtra("price", 0)
        val content = intent.getStringExtra("content")
        Log.d("userID", "$userID")



        if (userID != null) {
            useridTv.text = userID
            titleTv.text = title
            contentTv.text = content
            priceTv.text = price.toString()
            isSell.text = if (isSellValue) "판매중" else "판매완료"
        } else {
            // userID가 null인 경우, 처리할 내용
            Log.e("PostContent", "userID가 null입니다.")
        }
        var email="test"
        val postRef = userID?.let { db.collection("user").document(userID) }
        if (postRef != null) {
            postRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // 문서가 존재하는 경우
                        val data = documentSnapshot.data
                        if (data != null) {
                            email = data["email"] as String
                            Log.d("email",email)
                        }
                    } else {
                        // 문서가 존재하지 않는 경우, 에러 메시지 또는 다른 작업 수행
                        Log.d(ContentValues.TAG, "Document does not exist.")
                    }
                }
                .addOnFailureListener { e ->
                    println("문서 가져오기 실패: $e")
                }
        }

        chatStartBtn.setOnClickListener {

            // ----- 제발 됐으면 좋겠다 람쥐 ----- //
            val intent = Intent(this, ChatRoomActivity::class.java)

            // 여기부터 밑에 코드들 잘 맞추면 될듯???
            intent.putExtra("sellerEmail", email)
            intent.putExtra("currentEmail", auth.currentUser?.email.toString())

            val buyerCollection =
                db.collection("chats").document(auth.currentUser?.email.toString())
                    .collection("buyer")
            val chatData = hashMapOf(
                "email" to userID
            )
            buyerCollection.document(userID.toString()).set(chatData)
                .addOnSuccessListener {
                    startActivity(intent)
                }
        }

    }
}

package com.example.seoyeonjjangjjangmen

import android.content.ContentValues
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostContent : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.postcontent)

        val db = Firebase.firestore
        val auth = Firebase.auth

        val titleTv = findViewById<TextView>(R.id.title_tv)
        val contentTv = findViewById<TextView>(R.id.content_tv)
        val priceTv = findViewById<TextView>(R.id.price_tv)
        val imageView = findViewById<ImageView>(R.id.content_iv)
        val isSell = findViewById<TextView>(R.id.isSell_tv)
        val chatBtn = findViewById<Button>(R.id.postBtn)
        val useridTv = findViewById<TextView>(R.id.userid_tv)


        //글 데이터 불러오기
        val postRef = db.collection("post").document("7c461fb0-f8e2-41ab-b36b-757b86161740")
        postRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // 문서가 존재하는 경우
                    val data = documentSnapshot.data
                    if (data != null) {
                        val getTitle = data["title"] as String
                        val getIsSell = data["isSell"] as Boolean
                        val getPrice = data["price"] as Long
                        val getContent = data["content"] as String
                        val getImageURL = data["imageURL"] as String
                        val getUserId = data["userID"] as String

                        useridTv.setText(getUserId)//판매자 이름
                        titleTv.setText(getTitle)//제목
                        contentTv.setText(getContent)//내용
                        priceTv.setText(getPrice.toString())//가격
                        if (!getIsSell) {//판매여부
                            isSell.text = "판매완료"
                        } else {
                            isSell.text = "판매중"
                        }
                        
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

}
package com.example.seoyeonjjangjjangmen

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class PostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post)

        val postBtn = findViewById<Button>(R.id.postBtn)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 SeekBar를 터치하여 드래그를 시작할 때 호출됩니다.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        var isNew = true
        //새 글 작성
        if (isNew == false) {
            val db = Firebase.firestore
            val auth = Firebase.auth
            val user = auth.currentUser
            val uid = user?.uid
            var userID = "ㅇㅇㅇ"

            // Firestore에서 userID 가져오기
            if (uid != null) {
                val docRef = db.collection("userToken").document(uid)
                docRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val data = documentSnapshot.data
                            if (data != null) {
                                userID = data["userID"] as String
                                println("userID: $userID")
                            }
                        } else {
                            println("문서가 존재하지 않습니다.")
                        }
                    }
                    .addOnFailureListener { e ->
                        println("문서 가져오기 실패: $e")
                    }
                postBtn.setOnClickListener{
                    val title = findViewById<EditText>(R.id.title).text.toString()
                    val content = findViewById<EditText>(R.id.content).text.toString()

                    val price = seekBar.progress// 여기에서 price 값을 설정 (예: seekBar.progress)

                    val postData = hashMapOf(
                        "title" to title,
                        "isSell" to false,
                        "price" to price,
                        "userID" to userID,
                        "content" to content,
                        "imageURL" to "",
                    )
                    val postID = UUID.randomUUID().toString()

                    db.collection("post").document(postID)
                        .set(postData)
                        .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

                    // Firestore에서 현재 배열 가져오기
                    db.collection("userPostList").document(userID)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                val data = documentSnapshot.data
                                if (data != null) {
                                    // 현재 배열 가져오기
                                    val currentPostIDs = data["postIDs"] as? List<String> ?: emptyList()

                                    // 새로운 데이터 추가
                                    val updatedPostIDs = currentPostIDs.toMutableList()
                                    updatedPostIDs.add(postID)

                                    // 업데이트된 배열을 Firestore에 업로드
                                    val userPostListData = hashMapOf(
                                        "postIDs" to updatedPostIDs
                                    )

                                    db.collection("userPostList").document(userID)
                                        .set(userPostListData)
                                        .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                                        .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            println("문서 가져오기 실패: $e")
                        }

                }
            }
        }

        //수정
        else{
            postBtn.setText("수정하기")
            val db = Firebase.firestore
            val auth = Firebase.auth

            //글 데이터 불러오기
            //changePoint!! postID 가져오기
            val postRef = db.collection("post").document("b57317dd-722b-478a-a482-3762da026abe")
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

                            findViewById<EditText>(R.id.title).setText(getTitle)
                            findViewById<EditText>(R.id.content).setText(getContent)

                            seekBar.progress = getPrice.toInt()

                        }
                    } else {
                        // 문서가 존재하지 않는 경우, 에러 메시지 또는 다른 작업 수행
                        Log.d(ContentValues.TAG, "Document does not exist.")
                    }
                }
                .addOnFailureListener { e ->
                    println("문서 가져오기 실패: $e")
                }

            //upddate로 글 수정하기
            postBtn.setOnClickListener{
                val title = findViewById<EditText>(R.id.title).text.toString()
                val content = findViewById<EditText>(R.id.content).text.toString()

                val price = seekBar.progress// 여기에서 price 값을 설정 (예: seekBar.progress)

                val updatedData = hashMapOf(
                    "title" to title,
                    "isSell" to false,
                    "price" to price,
                    "content" to content,
                    "imageURL" to "",
                )

                // Firestore에서 문서 가져오기
                val postRef = db.collection("post").document("b57317dd-722b-478a-a482-3762da026abe")

                postRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            // 문서가 이미 존재하는 경우, 업데이트 데이터를 사용하여 업데이트
                            postRef.update(updatedData as Map<String, Any>)
                                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!") }
                                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
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
    }
}

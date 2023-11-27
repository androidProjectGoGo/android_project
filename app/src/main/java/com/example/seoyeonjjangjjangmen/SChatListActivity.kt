package com.example.seoyeonjjangjjangmen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SChatListActivity : AppCompatActivity() {
    lateinit var adapter: ArrayAdapter<String>
    lateinit var listView: ListView
    var userID ="android"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.s_chat_list)
        listView = findViewById(R.id.listView)

        val db = Firebase.firestore
        val auth = Firebase.auth
        val user = auth.currentUser
        val uid = user?.uid


        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        fetchChatList(userID)
//        // Firestore에서 userID 가져오기
//        if (uid != null) {
//            val docRef = db.collection("userToken").document(uid)
//            docRef.get()
//                .addOnSuccessListener { documentSnapshot ->
//                    if (documentSnapshot.exists()) {
//                        val data = documentSnapshot.data
//                        if (data != null) {
//                            userID = data["userID"] as String
//                            fetchChatList(userID)
//                        }
//                    } else {
//                        println("문서가 존재하지 않습니다.")
//                    }
//                }
//                .addOnFailureListener { e ->
//                    println("문서 가져오기 실패: $e")
//                }
//        }

    }
    private fun fetchChatList(userId: String) {
        val db = Firebase.firestore
        val chatListRef = db.collection("userChatList").document(userId)

        chatListRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val chatRoomIds = documentSnapshot["chatRoomIDs"] as? List<String> ?: emptyList()
                    displayChatList(chatRoomIds)
                } else {
                    Log.d("Firestore", "Chat list document does not exist.")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error fetching chat list", e)
            }
    }

    private fun displayChatList(chatRoomIds: List<String>) {
        val userList = mutableListOf<String>()

        for (chatRoomId in chatRoomIds) {
            val userIds = chatRoomId.split(userID) // 예시로 "androidhansung"일 때
            println("userIds"+userIds)
            val otherUserId = if (userIds[0].isNotEmpty()) userIds[0] else userIds[1]

            println("otherUserId: $otherUserId")

            userList.add(otherUserId)
        }

        updateListView(userList)
    }

    private fun updateListView(userList: List<String>) {
        val listView = findViewById<ListView>(R.id.listView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val clickedUserId = userList[position]
            val intent = Intent(this, SChatRoomActivity::class.java)
            intent.putExtra("userID", clickedUserId)
            println(clickedUserId)
            startActivity(intent)
        }
    }

}

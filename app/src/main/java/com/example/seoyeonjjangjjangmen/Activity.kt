package com.example.seoyeonjjangjjangmen

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Activity : AppCompatActivity() {
    var listView = findViewById<ListView>(R.id.listView)
    lateinit var adapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.s_chat_list)

        val chatUserID = intent.getStringExtra("userID")

        val db = Firebase.firestore
        val auth = Firebase.auth
        val user = auth.currentUser
        val uid = user?.uid
        var userID = "ㅇㅇㅇ"
        var chatRoomID ="x"
        var switchChatRoomID :String
        var chatRoomDoc: DocumentReference
        var switchChatRoomDoc: DocumentReference

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter
    }
}
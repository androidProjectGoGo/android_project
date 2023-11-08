package com.example.seoyeonjjangjjangmen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase // Firebase 클래스들을 import
import com.example.seoyeonjjangjjangmen.databinding.ChatListBinding
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    lateinit var binding : ChatListBinding
    lateinit var firebaseDatabase: DatabaseReference
    lateinit var recycler_chatroom: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeView()
        setupRecycler()
    }

    fun initializeView() { //뷰 초기화
        try {
            firebaseDatabase = FirebaseDatabase.getInstance().getReference("ChatRoom")!!
            recycler_chatroom = binding.recyclerChatrooms
        }catch (e:Exception)
        {
            e.printStackTrace()
            Toast.makeText(this,"화면 초기화 중 오류가 발생하였습니다.",Toast.LENGTH_LONG).show()
        }
    }


    fun setupRecycler() {
        recycler_chatroom.layoutManager = LinearLayoutManager(this)
        //recycler_chatroom.adapter = RecyclerChatRoomsAdapter(this)
    }
}

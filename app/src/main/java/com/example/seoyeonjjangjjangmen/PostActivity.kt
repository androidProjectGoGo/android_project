package com.example.seoyeonjjangjjangmen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PostActivity : AppCompatActivity() {
    private lateinit var userID : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post)

        userID = intent.getStringExtra("userID").toString()
        if(userID !=null){
            println("받은 데이터: $userID")
        }else{
            println("데이터를 수신하지 못했습니다.")
        }

    }
}
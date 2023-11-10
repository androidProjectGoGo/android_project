package com.example.seoyeonjjangjjangmen

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostListActivity : AppCompatActivity() {
    private var adapter : PostlistRVAdapter?= null
    private lateinit var auth: FirebaseAuth
    private val recyclerViewItems by lazy { findViewById<RecyclerView>(R.id.postListView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.postlist)


        auth = Firebase.auth
        val currentUser = auth.currentUser

        if (auth.currentUser == null) {
            Log.e("PostlistActivity", "데이터 가져오기 실패")
        }else {
            val userID = currentUser!!.uid

            recyclerViewItems.layoutManager = LinearLayoutManager(this)
            adapter = PostlistRVAdapter(this, userID)
            recyclerViewItems.adapter = adapter


            adapter?.setOnItemClickListener { postItem ->//PostContent로 전환
                // 클릭된 post의 정보를 PostContent로 전달
                val intent = Intent(this, PostContent::class.java)
                intent.putExtra("title", postItem.title)
                intent.putExtra("isSell", postItem.isSell)
                intent.putExtra("price", postItem.price)
                //intent.putExtra("content", postItem.content)
                //intent.putExtra("imageURL", postItem.imageURL)
                intent.putExtra("userID", postItem.userID) // postId를 전달하거나 다른 필요한 정보 전달
                startActivity(intent)
            }
        }

    }
}
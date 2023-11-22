package com.example.seoyeonjjangjjangmen

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
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
    val db = Firebase.firestore
    private var isMineCheck = true
    private lateinit var uid : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.postlist)

        val recyclerViewItems = findViewById<RecyclerView>(R.id.postListView)
        val isSellCheck = findViewById<Switch>(R.id.isSellCheck)
        val priceBar = findViewById<SeekBar>(R.id.priceBar)
        val priceValueTv = findViewById<TextView>(R.id.price_value_tv)
        val writeBtn = findViewById<Button>(R.id.writebutton)

        writeBtn.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            intent.putExtra("isNew", true)
            Log.d("isNew", isMineCheck.toString())
            startActivity(intent)
        }

        auth = Firebase.auth
        val currentUser = auth.currentUser

        if (auth.currentUser == null) {
            Log.e("PostlistActivity", "데이터 가져오기 실패")
        } else {
            val userID = currentUser!!.uid

            val docRef = db.collection("user")//uid 전환
            docRef.whereEqualTo("uid", userID)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        uid = document.id // 문서 이름 uid에 할당.
                        Log.d("document", "uid: $uid")
                        setupRecyclerView(userID)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("document", "문서 조회 실패", exception)
                }
        }

        isSellCheck.setOnCheckedChangeListener { _, isChecked ->
            // isSellCheck 스위치 상태에 따라 판매 여부를 확인하고 리스트를 갱신
            adapter?.filterItems(isChecked, priceBar.progress)
        }

        priceBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                priceValueTv.text = progress.toString()
                adapter?.filterItems(isSellCheck.isChecked, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun setupRecyclerView(userID: String) {//초기 설정
        val recyclerViewItems = findViewById<RecyclerView>(R.id.postListView)
        recyclerViewItems.layoutManager = LinearLayoutManager(this)
        adapter = PostlistRVAdapter(this, userID)
        recyclerViewItems.adapter = adapter

        adapter?.setOnItemClickListener { postItem ->
            Log.d("nameChange", "$userID <=> ${postItem.userId}")
            if (uid == postItem.userId) {
                isMineCheck = true
                val intent = Intent(this, PostActivity::class.java)
                intent.putExtra("isNew", false)
                intent.putExtra("postID", postItem.postID)
                Log.d("postID", postItem.postID)
                startActivity(intent)
            } else {
                isMineCheck = false
                val intent = Intent(this, PostContent::class.java)
                intent.putExtra("title", postItem.title)
                intent.putExtra("isSell", postItem.isSell)
                intent.putExtra("price", postItem.price)
                intent.putExtra("content", postItem.content)
                intent.putExtra("userID", postItem.userId)
                intent.putExtra("postID", postItem.postID)
                startActivity(intent)
            }
        }
    }


    override fun onResume() {//다시 돌아왔을 때
        super.onResume()
        loadDataAndUpdateRecyclerView()

    }

    private fun loadDataAndUpdateRecyclerView() {//recyclerview 데이터 업데이트
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Log.e("PostlistActivity", "데이터 가져오기 실패")
        } else {
            val userID = currentUser.uid

            val docRef = db.collection("user")
            docRef.whereEqualTo("uid", userID)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        uid = document.id // 문서 이름 uid에 할당.
                        Log.d("document", "uid: $uid")
                        setupRecyclerView(userID)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("document", "문서 조회 실패", exception)
                }
        }
    }

}

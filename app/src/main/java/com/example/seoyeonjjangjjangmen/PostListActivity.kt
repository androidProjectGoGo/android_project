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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.postlist)

        val recyclerViewItems = findViewById<RecyclerView>(R.id.postListView)
        val isSellCheck = findViewById<Switch>(R.id.isSellCheck)
        val priceBar = findViewById<SeekBar>(R.id.priceBar)
        val priceValueTv = findViewById<TextView>(R.id.price_value_tv)

        val writeBtn = findViewById<Button>(R.id.writebutton)
        val isMineCheck = true


        writeBtn.setOnClickListener{

            val intent = Intent(this, PostActivity::class.java)
            intent.putExtra("isNew", isMineCheck)
            startActivity(intent)
        }


        auth = Firebase.auth
        val currentUser = auth.currentUser

        if (auth.currentUser == null) {
            Log.e("PostlistActivity", "데이터 가져오기 실패")
        }else {
            val userID = currentUser!!.uid

            recyclerViewItems.layoutManager = LinearLayoutManager(this)
            adapter = PostlistRVAdapter(this, userID)
            recyclerViewItems.adapter = adapter


            if(isMineCheck==true){
                adapter?.setOnItemClickListener { postItem -> //수정하기로 전환
                    val intent = Intent(this, PostActivity::class.java)
                    intent.putExtra("isNew", false)
                    intent.putExtra("postID", postItem.userId)
                    startActivity(intent)
                }
            }

            else{
                adapter?.setOnItemClickListener { postItem ->//PostContent로 전환
                    // 클릭된 post의 정보를 PostContent로 전달
                    val intent = Intent(this, PostContent::class.java)
                    intent.putExtra("title", postItem.title)
                    intent.putExtra("isSell", postItem.isSell)
                    intent.putExtra("price", postItem.price)
                    intent.putExtra("content", postItem.content)
                    intent.putExtra("userID", postItem.userId) // postId를 전달하거나 다른 필요한 정보 전달
                    startActivity(intent)
                }
            }


            isSellCheck.setOnCheckedChangeListener { _, isChecked ->
                // isSellCheck 스위치 상태에 따라 판매 여부를 확인하고 리스트를 갱신
                adapter?.filterItems(isChecked, priceBar.progress)
            }

            priceBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    priceValueTv.text = progress.toString()
                    adapter?.filterItems(isSellCheck.isChecked, progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })

        }
    }
}
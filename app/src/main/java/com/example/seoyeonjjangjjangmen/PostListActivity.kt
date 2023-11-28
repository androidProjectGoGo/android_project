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
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
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
        val tabLayout = findViewById<TabLayout>(R.id.tab)
        val chatListTab = findViewById<TabItem>(R.id.chatListTab)


        writeBtn.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            intent.putExtra("isNew", true)
            Log.d("isNew", isMineCheck.toString())
            startActivity(intent)
        }

        // TabLayout에 탭 선택 리스너 설정
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {// chatListTab이 선택되었을 때 ChatRoomActivity로 전환
                if (tab?.position==1) {
                    val intent = Intent(this@PostListActivity, PostContent::class.java)//chatlist로 바꿔야함
                    Log.d("gogoChat", "gogoring")
                    startActivity(intent)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 탭이 선택 해제될 때 처리
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

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

    private var listenerRegistration: ListenerRegistration? = null
    override fun onResume() {//다시 돌아왔을 때
        super.onResume()
        loadDataAndUpdateRecyclerView()

    }
    override fun onPause() {
        super.onPause()
        stopListeningForUpdates()
    }
    private fun stopListeningForUpdates() {
        listenerRegistration?.remove()
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

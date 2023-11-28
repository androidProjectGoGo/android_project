//package com.example.seoyeonjjangjjangmen
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.SeekBar
//import android.widget.Switch
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.tabs.TabLayout
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.ktx.Firebase
//
//class PostListActivity : AppCompatActivity() {
//    private var adapter : PostlistRVAdapter?= null
//    private lateinit var auth: FirebaseAuth
//    val db = Firebase.firestore
//    private var isMineCheck = true
//    private lateinit var uid : String
//    private lateinit var userID : String
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.postlist)
//
//        val recyclerViewItems = findViewById<RecyclerView>(R.id.postListView)
//        val isSellCheck = findViewById<Switch>(R.id.isSellCheck)
//        val priceBar = findViewById<SeekBar>(R.id.priceBar)
//        val priceValueTv = findViewById<TextView>(R.id.price_value_tv)
//        val writeBtn = findViewById<Button>(R.id.writebutton)
//        val tabLayout = findViewById<TabLayout>(R.id.tab)
//
//
//
//        writeBtn.setOnClickListener {
//            val intent = Intent(this, PostActivity::class.java)
//            intent.putExtra("isNew", true)
//            Log.d("isNew", isMineCheck.toString())
//            startActivity(intent)
//        }
//
//        // TabLayout에 탭 선택 리스너 설정
//        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {// chatListTab이 선택되었을 때 ChatRoomActivity로 전환
//                if (tab?.position==1) {
//                    val intent = Intent(this@PostListActivity, FragmentChatList::class.java)//chatlist로 바꿔야함
//                    Log.d("gogoChat", "gogoring")
//                    startActivity(intent)
//                }
//            }
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                // 탭이 선택 해제될 때 처리
//            }
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//            }
//        })
//
//        auth = Firebase.auth
//        val currentUser = auth.currentUser
//
//        if (auth.currentUser == null) {
//            Log.e("PostlistActivity", "데이터 가져오기 실패")
//        } else {
//            userID = currentUser!!.uid
//            val docRef = db.collection("user")//uid 전환
//            docRef.whereEqualTo("uid", userID)
//                .get()
//                .addOnSuccessListener { documents ->
//                    for (document in documents) {
//                        uid = document.id // 문서 이름 uid에 할당.
//                        Log.d("document", "uid: $uid")
//                        setupRecyclerView(userID)
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Log.w("document", "문서 조회 실패", exception)
//                }
//        }
//
//        isSellCheck.setOnCheckedChangeListener { _, isChecked ->
//            // isSellCheck 스위치 상태에 따라 판매 여부를 확인하고 리스트를 갱신
//            adapter?.filterItems(isChecked, priceBar.progress)
//        }
//
//        priceBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(
//                seekBar: SeekBar,
//                progress: Int,
//                fromUser: Boolean
//            ) {
//                priceValueTv.text = progress.toString()
//                adapter?.filterItems(isSellCheck.isChecked, progress)
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar) {}
//
//            override fun onStopTrackingTouch(seekBar: SeekBar) {}
//        })
//
//    }
//
//    private fun setupRecyclerView(userID: String) {//초기 설정
//        val recyclerViewItems = findViewById<RecyclerView>(R.id.postListView)
//        recyclerViewItems.layoutManager = LinearLayoutManager(this)
//        adapter = PostlistRVAdapter(this, userID)
//        recyclerViewItems.adapter = adapter
//
//        adapter?.setOnItemClickListener { postItem ->
//            Log.d("nameChange", "$userID <=> ${postItem.userId}")
//            if (uid == postItem.userId) {
//                isMineCheck = true
//                val intent = Intent(this, PostActivity::class.java)
//                intent.putExtra("isNew", false)
//                intent.putExtra("postID", postItem.postID)
//                Log.d("postID", postItem.postID)
//                startActivity(intent)
//            } else {
//                isMineCheck = false
//                val intent = Intent(this, PostContent::class.java)
//                intent.putExtra("title", postItem.title)
//                intent.putExtra("isSell", postItem.isSell)
//                intent.putExtra("price", postItem.price)
//                intent.putExtra("content", postItem.content)
//                intent.putExtra("userID", postItem.userId)
//                intent.putExtra("postID", postItem.postID)
//                startActivity(intent)
//            }
//        }
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        setupRecyclerView(userID)
//        Log.d("onBack_update", "update")
//    }
//    override fun onResume() {//다시 돌아왔을 때
//        super.onResume()
//        setupRecyclerView(userID)
//        Log.d("resume_update", "update")
//    }
//    override fun onPause() {
//        super.onPause()
//        stopListeningForUpdates()
//    }
//    private fun stopListeningForUpdates() {
//
//    }
//
//}

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seoyeonjjangjjangmen.FragmentChatList
import com.example.seoyeonjjangjjangmen.PostActivity
import com.example.seoyeonjjangjjangmen.PostContent
import com.example.seoyeonjjangjjangmen.PostlistRVAdapter
import com.example.seoyeonjjangjjangmen.R
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PostListActivity : Fragment() {
    private var adapter: PostlistRVAdapter? = null
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    private var isMineCheck = true
    private lateinit var uid: String
    private lateinit var userID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.postlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerViewItems = view.findViewById<RecyclerView>(R.id.postListView)
        val isSellCheck = view.findViewById<Switch>(R.id.isSellCheck)
        val priceBar = view.findViewById<SeekBar>(R.id.priceBar)
        val priceValueTv = view.findViewById<TextView>(R.id.price_value_tv)
        val writeBtn = view.findViewById<Button>(R.id.writebutton)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab)

        writeBtn.setOnClickListener {
            val intent = Intent(requireContext(), PostActivity::class.java)
            intent.putExtra("isNew", true)
            Log.d("isNew", isMineCheck.toString())
            startActivity(intent)
        }

        // bottomnavigaion 사용으로 인해 TabLayout을 사용하지 않으므로 주석 처리함
        // TabLayout에 탭 선택 리스너 설정
//        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                if (tab?.position == 1) {
//                    val intent = Intent(requireContext(), FragmentChatList::class.java)
//                    Log.d("gogoChat", "gogoring")
//                    startActivity(intent)
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                // 탭이 선택 해제될 때 처리
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {}
//        })

        auth = Firebase.auth
        val currentUser = auth.currentUser

        if (auth.currentUser == null) {
            Log.e("PostlistFragment", "데이터 가져오기 실패")
        } else {
            userID = currentUser!!.uid
            val docRef = db.collection("user")
            docRef.whereEqualTo("uid", userID)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        uid = document.id
                        Log.d("document", "uid: $uid")
                        setupRecyclerView(userID)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("document", "문서 조회 실패", exception)
                }
        }

        isSellCheck.setOnCheckedChangeListener { _, isChecked ->
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

    private fun setupRecyclerView(userID: String) {
        val recyclerViewItems = view?.findViewById<RecyclerView>(R.id.postListView)
        recyclerViewItems?.layoutManager = LinearLayoutManager(requireContext())
        adapter = PostlistRVAdapter(requireContext(), userID)
        recyclerViewItems?.adapter = adapter

        adapter?.setOnItemClickListener { postItem ->
            Log.d("nameChange", "$userID <=> ${postItem.userId}")
            if (uid == postItem.userId) {
                isMineCheck = true
                val intent = Intent(requireContext(), PostActivity::class.java)
                intent.putExtra("isNew", false)
                intent.putExtra("postID", postItem.postID)
                Log.d("postID", postItem.postID)
                startActivity(intent)
            } else {
                isMineCheck = false
                val intent = Intent(requireContext(), PostContent::class.java)
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

    override fun onResume() {
        super.onResume()
        setupRecyclerView(userID)
        Log.d("resume_update", "update")
    }

    override fun onPause() {
        super.onPause()
        stopListeningForUpdates()
    }

    private fun stopListeningForUpdates() {
        // Add any necessary cleanup code here
    }
}

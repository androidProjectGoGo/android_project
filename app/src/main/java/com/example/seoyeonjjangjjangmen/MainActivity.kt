package com.example.seoyeonjjangjjangmen

import PostListActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    val fragmentManager: FragmentManager = supportFragmentManager
    private var isLoggedIn: Boolean = false

    val fragementChatList : FragmentChatList = FragmentChatList()
    val fragmentPostList : PostListActivity = PostListActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setLoggedInStatus(true)

        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        val fragmentToLoad = intent.getStringExtra("fragmentToLoad")
        /*if (fragmentToLoad == "homeFragment") {
            setLoggedInStatus(true) // 글수정/삭제 후 로그인 안정성 유지 위해 추가
            transaction.replace(R.id.frameLayout, fragmentHome).commit()
        }*/
        //transaction.replace(R.id.frameLayout, fragmentPostList).commit()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // setOnNavigationItemSelectedListener 대신 setOnItemSelectedListener 사용
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()

            when (menuItem.itemId) {
                R.id.homeItem -> {
                    if(isLoggedIn) { // 로그인후에만 볼 수 있도록
                        // 홈 아이템 클릭 시 다른 Fragment로 교체하도록 처리
                        transaction.replace(R.id.frameLayout, fragmentPostList)
                            .commitAllowingStateLoss()
                    }
                }
                R.id.chatItem -> {
                    if(isLoggedIn) {
                        // 대화하기 아이템 클릭 시 다른 Fragment로 교체하도록 처리
                        // transaction.replace(R.id.frameLayout, 다른 Fragment).commitAllowingStateLoss()
                        transaction.replace(R.id.frameLayout, fragementChatList).commitAllowingStateLoss()
                    }
                }

            }

            true
        }

        bottomNavigationView.selectedItemId = R.id.homeItem // 바텀 내비게이션 생성 시 기본 값 설정. 이 위치에 작성해야 함.
    }

    fun setLoggedInStatus(isLoggedIn: Boolean) {
        this.isLoggedIn = isLoggedIn
    }
}
package com.example.seoyeonjjangjjangmen

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class FragmentChatList : Fragment(R.layout.fragment_chat_list) {

    private lateinit var chatListAdapter: ChatListAdapter
    private lateinit var recyclerView: RecyclerView
    private val chatlist = ArrayList<ChatListItem>()



    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private fun getChatList(callback: (List<ChatListItem>) -> Unit) {
        val chatList = mutableListOf<ChatListItem>()
        val email = auth.currentUser?.email.toString()

        db.collection("chats")
            .document(email)
            .collection("buyer")
            .get().addOnSuccessListener { buyerSnapshots ->
                for (buyerDocument in buyerSnapshots.documents) {
                    val sellerEmail = buyerDocument.id
                    val chat = ChatListItem(sellerEmail)
                    chatList.add(chat)
                }
                callback(chatList)
                chatListAdapter.notifyDataSetChanged()
            }

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)


        recyclerView = view.findViewById(R.id.chatlist_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        chatListAdapter = ChatListAdapter(chatlist) {
            // 채팅방 클릭 이벤트 처리
            val intent = Intent(activity, ChatRoomActivity::class.java)
            intent.putExtra("sellerEmail", it.buyer)
            intent.putExtra("currentEmail", auth.currentUser?.email.toString())
            startActivity(intent)
        }

        recyclerView.adapter = chatListAdapter

        getChatList { chat ->
            // ArticleAdapter에 데이터 추가
            chatListAdapter.setChatList(chat)
        }

        return view
    }




    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        chatListAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
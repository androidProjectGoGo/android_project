package com.example.seoyeonjjangjjangmen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var chatItemAdapter: ChatItemAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        // ----- 여기 변수 두개도 바꿔서 맞춰야 할 듯 ----- //
        val currentEmail = intent.getStringExtra("currentEmail").toString()
        val sellerEmail = intent.getStringExtra("sellerEmail").toString()

        val buyer = findViewById<TextView>(R.id.txt_Title)
        buyer.text = sellerEmail

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        
        //setRecyclerView 호출하는 부분
        val recyclerView = findViewById<RecyclerView>(R.id.chatting_recyclerView)
        chatItemAdapter = ChatItemAdapter(emptyList(), currentEmail)
        chatItemAdapter.setRecyclerView(recyclerView)
        recyclerView.adapter = chatItemAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        getChatMessages(currentEmail, sellerEmail) { chatItems ->
            chatItemAdapter.setChatItem(chatItems)
            recyclerView.scrollToPosition(chatItems.size - 1)
        }

        val sendButton = findViewById<Button>(R.id.sendButton)
        val messageInput = findViewById<EditText>(R.id.messageInput)

        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                sendChatMessage(currentEmail, message, sellerEmail)
                messageInput.text.clear()

                // 메시지 전송 후 RecyclerView를 맨 아래로 스크롤
                recyclerView.scrollToPosition(chatItemAdapter.itemCount - 1)
            }
        }
    }

    private fun getChatMessages(
        currentEmail: String,
        sellerEmail: String,
        callback: (List<ChatItem>) -> Unit
    ) {
        val chatItems = mutableListOf<ChatItem>()

        db.collection("chats")
            .document(currentEmail)
            .collection("buyer")
            .document(sellerEmail)
            .collection("messages")
            .orderBy("time")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("ChatRoomActivity", "Listen failed.", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    chatItems.clear()
                    for (document in snapshot.documents) {
                        val sender = document.getString("sender")
                        val content = document.getString("content")
                        val timestamp = document.getTimestamp("time")
                        val chatting = ChatItem(sender, content, timestamp)
                        chatItems.add(chatting)
                    }
                    callback(chatItems)
                }
            }
    }

    private fun sendChatMessage(currentEmail: String, message: String, sellerEmail: String) {
        val sender = auth.currentUser?.email.toString()
        val timestamp = Timestamp.now()
        val chatMessage = ChatItem(sender, message, timestamp)

        val chatsCollection = db.collection("chats")
        val senderCollection = chatsCollection.document(currentEmail).collection("buyer")
        val messagesCollection = senderCollection.document(sellerEmail).collection("messages")

        val receiverCollection = chatsCollection.document(sellerEmail).collection("buyer")
        val messagesCollection2 = receiverCollection.document(currentEmail).collection("messages")

        messagesCollection.add(chatMessage)
        messagesCollection2.add(chatMessage)

        val sellerCollection = db.collection("chats").document(sellerEmail).collection("buyer")
        val chatData = hashMapOf(
            "email" to currentEmail
        )
        sellerCollection.document(currentEmail).set(chatData)
    }
}

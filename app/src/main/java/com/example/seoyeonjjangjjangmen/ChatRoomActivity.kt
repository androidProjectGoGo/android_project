package com.example.seoyeonjjangjjangmen

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

        val currentEmail = intent.getStringExtra("currentEmail").toString()
        val sellerEmail = intent.getStringExtra("sellerEmail").toString()


        val buyer = findViewById<TextView>(R.id.txt_Title)
        buyer.text = sellerEmail

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance() // 초기화


        val recyclerView = findViewById<RecyclerView>(R.id.chatting_recyclerView)
        chatItemAdapter = ChatItemAdapter(emptyList(), currentEmail) // Initially, an empty list
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
                sendChatMessage(sellerEmail, message, sellerEmail)
                messageInput.text.clear()
            }
        }
    }




    private fun getChatMessages(currentEmail: String, sellerEmail: String, callback: (List<ChatItem>) -> Unit) {
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
                    for (document in snapshot) {
                        val sender = document.getString("sender")
                        val content = document.getString("content")
                        val timestamp = document.getString("time")
                        val chatting = ChatItem(sender, content, timestamp)
                        chatItems.add(chatting)
                    }
                    callback(chatItems)
                }
            }
    }


    private fun sendChatMessage(email: String, message: String, sellerEmail: String) {
        val sender = auth.currentUser?.email.toString()
        val timestamp = Timestamp.now().toDate().toString()
        val chatMessage = ChatItem(sender, message, timestamp)

        val chatsCollection = db.collection("chats")
        val senderCollection = chatsCollection.document(sender).collection("buyer")
        val messagesCollection = senderCollection.document(sellerEmail).collection("messages")

        val receiverCollection = chatsCollection.document(sellerEmail).collection("buyer")
        val messagesCollection2 = receiverCollection.document(sender).collection("messages")

        messagesCollection.add(chatMessage)
        messagesCollection2.add(chatMessage)

        val sellerCollection = db.collection("chats").document(sellerEmail).collection("buyer")
        val chatData = hashMapOf(
            "email" to sender
        )
        sellerCollection.document(sender).set(chatData)
    }
}
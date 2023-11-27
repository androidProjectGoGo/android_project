package com.example.seoyeonjjangjjangmen

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SChatRoomActivity  : AppCompatActivity() {
    var listView = findViewById<ListView>(R.id.listView)
    lateinit var adapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.s_chat_list)
        val chatUserID = intent.getStringExtra("userID")

        val db = Firebase.firestore
        val auth = Firebase.auth
        val user = auth.currentUser
        val uid = user?.uid
        var userID = "ㅇㅇㅇ"
        var chatRoomID ="x"
        var switchChatRoomID :String
        var chatRoomDoc: DocumentReference
        var switchChatRoomDoc: DocumentReference

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        // Firestore에서 userID 가져오기
        if (uid != null) {
            val docRef = db.collection("userToken").document(uid)
            docRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val data = documentSnapshot.data
                        if (data != null) {
                            userID = data["userID"] as String
                            chatRoomID = userID+chatUserID
                            switchChatRoomID = chatUserID+userID
                            if(!chatRoomID.equals("x")){
                                chatRoomDoc = db.collection("chatRoomMessages").document(chatRoomID)
                                switchChatRoomDoc = db.collection("chatRoomMessages").document(switchChatRoomID)
                                //chatRoomID가 이미 있을 때
                                if(chatRoomDoc!=null || switchChatRoomDoc!=null){
                                    if(chatRoomDoc == null){
                                        chatRoomID=switchChatRoomID
                                    }
                                    isChatRoomID(chatRoomID)
                                }
                                //chatRoomID가 없을때
                                else{
                                    if (chatUserID != null) {
                                        isNotChatRoomID(chatRoomID,userID,chatUserID)
                                    }
                                }
                            }
                            println("userID: $userID")
                        }
                    } else {
                        println("문서가 존재하지 않습니다.")
                    }
                }
                .addOnFailureListener { e ->
                    println("문서 가져오기 실패: $e")
                }
        }

        var inputMessageEditText = findViewById<EditText>(R.id.inputMessageEditText)
        var sendMessageBtn = findViewById<Button>(R.id.sendMessageBtn)
        sendMessageBtn.setOnClickListener {
            if (inputMessageEditText.text != null) {
                var message = inputMessageEditText.text
                if (!chatRoomID.equals("x")) {
                    //메세지 데이터에 저장
                    sendMessage(chatRoomID, message, userID)
                    //메세지 ui 보여주기

                }
            }
        }

    }
    //chatRoomID가 있을 때
    fun isChatRoomID(chatRoomID : String){
        Firebase.firestore.collection("chatRoomMessages")
            .document(chatRoomID)
            .collection("m")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // 에러 처리
                    return@addSnapshotListener
                }

                val messages = mutableListOf<String>()
                snapshot?.documents?.forEach { document ->
                    val chatMessage = document.toObject(ChatMessage::class.java)
                    if (chatMessage != null) {
                        val messageText = "${chatMessage.sender}: ${chatMessage.message}"
                        messages.add(messageText)
                    }
                }

                adapter.clear()
                adapter.addAll(messages)
                adapter.notifyDataSetChanged()

                // 스크롤을 가장 아래로 이동 (마지막 메시지가 보이도록 함)
                listView.post {
                    listView.setSelection(adapter.count - 1)
                }
            }
    }
    //chatRoomID가 없을 때
    fun isNotChatRoomID(chatRoomID : String,userID:String,chatUserID:String){
        addChatRoomIDs(chatRoomID,userID)
        addChatRoomIDs(chatRoomID,chatUserID)
    }
    //chatRoomIDs 추가
    fun addChatRoomIDs(chatRoomID : String,target:String){
        val db = Firebase.firestore
        // 문서 가져오기
        db.collection("userChatList").document(target)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // 문서가 이미 존재하는 경우
                    val data = documentSnapshot.data
                    if (data != null) {
                        // 현재 배열 가져오기
                        val currentPostIDs = data["chatRoomIDs"] as? List<String> ?: emptyList()

                        // 새로운 데이터 추가
                        val updatedChatIDs = currentPostIDs.toMutableList()
                        updatedChatIDs.add(chatRoomID)
                        // 업데이트된 배열을 Firestore에 업로드
                        val userPostListData = hashMapOf(
                            "chatRoomIDs" to chatRoomID
                        )

                        db.collection("userChatList").document(target)
                            .set(userPostListData)
                            .addOnSuccessListener { Log.d(ContentValues.TAG, "Document successfully written!") }
                            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
                        finish()
                    }
                } else {
                    // 문서가 존재하지 않는 경우, 문서를 생성하고 새로운 데이터를 배열로 설정
                    val userPostListData = hashMapOf(
                        "chatRoomIDs" to listOf(chatRoomID)
                    )

                    db.collection("userChatList").document(target)
                        .set(userPostListData)
                        .addOnSuccessListener { Log.d(ContentValues.TAG, "Document successfully written!") }
                        .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
                }
            }
            .addOnFailureListener { e ->
                println("문서 가져오기 실패: $e")
            }
    }
    //메세지 보냈을 때 firebase 함수
    fun sendMessage(chatRoomID: String, message: Editable,userID: String){
        val timestamp = System.currentTimeMillis()
        val documentId = "m$timestamp"
        Firebase.firestore.collection("chatRoomMessages").document(chatRoomID).collection("m").document(documentId)
            .set(
                hashMapOf(
                    "sender" to userID,
                    "message" to message
                )
            )
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

    }
}

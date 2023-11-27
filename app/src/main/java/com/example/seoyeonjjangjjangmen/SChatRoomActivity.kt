package com.example.seoyeonjjangjjangmen

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SChatRoomActivity  : AppCompatActivity() {
    lateinit var adapter: ArrayAdapter<String>
    lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.s_chatroom)
        listView = findViewById(R.id.listView)
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
                            Log.d("userID",userID)
                            if (chatUserID != null) {
                                Log.d("receiveID",chatUserID)
                            }
                            if(!chatRoomID.equals("x")){
                                println("채팅룸 찾기")
                                chatRoomDoc = db.collection("chatRoomIDList").document(chatRoomID)
                                switchChatRoomDoc = db.collection("chatRoomIDList").document(switchChatRoomID)
                                Log.d("Debug", "chatRoomID: $chatRoomID, userID: $userID, chatUserID: $chatUserID")
//                                isChatRoomID(chatRoomID)
                                //chatRoomID가 이미 있을 때
                                chatRoomDoc.get().addOnCompleteListener { task ->
                                    Log.d("Debug2", "chatRoomID: $chatRoomID, userID: $userID, chatUserID: $chatUserID")

                                    if (task.isSuccessful) {
                                        if (task.result?.exists() == true) {
                                            // Document exists
                                            isChatRoomID(chatRoomID)
                                        } else {
                                            // Document does not exist
                                            // Switch to the other document
                                            switchChatRoomDoc.get().addOnCompleteListener { switchTask ->
                                                if (switchTask.isSuccessful && switchTask.result?.exists() == true) {

                                                    // Switch to the other document
                                                    chatRoomID = switchChatRoomID
                                                    isChatRoomID(chatRoomID)
                                                } else {
                                                    // Both documents do not exist
                                                    if (chatUserID != null) {
                                                        db.collection("chatRoomIDList")
                                                            .document(chatRoomID)
                                                            .set(mapOf<String, Any>())
                                                            .addOnSuccessListener {
                                                                Log.d("Firestore", "Empty document added successfully.")
                                                            }
                                                            .addOnFailureListener { e ->
                                                                Log.w("Firestore", "Error adding empty document", e)
                                                            }
                                                        isNotChatRoomID(chatRoomID, userID, chatUserID)
                                                        Log.d("채팅방 생성 이름 :", chatRoomID)
                                                    }
                                                }
                                            }
                                        }
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
                var message = inputMessageEditText.text.toString()
                if (!chatRoomID.equals("x")) {
                    //메세지 데이터에 저장
                    sendMessage(chatRoomID, message, userID)
                    //메세지 ui 보여주기

                    //text초기화
                    inputMessageEditText.setText("")
//                    isChatRoomID(chatRoomID)
                    println("잘되는지 확인"+chatRoomID)
                }
            }
        }

    }
    //chatRoomID가 있을 때
    fun isChatRoomID(chatRoomID : String){
        Firebase.firestore.collection("chatRoomMessages")
            .document(chatRoomID)
            .collection("m")
            .orderBy(FieldPath.documentId(), Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Error getting documents: $e")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    println("Number of documents: ${snapshot.documents.size}")
                } else {
                    println("Snapshot is null")
                }
                println("message")

                val messages = mutableListOf<String>()
                snapshot?.documents?.forEach { document ->

                    val sender = document.getString("sender")
                    val message = document.getString("message")
                    println(message)
                    if (sender != null && message != null) {
                        val messageText = "$sender: $message"
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

                        val updatedChatIDs = currentPostIDs.toMutableList()
                        updatedChatIDs.add(chatRoomID)

                        val userPostListData = hashMapOf("chatRoomIDs" to updatedChatIDs)
                        db.collection("userChatList").document(target)
                            .set(userPostListData)
                            .addOnSuccessListener { Log.d(ContentValues.TAG, "Document successfully written!") }
                            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
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
    fun sendMessage(chatRoomID: String, message: String,userID: String){
        val timestamp = System.currentTimeMillis().toString()
        val documentId = "$timestamp"
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

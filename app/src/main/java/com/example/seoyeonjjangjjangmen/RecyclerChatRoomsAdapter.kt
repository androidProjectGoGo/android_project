package com.example.seoyeonjjangjjangmen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Context
import com.google.firebase.database.getValue
import com.google.firebase.firestore.auth.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
class RecyclerChatRoomsAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerChatRoomsAdapter.ViewHolder>() {
    // 채팅방 목록을 저장하는 ArrayList
    var chatRooms: ArrayList<ChatRoom> = arrayListOf()
    // 각 채팅방의 Firebase Realtime Database에서의 고유 키를 저장하는 ArrayList
    var chatRoomKeys: ArrayList<String> = arrayListOf()
    // 현재 사용자의 Firebase UID를 저장하는 문자열
    val myUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    init {
        // 채팅방 목록 초기화 및 업데이트
        setupAllUserList()
    }

    // 전체 채팅방 목록을 초기화하고 업데이트하는 함수
    private fun setupAllUserList() {
        // Firebase Realtime Database에서 사용자가 참여한 채팅방을 조회하고 데이터를 업데이트
        FirebaseDatabase.getInstance().getReference("ChatRoom").child("chatRooms")
            .orderByChild("users/$myUid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatRooms.clear()
                    // 조회한 데이터를 ArrayList에 추가하고 해당 채팅방의 키를 저장
                    for (data in snapshot.children) {
                        chatRooms.add(data.getValue<ChatRoom>()!!)
                        chatRoomKeys.add(data.key!!)
                    }
                    // 데이터 변경을 알림
                    notifyDataSetChanged()
                }
            })
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_list, parent, false)
        return ViewHolder(ListChatroomItemBinding.bind(view))
    }

    // ViewHolder에 데이터를 바인딩하는 함수
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 채팅방에 포함된 사용자 키 목록
        var userIdList = chatRooms[position].users!!.keys
        // 상대방 사용자 키
        var opponent = userIdList.first { !it.equals(myUid) }

        // 상대방 사용자 키를 포함하는 채팅방 불러오기
        FirebaseDatabase.getInstance().getReference("User").child("users").orderByChild("uid")
            .equalTo(opponent)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                @SuppressLint("RestrictedApi")
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        // 채팅방 키 초기화
                        holder.chatRoomKey = data.key.toString()!!
                        // 상대방 정보 초기화
                        holder.opponentUser = data.getValue<User>()!!
                        // 상대방 이름 초기화
                        holder.txt_name = data.getValue<User>()!!.toString()
                    }
                }
            })

        // 채팅방 항목 선택 시 이벤트 처리
        holder.background.setOnClickListener() {
            try {
                val intent = Intent(context, ChatRoomActivity::class.java)
                // 채팅방 정보, 상대방 사용자 정보, 채팅방 키 정보를 인텐트에 추가
                intent.putExtra("ChatRoom", chatRooms.get(position))
                intent.putExtra("Opponent", holder.opponentUser)
                intent.putExtra("ChatRoomKey", chatRoomKeys[position])
                // 해당 채팅방으로 이동
                context.startActivity(intent)
                (context as AppCompatActivity).finish()
            } catch (e: Exception) {
                e.printStackTrace()
                System.out.println("채팅방 이동 중 문제가 발생함. At : RecyclerChatRoomsAdapter.onBindViewHolder")

            }
        }

        // 채팅방 메시지가 존재하는 경우 마지막 메시지 및 시각 초기화 및 표시
        if (chatRooms[position].messages!!.size > 0) {
            setupLastMessageAndDate(holder, position)
            setupMessageCount(holder, position)
        }
    }

    // 마지막 메시지 및 시각 초기화 및 표시하는 함수
    fun setupLastMessageAndDate(holder: ViewHolder, position: Int) {
        try {
            var lastMessage =
                chatRooms[position].messages!!.values.sortedWith(compareBy({ it.sended_date }))
                    .last()
            // 마지막 메시지 표시
            holder.txt_message = lastMessage
            // 마지막으로 전송된 시각 표시
            holder.txt_date = getLastMessageTimeString(lastMessage.sended_date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 확인되지 않은 메시지의 개수를 설정하고 화면에 표시하는 함수
    fun setupMessageCount(holder: ViewHolder, position: Int) {
        try {
            // 확인되지 않은 메시지 중 확인되지 않은 상대방의 메시지 개수 가져오기
            var unconfirmedCount =
                chatRooms[position].messages!!.filter {
                    !it.value.confirmed && !it.value.senderUid.equals(myUid)
                }.size
            // 확인되지 않은 메시지가 있을 경우 개수 표시
            if (unconfirmedCount > 0) {
                holder.txt_chatCount?.visibility = View.VISIBLE
                holder.txt_chatCount?.text = unconfirmedCount.toString()
            } else
                holder.txt_chatCount?.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
            holder.txt_chatCount?.visibility = View.GONE
        }
    }

    // 마지막 메시지가 전송된 시각을 현재 시간과 비교하여 표시 형식으로 반환하는 함수
    fun getLastMessageTimeString(lastTimeString: String): String {
        try {
            // 현재 시각
            var currentTime = LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId())
            var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

            // 마지막 메시지 시각 월,일,시,분
            var messageMonth = lastTimeString.substring(4, 6).toInt()
            var messageDate = lastTimeString.substring(6, 8).toInt()
            var messageHour = lastTimeString.substring(8, 10).toInt()
            var messageMinute = lastTimeString.substring(10, 12).toInt()

            // 현 시각 월,일,시,분
            var formattedCurrentTimeString = currentTime.format(dateTimeFormatter)
            var currentMonth = formattedCurrentTimeString.substring(4, 6).toInt()
            var currentDate = formattedCurrentTimeString.substring(6, 8).toInt()
            var currentHour = formattedCurrentTimeString.substring(8, 10).toInt()
            var currentMinute = formattedCurrentTimeString.substring(10, 12).toInt()

            // 현재 시각과 마지막 메시지 시각과의 차이 계산
            var monthAgo = currentMonth - messageMonth
            var dayAgo = currentDate - messageDate
            var hourAgo = currentHour - messageHour
            var minuteAgo = currentMinute - messageMinute

            // 차이에 따라 표시 형식 결정
            if (monthAgo > 0)
                return "$monthAgo 개월 전"
            else {
                if (dayAgo > 0) {
                    if (dayAgo == 1)
                        return "어제"
                    else
                        return "$dayAgo 일 전"
                } else {
                    if (hourAgo > 0)
                        return "$hourAgo 시간 전"
                    else {
                        if (minuteAgo > 0)
                            return "$minuteAgo 분 전"
                        else
                            return "방금"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    // RecyclerView의 항목 수 반환
    override fun getItemCount(): Int {
        return chatRooms.size
    }

    // ViewHolder 클래스 정의
    inner class ViewHolder(itemView: ListChatroomItemBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        @SuppressLint("RestrictedApi")
        var opponentUser = User("")
        var chatRoomKey = ""
        var background = itemView.background
        var txt_name = itemView.txtName
        var txt_message = itemView.txtMessage
        var txt_date = itemView.txtMessageDate
        var txt_chatCount = itemView.txtChatCount
    }
}


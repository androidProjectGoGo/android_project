package com.example.seoyeonjjangjjangmen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ChatListAdapter(
    private var chatlist: List<ChatListItem>,
    private val onItemClick: (ChatListItem) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ChatlistViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setChatList(chats: List<ChatListItem>) {
        chatlist = chats
        notifyDataSetChanged()
    }


    class ChatlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val interlocutor: TextView = itemView.findViewById(R.id.interlocutor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatlistViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent, false)
        return ChatlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatlistViewHolder, position: Int) {
        val chatRoom = chatlist[position]

        holder.interlocutor.text = chatRoom.buyer

        holder.itemView.setOnClickListener {
            onItemClick(chatRoom)
        }
    }

    override fun getItemCount() = chatlist.size
}
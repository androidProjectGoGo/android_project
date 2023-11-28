package com.example.seoyeonjjangjjangmen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatItemAdapter(private var chatItems: List<ChatItem>, private val currentUserEmail: String) :
    RecyclerView.Adapter<ChatItemAdapter.ChatItemViewHolder>() {

    private lateinit var recyclerView: RecyclerView

    fun setRecyclerView(rv: RecyclerView) {
        recyclerView = rv
    }
    fun setChatItem(chatting: List<ChatItem>) {
        chatItems = chatting
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_ME = 1
        private const val VIEW_TYPE_OTHER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatItems[position].sender == currentUserEmail) {
            VIEW_TYPE_OTHER
        } else {
            VIEW_TYPE_ME
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        return if (viewType == VIEW_TYPE_ME) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_left, parent, false)
            ChatItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_right, parent, false)
            ChatItemViewHolder(view)
        }
    }


    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        holder.bind(chatItems[position])
    }

    override fun getItemCount() = chatItems.size


    inner class ChatItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        //private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)

        fun bind(chatItem: ChatItem) {
            contentTextView.text = chatItem.content
            //timeTextView.text = chatItem.time
        }
    }
}
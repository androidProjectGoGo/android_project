package com.example.seoyeonjjangjjangmen

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.seoyeonjjangjjangmen.databinding.ItemPostlistBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.*

data class PostItem(
    val title: String,
    val price: Long,
    val isSell: Boolean,
    val isMy: Boolean
)


class PostlistRVAdapter(val context: Context) : RecyclerView.Adapter<PostlistRVAdapter.ViewHolder>() {
    private val postItems: MutableList<PostItem> = mutableListOf()
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("your_database_path")

    init {
        // 데이터를 가져오기
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postItems.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val title = postSnapshot.child("title").getValue(String::class.java) ?: ""
                    val price = postSnapshot.child("price").getValue(Long::class.java) ?: 0
                    val isSell = postSnapshot.child("isSell").getValue(Boolean::class.java) ?: false
                    val isMy = postSnapshot.child("isMy").getValue(Boolean::class.java) ?: false
                    val postItem = PostItem(title, price, isSell, isMy)
                    postItems.add(postItem)
                }
                notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {// 데이터 가져오기 실패 시 처리
                println("문서 가져오기 실패: $databaseError")
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postItem = postItems[position]
        holder.bind(postItem)
    }

    override fun getItemCount(): Int {
        return postItems.size
    }

    inner class ViewHolder(private val binding: ItemPostlistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(postItem: PostItem) {
            binding.itemTitle.text = postItem.title
            binding.itemPrice.text = postItem.price.toString()
            binding.itemIsSellTag.text = if (postItem.isSell) "판매중" else "판매완료"
            binding.itemMyPostTag.text = if (postItem.isMy) "내 글" else ""
            // 이미지 로딩 코드를 추가.. imageURL을 이미지뷰에 설정하기
        }
    }
}

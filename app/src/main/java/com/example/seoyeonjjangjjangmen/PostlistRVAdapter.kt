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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class PostItem(
    val title: String,
    val price: Long,
    val isSell: Boolean,
    val userID: String
)


class PostlistRVAdapter(val context: Context, val userID: String) : RecyclerView.Adapter<PostlistRVAdapter.ViewHolder>() {
    private val postItems: MutableList<PostItem> = mutableListOf()
    val db = Firebase.firestore
    private val postsCollection = db.collection("post")

    private var onItemClickListener: ((PostItem)-> Unit)?= null


    init {
        // 데이터를 가져오기
        postsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                postItems.clear()
                for (document in querySnapshot.documents) {
                    // Firestore 문서에서 필요한 데이터 추출
                    val title = document.getString("title") ?: ""
                    val price = document.getLong("price") ?: 0
                    val isSell = document.getBoolean("isSell") ?: false
                    //val postID = postSnapshot.child("postID").getValue(String::class.java) ?: ""

                    // PostItem 객체 생성 및 리스트에 추가
                    val postItem = PostItem(title, price, isSell, userID)
                    postItems.add(postItem)
                }
                notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("PostlistRVAdapter", "데이터 가져오기 실패: $e")
            }
    }

    fun setOnItemClickListener(listener: (PostItem)-> Unit){
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postItem = postItems[position]
        holder.bind(postItem)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(postItem)
        }
    }

    override fun getItemCount(): Int {
        val itemCount = postItems.size
        Log.d("PostlistRVAdapter", "getItemCount: $itemCount")
        return itemCount
    }

    inner class ViewHolder(private val binding: ItemPostlistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(postItem: PostItem) {
            binding.itemTitle.text = postItem.title
            binding.itemPrice.text = postItem.price.toString()
            binding.itemIsSellTag.text = if (postItem.isSell) "판매중" else "판매완료"
            //binding.itemMyPostTag.text = if (postItem.isMine) "내 글" else ""
            // 이미지 로딩 코드를 추가.. imageURL을 이미지뷰에 설정하기
        }
    }
}

package com.example.seoyeonjjangjjangmen


import android.content.Context
import android.util.Log
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.seoyeonjjangjjangmen.databinding.ItemPostlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

data class PostItem(
    val title: String,
    val price: Long,
    val isSell: Boolean,
    val userId: String,
    val content: String,
    val postID : String
)


class PostlistRVAdapter(val context: Context, var uid: String) : RecyclerView.Adapter<PostlistRVAdapter.ViewHolder>() {
    private val postItems: MutableList<PostItem> = mutableListOf()
    private val filteredItems: MutableList<PostItem> = mutableListOf()

    val db = Firebase.firestore
    private val postsCollection = db.collection("post")
    private val usersCollection = db.collection("user")
    private val auth = FirebaseAuth.getInstance()
    private var onItemClickListener: ((PostItem)-> Unit)?= null


    init {
        // 데이터 가져오기
        postsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                postItems.clear()
                for (document in querySnapshot.documents) {
                    // Firestore 문서에서 필요한 데이터 추출
                    val title = document.getString("title") ?: ""
                    val price = document.getLong("price") ?: 0
                    val isSell = document.getBoolean("isSell") ?: false
                    val userId = document.getString("userID")?:""
                    val content = document.getString("content")?:""
                    val postID = document.id

                    // PostItem 객체 생성 및 리스트에 추가
                    val postItem = PostItem(title, price, isSell, userId, content, postID)
                    postItems.add(postItem)
                }
                filteredItems.addAll(postItems)
                notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("PostlistRVAdapter", "데이터 가져오기 실패: $e")
            }


        usersCollection.whereEqualTo("uid", uid)//uid-> userid로 만들기
        .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("Firestore", "${document.id} => ${document.data}")
                    uid = document.id
                    //document.id가 문서 이름. 즉 userid와 같이 됨.
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting documents: ", exception)
            }
    }

    fun filterItems(isSellChecked: Boolean, maxPrice: Int) {
        val filteredList = if (isSellChecked) {
            postItems.filter { it.isSell && it.price <= maxPrice }
        } else {
            postItems.filter { it.price <= maxPrice }
        }

        filteredItems.clear()
        filteredItems.addAll(filteredList)
        notifyDataSetChanged()
    }


    fun setOnItemClickListener(listener: (PostItem)-> Unit){
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < filteredItems.size) {
            val postItem = filteredItems[position]
            holder.bind(postItem)

            holder.itemView.setOnClickListener {
                onItemClickListener?.invoke(postItem)
            }
        }
    }


    override fun getItemCount(): Int {
        val itemCount = postItems.size
        //Log.d("PostlistRVAdapter", "getItemCount: $itemCount")
        return itemCount
    }


    inner class ViewHolder(private val binding: ItemPostlistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(postItem: PostItem) {
            binding.itemTitle.text = postItem.title
            binding.itemPrice.text = postItem.price.toString()
            binding.itemIsSellTag.text = if (postItem.isSell) "판매중" else "판매완료"


            binding.itemMyPostTag.visibility = if (uid == postItem.userId) {
                Log.d("PostlistRVAdapter", "currentUser: $uid, postItem.userId: ${postItem.userId}")
                View.VISIBLE
            } else {
                //Log.d("PostlistRVAdapter", "currentUser: $uid, postItem.userId: ${postItem.userId}")
                View.GONE
            }
        }
    }
}

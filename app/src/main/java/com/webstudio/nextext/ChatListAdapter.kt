package com.webstudio.nextext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class ChatListAdapter(
    private val chatList: List<ChatPreview>,
    private val onChatClick: (ChatPreview) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.profileImage)
        val chatName: TextView = view.findViewById(R.id.chatName)
        val lastMessage: TextView = view.findViewById(R.id.lastMessage)
        val chatTime: TextView = view.findViewById(R.id.chatTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_list_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount() = chatList.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = chatList[position]

        holder.chatName.text = item.userName
        holder.lastMessage.text = item.lastMessage
        holder.chatTime.text = formatTime(item.timestamp)


        if (!item.profileImage.isNullOrEmpty()) {
            Glide.with(holder.profileImage.context)
                .load(item.profileImage)
                .placeholder(R.drawable.default_profile)
                .circleCrop()
                .into(holder.profileImage)
        } else {
            holder.profileImage.setImageResource(R.drawable.default_profile)
        }

        holder.itemView.setOnClickListener {
            onChatClick(item)
        }
    }

    private fun formatTime(date: Date?): String {
        if (date == null) return ""
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(date)
    }
}

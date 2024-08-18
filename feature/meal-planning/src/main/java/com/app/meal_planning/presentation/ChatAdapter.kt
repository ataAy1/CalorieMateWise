package com.app.meal_planning.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.meal_planning.R
import com.app.meal_planning.databinding.ItemChatMessageBinding

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val messages: MutableList<Pair<String, Boolean>> = mutableListOf()

    inner class ChatViewHolder(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Pair<String, Boolean>) {
            val (text, isQuestion) = message
            binding.textViewMessage.text = text

            binding.textViewMessage.setTextColor(
                if (isQuestion) ContextCompat.getColor(binding.root.context, com.app.utils.R.color.alice_blue) else ContextCompat.getColor(binding.root.context, com.app.utils.R.color.dark_goldenrod)
            )

            val drawable = if (isQuestion) R.drawable.chat_bubble_question else R.drawable.chat_bubble_user
            binding.textViewMessage.background = ContextCompat.getDrawable(binding.root.context, drawable)

            val layoutParams = binding.textViewMessage.layoutParams as ConstraintLayout.LayoutParams
            if (isQuestion) {
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            } else {
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
            }
            binding.textViewMessage.layoutParams = layoutParams
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: String, isQuestion: Boolean = false) {
        messages.add(Pair(message, isQuestion))
        notifyItemInserted(messages.size - 1)
    }

    fun promptForInput(question: String) {
        addMessage(question, true)
    }
}


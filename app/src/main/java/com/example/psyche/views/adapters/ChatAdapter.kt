package com.example.psyche.views.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.psyche.data.ChatItem
import com.example.psyche.databinding.ItemChatBinding
import com.example.psyche.views.mentalhealthtest.MentalHealthTestActivity

class ChatAdapter(
    private val chatItems: List<ChatItem>,
    private val selectedAnswers: MutableMap<Int, String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ChatViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ResultViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when {
            chatItems[position].isResultCard -> 2
            chatItems[position].isQuestion -> 0
            else -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            2 -> ResultViewHolder(binding)
            else -> ChatViewHolder(binding)
        }
    }

    private fun getColorForValue(value: String): Int {
        return when (value) {
            "Fair" -> Color.parseColor("#538FDE")
            "Good" -> Color.parseColor("#007361")
            "Bad" -> Color.parseColor("#68380F")
            "Need Attention" -> Color.parseColor("#F92D26")
            else -> Color.parseColor("#000000")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatItem = chatItems[position]
        if (holder is ChatViewHolder) {
            if (chatItem.isQuestion) {
                holder.binding.questionBubble.visibility = View.VISIBLE
                holder.binding.answerBubble.visibility = View.GONE
                holder.binding.chatTextView.text = chatItem.text
            } else {
                holder.binding.questionBubble.visibility = View.GONE
                holder.binding.answerBubble.visibility = View.VISIBLE
                holder.binding.answersRadioGroup.setOnCheckedChangeListener(null)
                holder.binding.answersRadioGroup.clearCheck()

                holder.binding.answersRadioGroup.removeAllViews()
                val answers = listOf("Never", "Always", "Often", "Rarely", "Sometimes", "Not at all")
                for (answer in answers) {
                    val radioButton = RadioButton(holder.binding.root.context)
                    radioButton.text = answer
                    radioButton.isEnabled = !selectedAnswers.containsKey(position)
                    holder.binding.answersRadioGroup.addView(radioButton)
                    if (selectedAnswers[position] == answer) {
                        radioButton.isChecked = true
                    }
                }

                holder.binding.answersRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                    if (checkedId != -1) {
                        val selectedAnswer = group.findViewById<RadioButton>(checkedId).text.toString()
                        selectedAnswers[position] = selectedAnswer
                        (holder.itemView.context as MentalHealthTestActivity).onAnswerSelected(selectedAnswer)
                        for (i in 0 until group.childCount) {
                            group.getChildAt(i).isEnabled = false
                        }
                    }
                }
            }
        } else if (holder is ResultViewHolder) {
            holder.binding.resultCard.visibility = View.VISIBLE
            val resultData = chatItem.text.split("\n")
            holder.binding.tvSleepValue.text = resultData[0].split(": ")[1]
            holder.binding.tvFatigueValue.text = resultData[1].split(": ")[1]
            holder.binding.tvInterestValue.text = resultData[2].split(": ")[1]
            holder.binding.tvConcentrationValue.text = resultData[3].split(": ")[1]
            holder.binding.tvPredictedClassName.text = resultData[4].split(": ")[1]

            holder.binding.tvSleepValue.setTextColor(getColorForValue(resultData[0].split(": ")[1]))
            holder.binding.tvFatigueValue.setTextColor(getColorForValue(resultData[1].split(": ")[1]))
            holder.binding.tvInterestValue.setTextColor(getColorForValue(resultData[2].split(": ")[1]))
            holder.binding.tvConcentrationValue.setTextColor(getColorForValue(resultData[3].split(": ")[1]))
        }
    }

    override fun getItemCount() = chatItems.size
}
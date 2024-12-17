package com.example.psyche.views.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.psyche.databinding.HistoryCardBinding
import com.example.psyche.data.HistoryData
import com.example.psyche.helpers.formatTimestamp

class HistoryAdapter(private val historyList: List<HistoryData>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(private val binding: HistoryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(historyData: HistoryData) {
            binding.tvSleepValue.text = historyData.sleep
            binding.tvFatigueValue.text = historyData.fatigue
            binding.tvInterestValue.text = historyData.interest
            binding.tvConcentrationValue.text = historyData.concentration
            binding.tvPredictionValue.text = historyData.prediction
            binding.tvTimestampValue.text = formatTimestamp(historyData.timestamp)

            binding.tvSleepValue.setTextColor(getColorForValue(historyData.sleep))
            binding.tvFatigueValue.setTextColor(getColorForValue(historyData.fatigue))
            binding.tvInterestValue.setTextColor(getColorForValue(historyData.interest))
            binding.tvConcentrationValue.setTextColor(getColorForValue(historyData.concentration))

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size
}
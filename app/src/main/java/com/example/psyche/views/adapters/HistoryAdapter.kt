package com.example.psyche.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.psyche.databinding.HistoryCardBinding
import com.example.psyche.views.components.HistoryData

class HistoryAdapter(private val historyList: List<HistoryData>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(private val binding: HistoryCardBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(historyData: HistoryData) {
        binding.tvSleep.text = "Sleep:"
        binding.tvSleepValue.text = historyData.sleep
        binding.tvFatigue.text = "Fatigue:"
        binding.tvFatigueValue.text = historyData.fatigue
        binding.tvConcentration.text = "Concentration:"
        binding.tvConcentrationValue.text = historyData.concentration
        binding.tvInterest.text = "Interest:"
        binding.tvInterestValue.text = historyData.interest
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
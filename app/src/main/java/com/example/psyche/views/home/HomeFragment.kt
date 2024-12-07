package com.example.psyche.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.psyche.databinding.FragmentHomeBinding
import com.example.psyche.views.adapters.HistoryAdapter
import com.example.psyche.views.components.HistoryData

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyData = listOf(
            HistoryData("Good", "Low", "High", "Moderate"),
            HistoryData("Need Attention", "Moderate", "Moderate", "High"),
            HistoryData("Sleepy", "High", "Low", "Low")
        )

        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewHistory.adapter = HistoryAdapter(dummyData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.psyche.views.testhistory

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.psyche.databinding.FragmentTestHistoryBinding
import com.example.psyche.views.adapters.HistoryAdapter
import com.example.psyche.data.HistoryData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TestHistoryFragment : Fragment() {
    private lateinit var binding: FragmentTestHistoryBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val historyList = mutableListOf<HistoryData>()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
        }

        val textView = TextView(context).apply {
            text = "Test History"
            textSize = 20f
            setTextColor(resources.getColor(android.R.color.white, null))
        }
        binding.toolbar.addView(textView)
        val layoutParams = textView.layoutParams as androidx.appcompat.widget.Toolbar.LayoutParams
        layoutParams.gravity = android.view.Gravity.CENTER
        textView.layoutParams = layoutParams

        adapter = HistoryAdapter(historyList)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        fetchHistoryData()
    }

    private fun fetchHistoryData() {
        showProgressBar()
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users")
                .document(user.uid)
                .collection("mental_health_results")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val historyData = document.toObject(HistoryData::class.java)
                        historyList.add(historyData)
                    }
                    adapter.notifyDataSetChanged()
                    hideProgressBar()
                    animateViewsSequentially()
                }
                .addOnFailureListener { e ->
                    // Handle the error
                    hideProgressBar()
                }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        binding.contentLayout.visibility = View.VISIBLE
    }

    private fun animateViewsSequentially() {
        val views = listOf(
            binding.recyclerView
        )

        views.forEach { it.visibility = View.GONE }

        val animatorSet = AnimatorSet()
        val animators = views.map { view ->
            ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
                duration = 500
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        view.visibility = View.VISIBLE
                    }
                })
            }
        }

        animatorSet.playSequentially(animators)
        animatorSet.start()
    }
}
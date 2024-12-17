package com.example.psyche.views.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.psyche.R
import com.example.psyche.databinding.FragmentHomeBinding
import com.example.psyche.views.adapters.HistoryAdapter
import com.example.psyche.views.mentalhealthtest.MentalHealthTestActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isFetchingData.observe(viewLifecycleOwner, Observer { isFetching ->
            if (isFetching) {
                showProgressBar()
            } else {
                hideProgressBar()
            }
        })

        viewModel.userName.observe(viewLifecycleOwner, Observer { name ->
            binding.textViewUserName.text = " " + name
        })

        viewModel.latestHistory.observe(viewLifecycleOwner, Observer { history ->
            if (history.isNotEmpty()) {
                binding.recyclerViewHistory.layoutManager = LinearLayoutManager(context)
                binding.recyclerViewHistory.adapter = HistoryAdapter(history)
                setImageBasedOnPrediction(history[0].prediction)
            } else {
                binding.recyclerViewHistory.visibility = View.GONE
                binding.imageViewPrediction.setImageResource(R.drawable.happy)
                binding.textViewNoHistory.visibility = View.VISIBLE
            }
        })

        if (viewModel.userName.value == null) {
            viewModel.fetchUserName()
        }

        viewModel.checkForUpdates()

        binding.buttonStartTest.setOnClickListener {
            val intent = Intent(activity, MentalHealthTestActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setImageBasedOnPrediction(prediction: String) {
        val imageRes = when (prediction) {
            "Happy" -> R.drawable.happy
            "Feeling Blue" -> R.drawable.feelingblue
            "Struggling" -> R.drawable.struggling
            "Overwhelmed" -> R.drawable.overwhelmed
            else -> R.drawable.happy
        }
        binding.imageViewPrediction.setImageResource(imageRes)
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
            binding.textViewUserName,
            binding.recyclerViewHistory,
            binding.imageViewPrediction,
            binding.buttonStartTest
        )

        views.forEach { it.visibility = View.GONE }

        val animatorSet = AnimatorSet()
        val animators = views.map { view ->
            ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
                duration = 800
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
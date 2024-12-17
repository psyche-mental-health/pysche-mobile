package com.example.psyche.views.settings

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.psyche.databinding.FragmentSettingsBinding
import com.example.psyche.views.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
        }

        val textView = TextView(context).apply {
            text = "Settings"
            textSize = 20f
            setTextColor(resources.getColor(android.R.color.white, null))
        }
        binding.toolbar.addView(textView)
        val layoutParams = textView.layoutParams as androidx.appcompat.widget.Toolbar.LayoutParams
        layoutParams.gravity = Gravity.CENTER
        textView.layoutParams = layoutParams

        viewModel.isDarkModeEnabled.observe(viewLifecycleOwner, Observer { isEnabled: Boolean ->
            binding.switchDarkMode.isChecked = isEnabled
        })

        viewModel.selectedLanguage.observe(viewLifecycleOwner, Observer { language: String ->
            binding.tvSelectedLanguage.text = language
        })

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkModeEnabled(isChecked)
        }

        binding.tvSelectedLanguage.paintFlags =
            binding.tvSelectedLanguage.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.tvSelectedLanguage.setOnClickListener {
            showLanguageDialog()
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Indonesia")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Language")
            .setItems(languages) { dialog, which ->
                val selectedLanguage = languages[which]
                viewModel.setSelectedLanguage(selectedLanguage)
                activity?.recreate()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
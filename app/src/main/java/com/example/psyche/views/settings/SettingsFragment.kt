package com.example.psyche.views.settings

import android.content.Context
import android.os.Bundle
import android.graphics.Paint
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.psyche.databinding.FragmentSettingsBinding
import com.example.psyche.helpers.ThemeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale
import android.content.res.Configuration
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

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

        return view
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
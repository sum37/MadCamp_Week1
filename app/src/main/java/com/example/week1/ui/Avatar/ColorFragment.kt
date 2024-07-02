package com.example.week1.ui.Avatar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.week1.R
import com.example.week1.databinding.FragmentColorBinding

class ColorFragment : Fragment() {

    private var _binding: FragmentColorBinding? = null
    private val binding get() = _binding!!
    private lateinit var avatarViewModel: AvatarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentColorBinding.inflate(inflater, container, false)
        avatarViewModel = ViewModelProvider(requireActivity()).get(AvatarViewModel::class.java)

        binding.setColor1.setOnClickListener {
            Log.d("ColorFragment", "setColor1 clicked")
            avatarViewModel.setColor(R.color.color_option1)
        }

        binding.setColor2.setOnClickListener {
            Log.d("ColorFragment", "setColor2 clicked")
            avatarViewModel.setColor(R.color.color_option2)
        }

        binding.setColor3.setOnClickListener {
            Log.d("ColorFragment", "setColor3 clicked")
            avatarViewModel.setColor(R.color.color_option3)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.week1.ui.Avatar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.week1.R
import com.example.week1.databinding.FragmentClothesBinding

class ClothFragment : Fragment() {

    private var _binding: FragmentClothesBinding? = null
    private val binding get() = _binding!!
    private lateinit var avatarViewModel: AvatarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClothesBinding.inflate(inflater, container, false)
        avatarViewModel = ViewModelProvider(requireActivity()).get(AvatarViewModel::class.java)

        binding.setCloth1.setOnClickListener {
            Log.d("tag","setcloth1")
            avatarViewModel.setCloth(R.drawable.cloth1_inrec)
        }

        binding.setCloth2.setOnClickListener {
            Log.d("tag","setcloth2")
            avatarViewModel.setCloth(R.drawable.cloth2_inrec)
        }

        binding.setCloth3.setOnClickListener {
            Log.d("tag","setcloth3")
            avatarViewModel.setCloth(R.drawable.cloth3_inrec)
        }

        binding.setCloth4.setOnClickListener {
            Log.d("tag","setcloth3")
            avatarViewModel.setCloth(R.drawable.cloth4_inrec)
        }

        binding.setCloth5.setOnClickListener {
            Log.d("tag","setcloth3")
            avatarViewModel.setCloth(R.drawable.cloth5_inrec)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

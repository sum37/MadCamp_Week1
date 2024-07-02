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
import com.example.week1.databinding.FragmentHatBinding

class HatFragment : Fragment() {

    private var _binding: FragmentHatBinding? = null
    private val binding get() = _binding!!
    private lateinit var avatarViewModel: AvatarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHatBinding.inflate(inflater, container, false)
        avatarViewModel = ViewModelProvider(requireActivity()).get(AvatarViewModel::class.java)

        binding.setHat1.setOnClickListener {
            Log.d("tag","sethat1")
            avatarViewModel.setHat(R.drawable.hat1_inrec)
        }

        binding.setHat2.setOnClickListener {
            Log.d("tag","sethat2")
            avatarViewModel.setHat(R.drawable.hat2_inrec)
        }

        binding.setHat3.setOnClickListener {
            Log.d("tag","sethat3")
            avatarViewModel.setHat(R.drawable.hat3_inrec)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

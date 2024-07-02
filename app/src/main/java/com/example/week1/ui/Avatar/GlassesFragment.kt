package com.example.week1.ui.Avatar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.week1.R
import com.example.week1.databinding.FragmentGlassesBinding

class GlassesFragment : Fragment() {

    private var _binding: FragmentGlassesBinding? = null
    private val binding get() = _binding!!
    private lateinit var avatarViewModel: AvatarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGlassesBinding.inflate(inflater, container, false)
        avatarViewModel = ViewModelProvider(requireActivity()).get(AvatarViewModel::class.java)

        binding.setGlasses1.setOnClickListener {
            Log.d("tag","setglasses1")
            avatarViewModel.setGlasses(R.drawable.glasses1_inrec)
        }

        binding.setGlasses2.setOnClickListener {
            Log.d("tag","setglasses2")
            avatarViewModel.setGlasses(R.drawable.glasses2_inrec)
        }

        binding.setGlasses3.setOnClickListener {
            Log.d("tag","setglasses3")
            avatarViewModel.setGlasses(R.drawable.glasses3_inrec)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

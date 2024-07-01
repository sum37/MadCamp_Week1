package com.example.week1.ui.Avatar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.week1.databinding.FragmentAvatarBinding
import com.google.android.material.tabs.TabLayoutMediator

class AvatarFragment : Fragment() {

    private var _binding: FragmentAvatarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val avatarViewModel =
            ViewModelProvider(this).get(AvatarViewModel::class.java)

        _binding = FragmentAvatarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        val adapter = AvatarPagerAdapter(requireActivity())
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

        avatarViewModel.color.observe(viewLifecycleOwner) {
            // Update avatar image color
        }

        avatarViewModel.glasses.observe(viewLifecycleOwner) {
            // Update avatar image glasses
        }

        avatarViewModel.hat.observe(viewLifecycleOwner) {
            // Update avatar image hat
        }

        avatarViewModel.cloth.observe(viewLifecycleOwner) {
            // Update avatar image clothing
        }

        avatarViewModel.background.observe(viewLifecycleOwner) {
            // Update avatar image background
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

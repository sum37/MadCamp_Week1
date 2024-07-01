package com.example.week1.ui.Avatar

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.week1.R
import com.example.week1.databinding.FragmentAvatarBinding
import com.google.android.material.tabs.TabLayoutMediator

class AvatarFragment : Fragment() {

    private var _binding: FragmentAvatarBinding? = null
    private val binding get() = _binding!!
    private lateinit var avatarViewModel: AvatarViewModel
    private lateinit var avatarImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("AvatarFragment", "onCreateView called")
        avatarViewModel = ViewModelProvider(requireActivity()).get(AvatarViewModel::class.java)

        _binding = FragmentAvatarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        avatarImageView = binding.avatarImage

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        val adapter = AvatarPagerAdapter(requireActivity())
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

        observeViewModel()

        return root
    }

    private fun changeVectorDrawableColor(drawableId: Int, colorId: Int) {
        Log.d("AvatarFragment", "changeVectorDrawableColor called with colorId: $colorId")
        val drawable: Drawable? = ContextCompat.getDrawable(requireContext(), drawableId)
        if (drawable != null) {
            val wrappedDrawable: Drawable = DrawableCompat.wrap(drawable)
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(requireContext(), colorId))
            avatarImageView.setImageDrawable(wrappedDrawable)
            Log.d("AvatarFragment", "Color changed to: ${ContextCompat.getColor(requireContext(), colorId)}")
        } else {
            Log.d("AvatarFragment", "Drawable not found")
        }
    }

    private fun observeViewModel() {
        Log.d("AvatarFragment", "observeViewModel called")
        avatarViewModel.color.observe(viewLifecycleOwner) { newColor ->
            Log.d("AvatarFragment", "color observed with newColor: $newColor")
            changeVectorDrawableColor(R.drawable.jelly, newColor)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

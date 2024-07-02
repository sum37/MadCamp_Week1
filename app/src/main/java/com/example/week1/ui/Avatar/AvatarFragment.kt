package com.example.week1.ui.Avatar

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
    private lateinit var avatarImageView: FrameLayout
    private lateinit var avatarImageBody: ImageView
    private lateinit var avatarImageCloth1: ImageView
    private lateinit var avatarImageCloth2: ImageView
    private lateinit var avatarImageCloth3: ImageView
    private lateinit var avatarImageCloth4: ImageView
    private lateinit var avatarImageCloth5: ImageView
    private lateinit var avatarImageGlasses1: ImageView
    private lateinit var avatarImageGlasses2: ImageView
    private lateinit var avatarImageGlasses3: ImageView
    private lateinit var avatarImageHat1: ImageView
    private lateinit var avatarImageHat2: ImageView
    private lateinit var avatarImageHat3: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("AvatarFragment", "onCreateView called")
        avatarViewModel = ViewModelProvider(requireActivity()).get(AvatarViewModel::class.java)

        _binding = FragmentAvatarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        avatarImageView = binding.avatarImageView
        avatarImageBody = binding.avatarImageBody
        avatarImageCloth1 = binding.avatarImageCloth1
        avatarImageCloth2 = binding.avatarImageCloth2
        avatarImageCloth3 = binding.avatarImageCloth3
        avatarImageCloth4 = binding.avatarImageCloth4
        avatarImageCloth5 = binding.avatarImageCloth5
        avatarImageGlasses1 = binding.avatarImageGlasses1
        avatarImageGlasses2 = binding.avatarImageGlasses2
        avatarImageGlasses3 = binding.avatarImageGlasses3
        avatarImageHat1 = binding.avatarImageHat1
        avatarImageHat2 = binding.avatarImageHat2
        avatarImageHat3 = binding.avatarImageHat3

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        val adapter = AvatarPagerAdapter(requireActivity())
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

        observeViewModel()

        binding.btnSaveAvatar.setOnClickListener {
            val dialog = AvatarSaveFragment()
            dialog.show(parentFragmentManager, "AvatarSaveDialogFragment")
        }

        return root
    }

    private fun observeViewModel() {
        Log.d("AvatarFragment", "observeViewModel called")
        avatarViewModel.color.observe(viewLifecycleOwner) { newColor -> Log.d("AvatarFragment", "color observed with newColor: $newColor")
            changeVectorDrawableColor(R.drawable.jelly_body, newColor)
        }

        avatarViewModel.cloth.observe(viewLifecycleOwner) { selectedCloth -> Log.d("AvatarFragment", "cloth observed with selectedCloth: $selectedCloth")
            updateClothVisibility(selectedCloth)
        }

        avatarViewModel.glasses.observe(viewLifecycleOwner) { selectedGlasses ->
            updateGlassesVisibility(selectedGlasses)
        }

        avatarViewModel.hat.observe(viewLifecycleOwner) { selectedHat ->
            updateHatVisibility(selectedHat)
        }

    }

    private fun updateClothVisibility(selectedCloth: Int) {
        avatarImageCloth1.visibility = if (selectedCloth == R.drawable.cloth1_inrec) View.VISIBLE else View.INVISIBLE
        avatarImageCloth2.visibility = if (selectedCloth == R.drawable.cloth2_inrec) View.VISIBLE else View.INVISIBLE
        avatarImageCloth3.visibility = if (selectedCloth == R.drawable.cloth3_inrec) View.VISIBLE else View.INVISIBLE
        avatarImageCloth4.visibility = if (selectedCloth == R.drawable.cloth4_inrec) View.VISIBLE else View.INVISIBLE
        avatarImageCloth5.visibility = if (selectedCloth == R.drawable.cloth5_inrec) View.VISIBLE else View.INVISIBLE
    }

    private fun updateGlassesVisibility(selectedGlasses: Int) {
        avatarImageGlasses1.visibility = if (selectedGlasses == R.drawable.glasses1_inrec) View.VISIBLE else View.INVISIBLE
        avatarImageGlasses2.visibility = if (selectedGlasses == R.drawable.glasses2_inrec) View.VISIBLE else View.INVISIBLE
        avatarImageGlasses3.visibility = if (selectedGlasses == R.drawable.glasses3_inrec) View.VISIBLE else View.INVISIBLE
    }

    private fun updateHatVisibility(selectedHat: Int) {
        avatarImageHat1.visibility = if (selectedHat == R.drawable.hat1_inrec) View.VISIBLE else View.INVISIBLE
        avatarImageHat2.visibility = if (selectedHat == R.drawable.hat2_inrec) View.VISIBLE else View.INVISIBLE
        avatarImageHat3.visibility = if (selectedHat == R.drawable.hat3_inrec) View.VISIBLE else View.INVISIBLE
    }

    private fun changeVectorDrawableColor(drawableId: Int, colorId: Int) {
        Log.d("AvatarFragment", "changeVectorDrawableColor called with colorId: $colorId")
        val drawable: Drawable? = ContextCompat.getDrawable(requireContext(), drawableId)
        if (drawable != null) {
            val wrappedDrawable: Drawable = DrawableCompat.wrap(drawable)
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(requireContext(), colorId))
            avatarImageBody.setImageDrawable(wrappedDrawable)
            Log.d("AvatarFragment", "Color changed to: ${ContextCompat.getColor(requireContext(), colorId)}")
        } else {
            Log.d("AvatarFragment", "Drawable not found")
        }
    }

    // AvatarFragment.kt
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.btnSaveAvatar.setOnClickListener {
//            val action = AvatarFragmentDirections.actionAvatarFragmentToAvatarSaveFragment()
//            findNavController().navigate(action)
//        }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

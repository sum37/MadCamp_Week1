package com.example.week1.ui.Avatar

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.week1.R
import com.example.week1.databinding.FragmentAvatarBinding
import com.google.android.material.tabs.TabLayoutMediator

class AvatarFragment : Fragment() {

    private var _binding: FragmentAvatarBinding? = null
    private val binding get() = _binding!!
    private lateinit var avatarViewModel: AvatarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("AvatarFragment", "onCreateView called")
        avatarViewModel = ViewModelProvider(requireActivity()).get(AvatarViewModel::class.java)

        _binding = FragmentAvatarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        val adapter = AvatarPagerAdapter(requireActivity())
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

        observeViewModel()

        binding.btnSaveAvatar.setOnClickListener {
            val capturedBitmap = captureAvatar()
            if (capturedBitmap != null) {
                val dialog = AvatarSaveFragment.newInstance(capturedBitmap)
                dialog.show(parentFragmentManager, "AvatarSaveDialogFragment")
            } else {
                Log.e("AvatarFragment", "Failed to capture avatar bitmap")
            }
        }

        return root
    }

    private fun observeViewModel() {
        Log.d("AvatarFragment", "observeViewModel called")
        avatarViewModel.color.observe(viewLifecycleOwner) { newColor ->
            Log.d("AvatarFragment", "color observed with newColor: $newColor")
            changeVectorDrawableColor(R.drawable.jelly_body, newColor)
        }

        avatarViewModel.cloth.observe(viewLifecycleOwner) { selectedCloth ->
            Log.d("AvatarFragment", "cloth observed with selectedCloth: $selectedCloth")
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
        binding.avatarImageCloth1.visibility = if (selectedCloth == R.drawable.cloth1_inrec) View.VISIBLE else View.INVISIBLE
        binding.avatarImageCloth2.visibility = if (selectedCloth == R.drawable.cloth2_inrec) View.VISIBLE else View.INVISIBLE
        binding.avatarImageCloth3.visibility = if (selectedCloth == R.drawable.cloth3_inrec) View.VISIBLE else View.INVISIBLE
        binding.avatarImageCloth4.visibility = if (selectedCloth == R.drawable.cloth4_inrec) View.VISIBLE else View.INVISIBLE
        binding.avatarImageCloth5.visibility = if (selectedCloth == R.drawable.cloth5_inrec) View.VISIBLE else View.INVISIBLE
    }

    private fun updateGlassesVisibility(selectedGlasses: Int) {
        binding.avatarImageGlasses1.visibility = if (selectedGlasses == R.drawable.glasses1_inrec) View.VISIBLE else View.INVISIBLE
        binding.avatarImageGlasses2.visibility = if (selectedGlasses == R.drawable.glasses2_inrec) View.VISIBLE else View.INVISIBLE
        binding.avatarImageGlasses3.visibility = if (selectedGlasses == R.drawable.glasses3_inrec) View.VISIBLE else View.INVISIBLE
    }

    private fun updateHatVisibility(selectedHat: Int) {
        binding.avatarImageHat1.visibility = if (selectedHat == R.drawable.hat1_inrec) View.VISIBLE else View.INVISIBLE
        binding.avatarImageHat2.visibility = if (selectedHat == R.drawable.hat2_inrec) View.VISIBLE else View.INVISIBLE
        binding.avatarImageHat3.visibility = if (selectedHat == R.drawable.hat3_inrec) View.VISIBLE else View.INVISIBLE
    }

    private fun changeVectorDrawableColor(drawableId: Int, colorId: Int) {
        Log.d("AvatarFragment", "changeVectorDrawableColor called with colorId: $colorId")
        val drawable = ContextCompat.getDrawable(requireContext(), drawableId)
        if (drawable != null) {
            val wrappedDrawable = drawable.mutate()
            wrappedDrawable.setTint(ContextCompat.getColor(requireContext(), colorId))
            binding.avatarImageBody.setImageDrawable(wrappedDrawable)
            Log.d("AvatarFragment", "Color changed to: ${ContextCompat.getColor(requireContext(), colorId)}")
        } else {
            Log.d("AvatarFragment", "Drawable not found")
        }
    }

    private fun captureAvatar(): Bitmap? {
        val avatarView = binding.avatarImageView
        val bitmap = Bitmap.createBitmap(avatarView.width, avatarView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        avatarView.draw(canvas)
        return bitmap
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

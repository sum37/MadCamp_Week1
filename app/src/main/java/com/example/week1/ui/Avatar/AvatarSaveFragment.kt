// AvatarSaveDialogFragment.kt
package com.example.week1.ui.Avatar

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.week1.R
import com.example.week1.databinding.FragmentAvatarSaveBinding

class AvatarSaveFragment : DialogFragment() {

    private var _binding: FragmentAvatarSaveBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAvatarSaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 이미지 저장 버튼 클릭 리스너
        binding.btnSaveImage.setOnClickListener {
//            saveImage()
            Log.d("tag", "이미지저장할거야")
        }

        // 연락처에 적용하기 버튼 클릭 리스너
        binding.btnApplyToContact.setOnClickListener {
//            applyToContact()
            Log.d("tag", "연락처적용할거야")
        }
    }

    private fun saveImage() {
        val bitmap = captureAvatar()
        // 이미지 저장 로직 구현
    }

    private fun applyToContact() {
        // 연락처 목록 불러오기 및 적용 로직 구현
    }

    private fun captureAvatar(): Bitmap {
        val avatarView = binding.root.findViewById<View>(R.id.avatar_image_view)
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

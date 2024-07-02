package com.example.week1.ui.Avatar

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.week1.databinding.FragmentAvatarSaveBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class AvatarSaveFragment : DialogFragment() {

    private var _binding: FragmentAvatarSaveBinding? = null
    private val binding get() = _binding!!
    private lateinit var capturedBitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAvatarSaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        capturedBitmap = requireArguments().getParcelable(ARG_CAPTURED_BITMAP)!!

        binding.imageViewCaptured.setImageBitmap(capturedBitmap)

        // 이미지 저장 버튼 클릭 리스너
        binding.btnSaveImage.setOnClickListener {
            checkWriteStoragePermission()
        }

        // 연락처에 적용하기 버튼 클릭 리스너
        binding.btnApplyToContact.setOnClickListener {
            applyToContact()
        }
    }

    private fun checkWriteStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_WRITE_STORAGE
            )
        } else {
            saveImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_STORAGE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                saveImage()
            } else {
                Toast.makeText(
                    requireContext(),
                    "저장을 위해 외부 저장소 쓰기 권한이 필요합니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveImage() {
        val filename = "captured_image_${System.currentTimeMillis()}.png"
        var fos: OutputStream? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
                }
                val contentResolver = requireContext().contentResolver
                val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { contentResolver.openOutputStream(it) }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                val file = File(imagesDir, filename)
                fos = FileOutputStream(file)
            }

            if (fos != null) {
                capturedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()
                Toast.makeText(requireContext(), "이미지가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                Log.d("AvatarSaveFragment", "이미지 저장 완료: $filename")
            } else {
                Toast.makeText(requireContext(), "이미지 저장 실패", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "이미지 저장 실패", Toast.LENGTH_SHORT).show()
            Log.e("AvatarSaveFragment", "이미지 저장 실패: ${e.message}")
        }
    }

    private fun applyToContact() {
        // 연락처 적용 로직 구현
        Log.d("AvatarSaveFragment", "연락처에 적용 완료")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CAPTURED_BITMAP = "captured_bitmap"
        private const val REQUEST_CODE_WRITE_STORAGE = 1001

        fun newInstance(bitmap: Bitmap): AvatarSaveFragment {
            val fragment = AvatarSaveFragment()
            val args = Bundle()
            args.putParcelable(ARG_CAPTURED_BITMAP, bitmap)
            fragment.arguments = args
            return fragment
        }
    }
}

package com.example.week1.ui.Avatar

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
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

    private fun saveImage() {
        // 저장할 디렉터리 경로 설정 (여기서는 Pictures 디렉터리에 저장하는 예시입니다)
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val fileName = "avatar_image.png"

        // 실제 파일 경로 생성
        val file = File(directory, fileName)

        // 파일 출력 스트림 열기
        var fileOutputStream: FileOutputStream? = null

        Log.d("tag", "이까진 들어오는거야?")
        try {
            fileOutputStream = FileOutputStream(file)
            capturedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()

            // 저장 완료 메시지 출력
            Toast.makeText(requireContext(), "이미지가 저장되었습니다.", Toast.LENGTH_SHORT).show()
            Log.d("AvatarSaveFragment", "이미지 저장 완료: ${file.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "이미지 저장 실패", Toast.LENGTH_SHORT).show()
            Log.e("AvatarSaveFragment", "이미지 저장 실패: ${e.message}")
        } finally {
            fileOutputStream?.close()
        }
    }

    private fun checkWriteStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없을 경우 권한 요청
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_WRITE_STORAGE)
        } else {
            // 권한이 있을 경우 이미지 저장 메서드 호출
            saveImage()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우 이미지 저장 메서드 호출
                saveImage()
            } else {
                // 권한이 거부된 경우 사용자에게 메시지 표시
                Toast.makeText(requireContext(), "저장을 위해 외부 저장소 쓰기 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                Log.d("AvatarSaveFragment", "외부 저장소 쓰기 권한이 필요합니다.")
            }
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

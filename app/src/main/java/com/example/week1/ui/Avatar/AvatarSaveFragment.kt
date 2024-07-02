package com.example.week1.ui.Avatar

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.week1.databinding.FragmentAvatarSaveBinding
import com.example.week1.ui.Phone.ContactListDialogFragment
import com.example.week1.ui.Phone.ContactsData
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class AvatarSaveFragment : DialogFragment() {

    private var _binding: FragmentAvatarSaveBinding? = null
    private val binding get() = _binding!!
    private lateinit var capturedBitmap: Bitmap
    private var selectedContact: ContactsData? = null

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
            val contactListDialog = ContactListDialogFragment.newInstance()
            contactListDialog.setOnContactSelectedListener(object : ContactListDialogFragment.OnContactSelectedListener {
                override fun onContactSelected(contact: ContactsData) {
                    selectedContact = contact
                    checkContactsPermissions()
                }
            })
            contactListDialog.show(parentFragmentManager, "ContactListDialogFragment")
        }
    }

    private fun checkContactsPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
        )
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(permissionsToRequest.toTypedArray(), REQUEST_CODE_CONTACTS_PERMISSIONS)
        } else {
            selectedContact?.let { contact ->
                updateContactPhoto(contact.rawContactId, capturedBitmap)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CONTACTS_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                selectedContact?.let { contact ->
                    updateContactPhoto(contact.rawContactId, capturedBitmap)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "연락처 수정을 위해 권한이 필요합니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (requestCode == REQUEST_CODE_WRITE_STORAGE) {
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

    private fun updateContactPhoto(rawContactId: Int, bitmap: Bitmap) {
        val contentValues = ContentValues()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val photoByteArray = stream.toByteArray()

        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        contentValues.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
        contentValues.put(ContactsContract.CommonDataKinds.Photo.PHOTO, photoByteArray)
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)

        val where = "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
        val params = arrayOf(rawContactId.toString(), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)

        try {
            val rowsUpdated = requireContext().contentResolver.update(
                ContactsContract.Data.CONTENT_URI,
                contentValues,
                where,
                params
            )

            if (rowsUpdated > 0) {
                Toast.makeText(requireContext(), "연락처 사진이 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                // 사진이 없는 경우 삽입을 시도합니다.
                val newContactUri = requireContext().contentResolver.insert(
                    ContactsContract.Data.CONTENT_URI,
                    contentValues
                )
                if (newContactUri != null) {
                    Toast.makeText(requireContext(), "연락처 사진이 새로 추가되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "연락처 사진 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "연락처 사진 업데이트 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CAPTURED_BITMAP = "captured_bitmap"
        private const val REQUEST_CODE_WRITE_STORAGE = 1001
        private const val REQUEST_CODE_CONTACTS_PERMISSIONS = 1003

        fun newInstance(bitmap: Bitmap): AvatarSaveFragment {
            val fragment = AvatarSaveFragment()
            val args = Bundle()
            args.putParcelable(ARG_CAPTURED_BITMAP, bitmap)
            fragment.arguments = args
            return fragment
        }
    }
}

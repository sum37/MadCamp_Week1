package com.example.week1.ui.Avatar

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R
import com.example.week1.databinding.FragmentAvatarBinding
import com.example.week1.ui.Phone.ContactsAdapter
import com.example.week1.ui.Phone.ContactsData
import com.google.android.material.tabs.TabLayoutMediator
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class AvatarFragment : Fragment(), ContactsAdapter.OnItemClickListener {

    private var _binding: FragmentAvatarBinding? = null
    private val binding get() = _binding!!
    private lateinit var avatarViewModel: AvatarViewModel
    private lateinit var avatarImageView: ImageView

    private val REQUEST_WRITE_EXTERNAL_STORAGE = 1
    private var capturedImage: Bitmap? = null // 캡쳐한 이미지를 저장할 변수
    private var contactsList = ArrayList<ContactsData>() // 연락처 리스트를 저장할 변수
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var contactsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("AvatarFragment", "onCreateView called")
        avatarViewModel = ViewModelProvider(requireActivity()).get(AvatarViewModel::class.java)

        _binding = FragmentAvatarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        avatarImageView = binding.avatarImage

        val captureButton: ImageButton = binding.captureButton
        captureButton.setOnClickListener {
            val bitmap = captureImageView(avatarImageView)
            showCapturedImage(bitmap)
        }

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        val adapter = AvatarPagerAdapter(requireActivity())
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 초기화
        contactsRecyclerView = view.findViewById(R.id.contactsList)
        contactsAdapter = ContactsAdapter(contactsList, this)
        contactsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        contactsRecyclerView.adapter = contactsAdapter

        observeViewModel()
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

    private fun captureImageView(view: ImageView): Bitmap {
        // Create a bitmap with the same dimensions as the view
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        // Create a canvas to draw on the bitmap
        val canvas = Canvas(bitmap)
        // Draw the view on the canvas
        view.draw(canvas)
        capturedImage = bitmap // 캡쳐한 이미지를 저장
        return bitmap
    }

    private fun showCapturedImage(bitmap: Bitmap) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_captured_image, null)
        val capturedImageView: ImageView = dialogView.findViewById(R.id.captured_image_view)
        capturedImageView.setImageBitmap(bitmap)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton(null, null)
            .create()

        val closeButton: ImageButton = dialogView.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        val saveButton: Button = dialogView.findViewById(R.id.save_button)
        saveButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_EXTERNAL_STORAGE)
            } else {
                saveImageToGallery(bitmap)
            }
        }

        val setContactImageButton: Button = dialogView.findViewById(R.id.set_contact_image_button)
        setContactImageButton.setOnClickListener {
            dialog.dismiss()
            showContactSelectionDialog()
        }

        dialog.show()
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val filename = "captured_image_${System.currentTimeMillis()}.png"
        var fos: OutputStream? = null
        var imageUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
                }
                val contentResolver = requireContext().contentResolver
                imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { contentResolver.openOutputStream(it) }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                val file = File(imagesDir, filename)
                fos = FileOutputStream(file)
                imageUri = Uri.fromFile(file)
            }

            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()
                Toast.makeText(requireContext(), "Image saved to gallery", Toast.LENGTH_SHORT).show()

                // 스캔하여 갤러리에 표시
                imageUri?.let { uri ->
                    MediaScannerConnection.scanFile(requireContext(), arrayOf(uri.path), arrayOf("image/png"), null)
                }
            } else {
                Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showContactSelectionDialog() {
        // 연락처 리스트를 가져오는 로직 추가
        getContactsList()

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_contacts_list, null)
        val recyclerView: RecyclerView = dialogView.findViewById(R.id.contacts_recycler_view)
        val contactsAdapter = ContactsAdapter(contactsList, this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = contactsAdapter

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton(null, null)
            .create()

        dialog.show()
    }

    private fun getContactsList() {
        val contacts = requireContext().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        val list = ArrayList<ContactsData>()
        contacts?.let {
            while (it.moveToNext()) {
                val contactsId = contacts.getInt(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                val name = contacts.getString(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = contacts.getString(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val photoUriString = contacts.getString(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                val photoUri = photoUriString?.let { Uri.parse(it) }
                list.add(ContactsData(contactsId, name, number, photoUri))
            }
        }
        list.sortBy { it.name }
        contacts?.close()
        contactsList.clear()
        contactsList.addAll(list)
        contactsAdapter.notifyDataSetChanged() // 리스트 갱신 후 어댑터에 알림
    }

    private fun changeContactPhoto(contactId: Int) {
        Log.d("AvatarFragment", "changeContactPhoto called with contactId: $contactId")
        capturedImage?.let { bitmap ->
            val photoBytes = bitmapToByteArray(bitmap)
            val contentValues = ContentValues().apply {
                put(ContactsContract.Data.RAW_CONTACT_ID, contactId) // 여기서 contactId가 RAW_CONTACT_ID인지 확인 필요
                put(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                put(ContactsContract.CommonDataKinds.Photo.PHOTO, photoBytes)
                put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
            }

            val selection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
            val selectionArgs = arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
            val updatedRows = requireContext().contentResolver.update(ContactsContract.Data.CONTENT_URI, contentValues, selection, selectionArgs)

            Log.d("AvatarFragment", "updatedRows: $updatedRows")

            if (updatedRows > 0) {
                Log.d("AvatarFragment", "연락처 사진이 변경되었습니다.")
                Toast.makeText(requireContext(), "연락처 사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                updateContactList() // 업데이트 후 연락처 리스트 새로고침
            } else {
                Log.d("AvatarFragment", "업데이트 실패, 새로 추가 시도")
                // 업데이트가 실패했을 때, 새로 추가
                val newContentValues = ContentValues().apply {
                    put(ContactsContract.Data.RAW_CONTACT_ID, contactId) // 이 부분이 RAW_CONTACT_ID인지 확인 필요
                    put(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                    put(ContactsContract.CommonDataKinds.Photo.PHOTO, photoBytes)
                    put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                }
                requireContext().contentResolver.insert(ContactsContract.Data.CONTENT_URI, newContentValues)
                Toast.makeText(requireContext(), "연락처 사진이 새로 추가되었습니다.", Toast.LENGTH_SHORT).show()
                updateContactList() // 업데이트 후 연락처 리스트 새로고침
            }

            // 현재 프래그먼트를 종료하고 이전 탭으로 돌아가기
            parentFragmentManager.popBackStack()
        } ?: run {
            Log.d("AvatarFragment", "캡쳐된 이미지가 없습니다.")
            Toast.makeText(requireContext(), "캡쳐된 이미지가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun updateContactList() {
        // 연락처 리스트를 새로 가져와서 업데이트
        getContactsList()
        // RecyclerView를 새로고침
        contactsAdapter.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 권한이 허용되었을 때의 처리
                Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // 권한이 거부되었을 때의 처리
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(contact: ContactsData) {
        // 연락처 아이템 클릭 시 처리 로직
        changeContactPhoto(contact.id)
    }
}

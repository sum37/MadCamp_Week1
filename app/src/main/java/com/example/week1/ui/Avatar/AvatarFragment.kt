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
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.week1.R
import com.example.week1.databinding.FragmentAvatarBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class AvatarFragment : Fragment() {

    private var _binding: FragmentAvatarBinding? = null
    private val binding get() = _binding!!
    private lateinit var avatarViewModel: AvatarViewModel
    private lateinit var avatarImageView: ImageView

    private val REQUEST_WRITE_EXTERNAL_STORAGE = 1

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

    private fun captureImageView(view: ImageView): Bitmap {
        // Create a bitmap with the same dimensions as the view
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        // Create a canvas to draw on the bitmap
        val canvas = Canvas(bitmap)
        // Draw the view on the canvas
        view.draw(canvas)
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
}



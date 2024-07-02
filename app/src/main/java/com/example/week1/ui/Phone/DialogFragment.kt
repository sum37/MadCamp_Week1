package com.example.week1.ui.Phone

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.week1.databinding.FragmentContactDetailDialogBinding
import java.io.InputStream

class ContactDetailDialogFragment : DialogFragment() {

    private var _binding: FragmentContactDetailDialogBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_CONTACT = "contact"

        fun newInstance(contact: ContactsData): ContactDetailDialogFragment {
            val fragment = ContactDetailDialogFragment()
            val args = Bundle()
            args.putParcelable(ARG_CONTACT, contact)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactDetailDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val contact = arguments?.getParcelable<ContactsData>(ARG_CONTACT)
        contact?.let {
            binding.txtName.text = it.name
            binding.txtNumber.text = it.number
            loadContactPhoto(it.id) // 사진 로드 함수 호출

            binding.btnCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${contact.number}")
                }
                startActivity(intent)
            }
        }
    }

    private fun loadContactPhoto(contactId: Int) {
        val photoUri = Uri.withAppendedPath(
            ContactsContract.Contacts.CONTENT_URI, contactId.toString()
        )
        val inputStream: InputStream? = ContactsContract.Contacts.openContactPhotoInputStream(
            requireContext().contentResolver, photoUri
        )
        inputStream?.let {
            val bitmap = BitmapFactory.decodeStream(it)
            binding.imgContactPhoto.setImageBitmap(bitmap)
            it.close()
        } ?: run {
            Log.d("ContactDetailDialog", "No photo found for contact with ID: $contactId")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return dialog
    }
}

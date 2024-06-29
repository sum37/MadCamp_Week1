package com.example.week1.ui.Phone

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.week1.databinding.FragmentPhoneBinding

class PhoneFragment : Fragment(), View.OnClickListener {

    companion object {
        const val PERMISSION_REQUEST_CODE = 100
        private const val TAG = "PhoneFragment"
    }

    private var _binding: FragmentPhoneBinding? = null
    private val binding get() = _binding!!
    private var contactsAdapter: ContactsAdapter? = null
    private var contactsList = ArrayList<ContactsData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        initListener()
        onCheckContactsPermission()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "ㅋㅋㅋresume")
        onCheckContactsPermission()
    }

    private fun initLayout() {
        binding.includeTitle.txtTitle.text = "주소록"
        binding.contactsList.layoutManager = LinearLayoutManager(context)
        contactsAdapter = ContactsAdapter(contactsList)
        binding.contactsList.adapter = contactsAdapter
    }

    private fun initListener() {
        binding.btnPermission.setOnClickListener(this)
        binding.btnAddContacts.setOnClickListener(this)
        binding.includeTitle.btnDelete.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnPermission -> {
                requestPermission()
            }
            binding.btnAddContacts -> {
                // 연락처 추가 기능 구현
            }
            binding.includeTitle.btnDelete -> {
                // 삭제 기능 구현
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onCheckContactsPermission() {
        val permissionDenied = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED
        Log.d(TAG, "여긴 들어오나요?")
        binding.btnPermission.isVisible = permissionDenied
        binding.txtDescription.isVisible = permissionDenied
        binding.btnAddContacts.isVisible = !permissionDenied
        binding.contactsList.isVisible = !permissionDenied
        if (permissionDenied) {
            binding.txtDescription.text = "권한을 허용하셔야 이용하실 수 있습니다."
        } else {
            Log.d(TAG,"hihi")
            getContactsList()
        }
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (ContextCompat.checkSelfPermission(requireContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            onCheckContactsPermission()
        } else {
            if (shouldShowRequestPermissionRationale(permissions[0])) {
                binding.txtDescription.text = "권한이 거절되었습니다."
            } else {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("권한")
                    setMessage("권한을 허용하기 위해서 설정으로 이동합니다.")
                    setPositiveButton("확인") { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", requireContext().packageName, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    }
                    setNegativeButton("거절") { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            }
        }
    }

    private fun getContactsList() {
        val contacts = requireContext().contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        val list = ArrayList<ContactsData>()
        contacts?.let {
            while (it.moveToNext()) {
                val contactsId = contacts.getInt(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                val name = contacts.getString(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = contacts.getString(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                list.add(ContactsData(contactsId, name, number))
            }
        }
        list.sortBy { it.name }
        contacts?.close()
        contactsList.clear()
        contactsList.addAll(list)
        setContacts()
    }

    private fun setContacts() {
        contactsAdapter?.notifyDataSetChanged()
    }
}

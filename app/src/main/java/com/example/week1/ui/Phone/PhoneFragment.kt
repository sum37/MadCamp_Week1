package com.example.week1.ui.Phone

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.week1.databinding.FragmentPhoneBinding

class PhoneFragment : Fragment(), ContactsAdapter.OnItemClickListener {

    companion object {
        const val PERMISSION_REQUEST_CODE = 100
        private const val TAG = "PhoneFragment"
    }

    private var _binding: FragmentPhoneBinding? = null
    private val binding get() = _binding!!
    private var contactsAdapter: ContactsAdapter? = null
    private var contactsList = ArrayList<ContactsData>()
    private lateinit var phoneViewModel: PhoneViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        phoneViewModel =
            ViewModelProvider(this).get(PhoneViewModel::class.java)

        _binding = FragmentPhoneBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPhone
        phoneViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        initListener()
        onCheckContactsPermission()
    }

    override fun onResume() {
        super.onResume()
        onCheckContactsPermission()
    }

    private fun initLayout() {
        binding.contactsList.layoutManager = LinearLayoutManager(context)
    }

    private fun initListener() {
        binding.btnPermission.setOnClickListener {
            requestPermission()
        }
        binding.btnAddContacts.setOnClickListener {
            // 연락처 추가 기능 구현
        }
    }

    private fun onCheckContactsPermission() {
        val permissionDenied = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED
        binding.btnPermission.isVisible = permissionDenied
        binding.txtDescription.isVisible = permissionDenied
        binding.btnAddContacts.isVisible = !permissionDenied
        binding.contactsList.isVisible = !permissionDenied
        if (permissionDenied) {
            binding.txtDescription.text = "권한을 허용하셔야 이용하실 수 있습니다."
        } else {
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

    private fun prepareContactPairs(contacts: List<ContactsData>): List<ContactPair> {
        val contactPairs = mutableListOf<ContactPair>()

        for (i in contacts.indices step 2) {
            val contact1 = contacts[i]
            val contact2 = if (i + 1 < contacts.size) contacts[i + 1] else null
            contactPairs.add(ContactPair(contact1, contact2))
        }

        return contactPairs
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

        val contactPairs = prepareContactPairs(contactsList)
        contactsAdapter = ContactsAdapter(contactPairs, this)
        binding.contactsList.adapter = contactsAdapter
        contactsAdapter?.notifyDataSetChanged()
    }

    override fun onItemClick(contact: ContactsData) {
        val dialogFragment = ContactDetailDialogFragment.newInstance(contact)
        dialogFragment.show(parentFragmentManager, "ContactDetailDialogFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

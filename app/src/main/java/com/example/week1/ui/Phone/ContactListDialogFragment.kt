package com.example.week1.ui.Phone

import ContactsAdapter
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.week1.databinding.FragmentContactListBinding

class ContactListDialogFragment : DialogFragment(), ContactsAdapter.OnItemClickListener {

    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!
    private lateinit var contactsAdapter: ContactsAdapter
    private var contactsList = ArrayList<ContactsData>()

    interface OnContactSelectedListener {
        fun onContactSelected(contact: ContactsData)
    }

    private var listener: OnContactSelectedListener? = null

    fun setOnContactSelectedListener(listener: OnContactSelectedListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.contactsList.layoutManager = LinearLayoutManager(context)
        onCheckContactsPermission()
    }

    private fun onCheckContactsPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadContacts()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun loadContacts() {
        val contacts = requireContext().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )
        contacts?.let {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                val rawContactId = getRawContactId(id)
                val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsList.add(ContactsData(id, rawContactId, name, number))
            }
            it.close()
        }
        contactsList.sortBy { it.name }
        contactsAdapter = ContactsAdapter(prepareContactPairs(contactsList), this)
        binding.contactsList.adapter = contactsAdapter
        contactsAdapter.notifyDataSetChanged()
    }

    private fun getRawContactId(contactId: Int): Int {
        val rawContactUri = ContactsContract.RawContacts.CONTENT_URI
        val projection = arrayOf(ContactsContract.RawContacts._ID)
        val selection = "${ContactsContract.RawContacts.CONTACT_ID} = ?"
        val selectionArgs = arrayOf(contactId.toString())

        requireContext().contentResolver.query(
            rawContactUri,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.RawContacts._ID))
            }
        }
        return -1
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts()
        } else {
            Toast.makeText(context, "연락처 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClick(contact: ContactsData) {
        listener?.onContactSelected(contact)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1002

        fun newInstance(): ContactListDialogFragment {
            return ContactListDialogFragment()
        }
    }
}

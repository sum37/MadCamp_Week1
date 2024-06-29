package com.example.week1.ui.Phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.week1.databinding.FragmentPhoneBinding

class PhoneFragment : Fragment() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 100
    }

    private var _binding: FragmentPhoneBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    // FragmentPhoneBinding이 본 페이지에 해당하는 xml 과 이 파일을 연결해주는거임

    private val binding by lazy { FragmentPhoneBinding.inflate(layoutInflater) }
    private var contactsAdapter: ContactsAdapter? = null
    private var contactsList = ArrayList<ContactsData>()

//    override
    fun onClick(v: View?) {
        when (v) {
            binding.btnPermission -> {
                requestPermission()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initLayout()
        initListener()

        onCheckContactsPermission()
    }

    override fun onResume() {
        super.onResume()
        onCheckContactsPermission()
    }

    private fun initLayout() {
        setContentView(binding.root)
        binding.includeTitle.txtTitle.text = "주소록"
    }

    private fun initListener() {
        binding.btnPermission.setOnClickListener(this)
        binding.btnAddContacts.setOnClickListener(this)
        binding.includeTitle.btnDelete.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onCheckContactsPermission() {
        val permissionDenied = checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_DENIED
                || checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED

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


}
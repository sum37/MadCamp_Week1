package com.example.week1.ui.Avatar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.week1.databinding.FragmentAvatarBinding

class AvatarFragment : Fragment() {

private var _binding: FragmentAvatarBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val AvatarViewModel =
            ViewModelProvider(this).get(AvatarViewModel::class.java)

    _binding = FragmentAvatarBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val textView: TextView = binding.textAvatar
    AvatarViewModel.text.observe(viewLifecycleOwner) {
      textView.text = it
    }
    return root
  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
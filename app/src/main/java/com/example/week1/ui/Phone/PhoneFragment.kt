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


    private var _binding: FragmentPhoneBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    // FragmentPhoneBinding이 본 페이지에 해당하는 xml 과 이 파일을 연결해주는거임
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, // xml layout file을 view 객체로 보여준다
        container: ViewGroup?, //
        savedInstanceState: Bundle? // 이전에 저장한 상태 데이터를 사용해 프래그먼트 복제, 화면회전과 같은 상황에서 fragment유지할 수 있음
    ): View {

        val view =
            ViewModelProvider(this).get(PhoneViewModel::class.java)
        // viewModelProvider는 ViewModel을 생성하고 반환한다.
        _binding = FragmentPhoneBinding.inflate(inflater, container, false)
        // 여기서 fragment_phone을 view로 보여주는거임
        val root: View = binding.root
        // binding.root는 여기서 생성된 FragmentPhoneBinding의 최상위 View를 참조하는겁니다.

        return root // binding의 root view를 return 한다.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.phodo.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.phodo.PhotoGuide.PhotoGuideList
import com.example.phodo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var frag_home_binding : FragmentHomeBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
   //private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        frag_home_binding = FragmentHomeBinding.inflate(layoutInflater)
        val view = frag_home_binding.root

        frag_home_binding.circleIv.setOnClickListener {

        }
        frag_home_binding.imageView.setOnClickListener {
            val intent = Intent(context, PhotoGuideList::class.java)
            startActivity(intent)

        }

        //val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}
package com.example.phodo.PhotoMap

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.phodo.HomeActivity
import com.example.phodo.PhotoGuide.PhotoGuideList
import com.example.phodo.R
import com.example.phodo.databinding.ActivityMapBinding
import com.example.phodo.databinding.FragmentBottomSheetBinding
import com.example.phodo.databinding.FragmentHomeBinding
import com.example.phodo.dto.PhotoSpotItemDTO
import com.example.phodo.photoMaker.PhotoMaker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BottomSheet.newInstance] factory method to
 * create an instance of this fragment.
 */
interface BottomSheetListener {
    fun onDismiss()
}

class BottomSheet() : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var listener: BottomSheetListener? = null

    private lateinit var sheet_binding: FragmentBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sheet_binding = FragmentBottomSheetBinding.inflate(layoutInflater)
        val view = sheet_binding.root

        sheet_binding.button2.setOnClickListener {
            // 해당 포토가이드만 보러 가게 수정
            val intent = Intent(context, PhotoGuideList::class.java)
            startActivity(intent)
        }

        sheet_binding.button3.setOnClickListener {
            val intent1 = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent1.type = "image/*"
            startActivityForResult(intent1, Activity.RESULT_OK)
        }

        return view

        //return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun setMyBottomSheetDialogListener(listener: BottomSheetListener) {
        this.listener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDismiss()
    }

    fun setSpotInfo(spotInfo : PhotoSpotItemDTO) {
        sheet_binding.spotName.text = spotInfo.photoSpotName
        sheet_binding.spotInfo.text = "${spotInfo.photoGuideNum}" + "개의 포토가이드"
        Picasso.get()
            .load(spotInfo.photoSpotImage.toString())
            .into(sheet_binding.titleImg)
        sheet_binding.tag.text = ""
        for (i in 0 until spotInfo.hashtags.size) {
            sheet_binding.tag.append("#"+spotInfo.hashtags[i]+" ")
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == Activity.RESULT_OK) {

            if (resultCode == Activity.RESULT_OK) {
                try {
                    val selected_gallery_img = data!!.data

                    // 포토메이커 액티비티 실행
                    val intent = Intent(context, PhotoMaker::class.java)
                    intent.putExtra("photo_obj", selected_gallery_img.toString())
                    startActivity(intent)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BottomSheet.
         */
        // TODO: Rename and change types and number of parameters
        /*
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BottomSheet(context).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

         */
    }


}
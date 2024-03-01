package com.howlab.mvvmhowlstagram

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.howlab.mvvmhowlstagram.databinding.ActivityAddPhotoBinding

class AddPhotoActivity : AppCompatActivity() {
    var photoUri: Uri? = null       // 사진경로를 글로벌로 저장할 수 있는 uri 생성
    var photoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // 사진을 선택하면 콜백받을 수 있는 register
        if (it.resultCode == RESULT_OK) {
            // resultCode가 OK이면 Uri에 넣어줌
            photoUri = it.data?.data
            binding.uploadImageview.setImageURI(photoUri)
        }
    }

    lateinit var binding: ActivityAddPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_photo)

        var i = Intent(Intent.ACTION_PICK)
        i.type = "image/*"
        photoResult.launch(i)
    }
}
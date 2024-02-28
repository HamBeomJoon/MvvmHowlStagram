package com.howlab.mvvmhowlstagram.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    var auth = FirebaseAuth.getInstance()
    var id: MutableLiveData<String> = MutableLiveData("BeomJoon")
    var password: MutableLiveData<String> = MutableLiveData("")

    var showInputNumberActivity: MutableLiveData<Boolean> = MutableLiveData(false)
    var showFindIdActivity: MutableLiveData<Boolean> = MutableLiveData(false)

    fun loginWithSignupEmail() {
        println("Email")
        auth.createUserWithEmailAndPassword(id.value.toString(), password.value.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showInputNumberActivity.value = true
                } else {
                    // 회원가입 실패 (아이디가 이미 있을 경우)
                }
            }
    }
}
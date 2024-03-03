package com.howlab.mvvmhowlstagram.login

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.core.View
import com.howlab.mvvmhowlstagram.R

class LoginViewModel : ViewModel() {
    var auth = FirebaseAuth.getInstance()
    var id: MutableLiveData<String> = MutableLiveData("")
    var password: MutableLiveData<String> = MutableLiveData("")
    var showInputNumberActivity: MutableLiveData<Boolean> = MutableLiveData(false)
    var showFindIdActivity: MutableLiveData<Boolean> = MutableLiveData(false)
    var showMainActivity: MutableLiveData<Boolean> = MutableLiveData(false)
//    val context = getApplication<Application>().applicationContext
//
//    init {
//        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(context.getString(R.string.default_web_client_id))
//    }

    fun loginWithSignupEmail() {
        println("Email")
        auth.createUserWithEmailAndPassword(id.value.toString(), password.value.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showInputNumberActivity.value = true
                } else {
                    // 아이디가 이미 있을 경우
                    loginEmail()
                }
            }
    }

    fun loginEmail() {
        auth.signInWithEmailAndPassword(id.value.toString(), password.value.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.user?.isEmailVerified == true) {
                        showMainActivity.value = true
                    } else {
                        showInputNumberActivity.value = true
                    }
                }
            }
//
//        fun loginGoogle(view: View) {
//            var i = googleSignInClient.singInIntent
//            (view.context as? LongActivity)?.googleLoginResult?.launch(i)
//        }
    }
}
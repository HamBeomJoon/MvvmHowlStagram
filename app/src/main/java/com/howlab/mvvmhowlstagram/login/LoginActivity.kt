package com.howlab.mvvmhowlstagram.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.howlab.mvvmhowlstagram.MainActivity
import com.howlab.mvvmhowlstagram.R
import com.howlab.mvvmhowlstagram.databinding.ActivityLoginBinding
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.google.android.gms.tasks.Task

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityLoginBinding
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoSdk.init(this, Constants.APP_KEY) // 카카오 SDK 초기화
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = loginViewModel
        binding.activity = this
        binding.lifecycleOwner = this
//        Log.d(TAG, "keyhash : ${Utility.getKeyHash(this)}")

        setObserve()

        // 카카오 로그인 버튼에 클릭 리스너 설정
        binding.kakaoLoginButton.setOnClickListener(this)

        // ActivityResultLauncher
        setResultSignUp()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        with(binding) {
            googleLoginButton.setOnClickListener {
                signIn()
            }
        }

        setContentView(binding.root)
    }

    // 카카오 로그인 처리
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.kakao_login_button -> {
                // 카카오 로그인 처리
                UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
                    if (error != null) {
                        // 카카오 로그인 실패
                        Log.e("KakaoLogin", "카카오 로그인 실패", error)
                    } else {
                        // 카카오 로그인 성공
                        // 사용자 정보 가져오기
                        UserApiClient.instance.me { user, error ->
                            if (error != null) {
                                // 사용자 정보 가져오기 실패
                                Log.e("KakaoLogin", "사용자 정보 요청 실패", error)
                            } else if (user != null) {
                                // 사용자 정보 가져오기 성공
                                val userId = user.id.toString() // 사용자의 고유 ID
                                val nickname = user.kakaoAccount?.profile?.nickname // 사용자의 닉네임
                                Log.d("KakaoLogin", "사용자 ID: $userId, 닉네임: $nickname")
                                // 가져온 사용자 정보를 활용하여 로직 수행
                            }
                        }
                    }
                }
            }
            // 다른 버튼 등의 처리도 필요하다면 추가할 수 있습니다.
        }
    }

    private fun setObserve() {
        loginViewModel.showInputNumberActivity.observe(this) {
            if (it) {
                finish()
                startActivity(Intent(this, InputNumberActivity::class.java))
            }
        }
        loginViewModel.showFindIdActivity.observe(this) {
            if (it) {
                startActivity(Intent(this, FindIdActivity::class.java))
            }
        }
        loginViewModel.showMainActivity.observe(this) {
            if (it) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    fun findId() {
        println("find Id")
        loginViewModel.showFindIdActivity.value = true
    }

    // 구글 로그인이 성공한 결과값을 받는 함수
    var googleLoginResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            account.idToken     // 로그인한 사용자 정보를 암호화한 값

        }

    private fun setResultSignUp() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // 정상적으로 결과가 받아와진다면 조건문 실행
                if (result.resultCode == Activity.RESULT_OK) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task)

                }
            }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
            val familyName = account?.familyName.toString()
            val givenName = account?.givenName.toString()
            val displayName = account?.displayName.toString()
            val photoUrl = account?.photoUrl.toString()

            Log.d("로그인한 유저의 이메일", email)
            Log.d("로그인한 유저의 성", familyName)
            Log.d("로그인한 유저의 이름", givenName)
            Log.d("로그인한 유저의 전체이름", displayName)
            Log.d("로그인한 유저의 프로필 사진의 주소", photoUrl)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("failed", "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
        resultLauncher.launch(signInIntent)
    }

}
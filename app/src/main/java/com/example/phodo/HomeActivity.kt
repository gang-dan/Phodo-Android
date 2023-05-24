package com.example.phodo

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.phodo.PhotoGuide.PhotoGuideDetailViewModel
import com.example.phodo.PhotoGuide.PhotoGuideItem
import com.example.phodo.databinding.ActivityHomeBinding
import com.example.phodo.utils.PreferenceUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class HomeActivity : AppCompatActivity() {

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel : HomeViewModel

    private lateinit var googleSignInClient : GoogleSignInClient
    private val RC_SIGN_IN = 1
    //lateinit var selected_photoguide : PhotoGuideItem
    //var isPhootoGuide = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        prefs = PreferenceUtil(applicationContext)

        initGoogleLogin()

        val intent: Intent = intent
        if (intent.extras != null) {
            viewModel.photoGuide.value = intent.getParcelableExtra<PhotoGuideItem>("selected_guide_item")
            viewModel.isPhootGuide.value = true
        } else {
            viewModel.isPhootGuide.value = false
        }


        setSupportActionBar(binding.appBarHome.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.appBarHome.toolbar.setTitleTextColor(Color.rgb(129,0,231))

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_camera_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // 네비게이션 클릭해 계정 정보 확인
        val headerView = navView.getHeaderView(0)
        headerView.setOnClickListener {
            //Prefs에 저장된 사용자 정보가 있는지, 토큰이 유효한지 확인
            if (isLogin()) {

                if (prefs.accessToken != null) {
                    // 토큰이 유효한지 확인
                    if (viewModel.isValidToken()) {
                        // 토큰 유효하면 화면에 정보 띄우고 그렇지 않으면 로그인 하라는 화면 띄움
                    } else {
                        // 로그인 초기화 하면 뜸
                        // 초기화 화면 버튼 클릭시 signIn() 으로 연결
                    }
                }

            } else {
                signIn()
                // 로그인해 실시간으로 서버에서 받은 정보와 토큰을 셋팅하고 화면에 띄움
            }

        }

    }


    private fun initGoogleLogin() {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(
                    Scope("https://www.googleapis.com/auth/userinfo.email"),
                    Scope("https://www.googleapis.com/auth/userinfo.profile"),
                    Scope("openid")
                )
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(binding.root.context, gso)

    }

    // 로그인 하면
    private fun signIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    // 단순 로그아웃 하는
    private fun signOut() {

        googleSignInClient.signOut()
            .addOnCompleteListener {
                Toast.makeText(this, "로그아웃 되셨습니다!", Toast.LENGTH_SHORT).show()
                //Prefs에 있는 정보 삭제 (서버에는 알려야 하는지?....안 알려도 될 것 같음)
            }
    }

    // 앱이 켜지자 마자 해당 메서드 수행,
    fun isLogin(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        return if (account == null) false else (true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val authCode = account.serverAuthCode
                if (authCode != null) {
                    val email = account.email
                    prefs.setAuthCode(authCode)
                    viewModel.requestLogin() //뷰모델에서 서버에 aceess 토큰 요청
                }

                Log.d("authCode","${authCode}")
            } catch (e: ApiException) {
                Log.d("ApiException", "handleSignInResult: error" + e.statusCode)
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_camera_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
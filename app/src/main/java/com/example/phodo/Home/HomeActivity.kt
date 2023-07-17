package com.example.phodo.Home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.phodo.R
import com.example.phodo.RetrofitInstance
import com.example.phodo.ViewModelFactory
import com.example.phodo.data.RemoteDataSourceImp
import com.example.phodo.databinding.ActivityHomeBinding
import com.example.phodo.dto.PhotoGuideItemDTO
import com.example.phodo.utils.PreferenceUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import org.opencv.android.OpenCVLoader


class HomeActivity : AppCompatActivity() {

    init{
        if (!OpenCVLoader.initDebug()) {
            Log.d("test", "OpenCV is not loaded!");
        } else {
            Log.d("test", "OpenCV is loaded successfully!");

        }
    }

    companion object {
        lateinit var prefs: PreferenceUtil
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    //private lateinit var viewModel : HomeViewModel

    private lateinit var googleSignInClient : GoogleSignInClient
    private val RC_SIGN_IN = 1
    lateinit var selected_photoguide : PhotoGuideItemDTO


    val viewModel : HomeViewModel by viewModels { ViewModelFactory(
        RemoteDataSourceImp(
            RetrofitInstance
        )
    ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val intent: Intent = intent
        if (intent.extras != null) {
            viewModel.photoGuide.value = intent.getParcelableExtra("selected_guide_item")
            viewModel.isPhootGuide.value = true

            //fragment.photoGuide.value = intent.getParcelableExtra<PhotoGuideItemDTO>("selected_guide_item")
            //fragment.isPhootGuide.value = true
        } else {
            viewModel.isPhootGuide.value = false
            //fragment.isPhootGuide.value = false
        }

        prefs = PreferenceUtil(applicationContext)

        initGoogleLogin()

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
        val logout = headerView.findViewById<Button>(R.id.button4)
        logout.setOnClickListener {
            //Prefs에 저장된 사용자 정보가 있는지, 토큰이 유효한지 확인
            signIn()
            /*
            if (prefs.accessToken != null) {
                val navView: NavigationView = binding.navView
                val headerView = navView.getHeaderView(0)
                val nameView = headerView.findViewById<TextView>(R.id.textView3)
                val login = headerView.findViewById<Button>(R.id.button4)
                val text = headerView.findViewById<TextView>(R.id.userName)

                nameView.text = prefs.userEmail + "\n" + prefs.userName
                nameView.isVisible = true
                login.isVisible = false
                text.text = "이제 나만의 포토가이드 만들기 기능을 사용하실 수 있습니다!!"

                val profileView = headerView.findViewById<ImageView>(R.id.userPicture)
                Picasso.get()
                    .load(prefs.userProfileImg)
                    .into(profileView)



            } else {
                signIn()
                // 로그인해 실시간으로 서버에서 받은 정보와 토큰을 셋팅하고 화면에 띄움

            }

             */
        }

        /*
        viewModel.isLogin.observe(this, Observer { //viewmodel에서 만든 변경관찰 가능한todoLiveData를 가져온다.
            if (it == true) {
                var user_name = navView.getHeaderView(R.id.userName) as TextView
                user_name.text = prefs.userName

            } else {

            }

        })

         */

    }


    private fun initGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(
                Scope("https://www.googleapis.com/auth/userinfo.email"),
                Scope("https://www.googleapis.com/auth/userinfo.profile"),
                Scope("openid")
            )
            .requestIdToken(getString(R.string.server_client_id))
            .requestServerAuthCode(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    // 로그인 하면
    private fun signIn() {
        Log.d("prefs","signIn")
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    // 단순 로그아웃 하는
    private fun signOut() {
        googleSignInClient.signOut()
            .addOnCompleteListener {
                //viewModel.requestLogout() //Prefs에 있는 정보 삭제
                Toast.makeText(this, "로그아웃 되셨습니다!", Toast.LENGTH_SHORT).show()

            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {

            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val authCode = account.serverAuthCode
                val give_name = account.givenName
                val family_name = account.familyName
                val email = account.email
                val profile = account.photoUrl.toString()
                Log.d("mail","${email}")

                if (authCode != null) {
                    Log.d("authCode","${authCode}")
                    prefs.setAuthCode(authCode)
                    prefs.setUserInfo(true,0,family_name+give_name,email!!,profile)

                    val navView: NavigationView = binding.navView
                    val headerView = navView.getHeaderView(0)
                    val nameView = headerView.findViewById<TextView>(R.id.textView3)
                    val login = headerView.findViewById<Button>(R.id.button4)
                    val infoText = headerView.findViewById<TextView>(R.id.userName)

                    nameView.text = prefs.userName + "\n" + email
                    nameView.isVisible = true
                    login.isVisible = false
                    infoText.text = "이제 나만의 포토가이드를 만들 수 있습니다!!"

                    val profileView = headerView.findViewById<ImageView>(R.id.userPicture)
                    Picasso.get()
                        .load(prefs.userProfileImg)
                        .into(profileView)

                    //LoginRepository().getAccessToken(authCode!!)
                    //viewModel.requestLogin(authCode) //뷰모델에서 서버에 aceess 토큰 요청
                }


            } catch (e: ApiException) {
                Log.d("ApiException", "handleSignInResult: error" + e.statusCode)
            }

        }
    }

    // 앱이 켜지자 마자 해당 메서드 수행,
    fun isLogin(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        return if (account == null) false else (true)
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
package com.example.phodo.Home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.phodo.*
import com.example.phodo.Home.camera.CameraFragment
import com.example.phodo.Home.gallery.GalleryFragment
import com.example.phodo.Home.slideshow.SlideshowFragment
import com.example.phodo.data.RemoteDataSourceImp
import com.example.phodo.databinding.ActivityHomeBinding
import com.example.phodo.dto.PhotoGuideItemDTO
import com.example.phodo.utils.PreferenceUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import org.opencv.android.OpenCVLoader


class HomeActivity : AppCompatActivity() {

    init{
        if (!OpenCVLoader.initDebug()) {
            Log.d("test", "OpenCV is not loaded!");
        } else {
            Log.d("test", "OpenCV is loaded successfully!");

        }
    }

    /*
    companion object {
        lateinit var prefs: PreferenceUtils
    }

     */


    //private lateinit var oneTapClient: SignInClient
    //private lateinit var signInRequest: BeginSignInRequest

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    //private lateinit var viewModel : HomeViewModel

    private lateinit var googleSignInClient : GoogleSignInClient
    //private lateinit var mCredentialsClient : CredentialsClient
    private val RC_SIGN_IN = 1
    lateinit var selected_photoguide : PhotoGuideItemDTO


    val viewModel : HomeViewModel by viewModels { ViewModelFactory(
        RemoteDataSourceImp(
            RetrofitInstance
        )
    ) }

    // 화면에 표시할 프래그먼트 생성
    lateinit var fragmentManager: FragmentManager
    lateinit var fragmentTransaction: FragmentTransaction

    val cameraFragment = CameraFragment()
    val photoguideFragment = GalleryFragment()
    val likeFragment = SlideshowFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_content_camera_home,cameraFragment).commitAllowingStateLoss()


        val intent: Intent = intent
        if (intent.extras != null) {
            // 적용할 포토가이드가 있는 경우
            viewModel.photoGuide.value = intent.getParcelableExtra("selected_guide_item")
            viewModel.isPhootGuide.value = true

            // 가이드라인 객체 생성
            val photoGuideLine = GuideLine()
            // Json -> list 메서드 호출

            //fragment.photoGuide.value = intent.getParcelableExtra<PhotoGuideItemDTO>("selected_guide_item")
            //fragment.isPhootGuide.value = true
        } else {
            viewModel.isPhootGuide.value = false
            //fragment.isPhootGuide.value = false
        }



        //prefs = PreferenceUtils() //applicationContext
        //prefs.init(this)
        //PreferenceUtils.init(this)
        //prefs.deleteAccessToken()
        //prefs.deleteUserInfo()

        // 구글 로그인 초기화
        initGoogleLogin()

        setSupportActionBar(binding.appBarHome.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.appBarHome.toolbar.setTitleTextColor(Color.rgb(129,0,231))

        val drawerLayout: DrawerLayout = binding.drawerLayout

        val cameraBtn = findViewById<LinearLayout>(R.id.nav_camera)
        val photoguideBtn = findViewById<LinearLayout>(R.id.nav_photoguide)
        val likeBtn = findViewById<LinearLayout>(R.id.nav_like)
        val settingBtn = findViewById<LinearLayout>(R.id.nav_setting)
        val signBtn = findViewById<LinearLayout>(R.id.nav_sign)

        cameraBtn.setOnClickListener {
            cameraBtn.background = ContextCompat.getDrawable(this, R.drawable.nav_camera_on)
            photoguideBtn.background = ContextCompat.getDrawable(this,R.drawable.nav_photoguide_off)
            likeBtn.background = ContextCompat.getDrawable(this,R.drawable.nav_like_off)

            fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment_content_camera_home, cameraFragment).commitAllowingStateLoss()
            drawerLayout.close()

        }

        photoguideBtn.setOnClickListener {
            cameraBtn.background = ContextCompat.getDrawable(this, R.drawable.nav_camera_off)
            photoguideBtn.background = ContextCompat.getDrawable(this,R.drawable.nav_photoguide_on)
            likeBtn.background = ContextCompat.getDrawable(this,R.drawable.nav_like_off)

            fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment_content_camera_home, photoguideFragment).commitAllowingStateLoss()
            drawerLayout.close()

        }

        likeBtn.setOnClickListener {
            cameraBtn.background = ContextCompat.getDrawable(this, R.drawable.nav_camera_off)
            photoguideBtn.background = ContextCompat.getDrawable(this,R.drawable.nav_photoguide_off)
            likeBtn.background = ContextCompat.getDrawable(this,R.drawable.nav_like_on)

            fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment_content_camera_home, likeFragment).commitAllowingStateLoss()
            drawerLayout.close()

        }


        binding.appBarHome.menuBtnTouchArea.setOnClickListener {
            if (PreferenceUtils.init(this)!!.getAccessToken() != "" ) { //&& PreferenceUtils.init(this)!!.userId != -1
                 //accessToken 유효성 체크 코드 추가 (현재 날짜와 비교)
                Log.d("로그인 이미 완료","로그인 이미 완료")
                //Log.d("access token","${prefs.accessToken}")
                //Log.d("uerid","${prefs.userId}")
                //viewModel.requestLogout()
                setUserInfo()

            } else { // 유효하지 않은 경우
                // 로그아웃 진행
                Log.d("로그아웃"," 로그아웃 진행")
                signOut()

            }
            drawerLayout.open()
        }


    }


    private fun initGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(
                Scope("https://www.googleapis.com/auth/userinfo.email"),
                Scope("https://www.googleapis.com/auth/userinfo.profile"),
                Scope("openid")
            )
            .requestIdToken(BuildConfig.SERVER_CLIENT_ID)
            .requestServerAuthCode(BuildConfig.SERVER_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }


    // 앱이 켜지자 마자 해당 메서드 수행,
    fun isLogin(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        return if (account == null) false else (true)
    }

    // 로그인 화면
    private fun signIn() {
        // 사용자 계정 선택 및 토큰 발급
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {

            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val authCode = account.serverAuthCode
                val IdToken = account.idToken
                val email = account.email
                Log.d("IdToken", "${IdToken}")

                if (IdToken != null && email != null) {
                    // 서버로부터 사용자 정보와 토큰 불러오기
                    viewModel.requestLogin(IdToken, email)
                    Log.d("토큰 요청", "토큰 요청")

                    viewModel.loginInfo.observe(this, Observer {
                        if(it != null) {
                            PreferenceUtils.init(this)!!.setUserInfo(
                                viewModel.loginInfo.value!!.isNewMember,
                                viewModel.loginInfo.value!!.memberId,
                                viewModel.loginInfo.value!!.memberName,
                                email,
                                viewModel.loginInfo.value!!.profileImage
                            )

                            // 토큰 정보 세팅
                            PreferenceUtils.init(this)!!.setAccessToken(viewModel.loginInfo.value!!.accessToken)
                            PreferenceUtils.init(this)!!.setAccessTokenExpTime(viewModel.loginInfo.value!!.accessTokenExpireTime)
                            PreferenceUtils.init(this)!!.setRefreshToken(viewModel.loginInfo.value!!.refreshToken)
                            PreferenceUtils.init(this)!!.setRefreshTokenExpTime(viewModel.loginInfo.value!!.refreshTokenExpireTime)

                            setUserInfo()

                        } else {
                            // 로그인 실패 안내 문구
                            Toast.makeText(this, "로그인에 실패했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }

                    })
                }

            } catch (e: ApiException) {
                Log.d("ApiException", "handleSignInResult: error" + e.statusCode)
            }

        } else {
            Log.d("Google Client Exception", "올바른 구글 클라이언트에 연결하지 못함")
        }
    }

    // 로그인시, 화면에 사용자 정보 셋팅
    fun setUserInfo() {

        val main_text = findViewById<TextView>(R.id.mainTextView)
        val sub_text = findViewById<TextView>(R.id.subTextView)
        //val profileView = findViewById<LinearLayout>(R.id.profile)

        main_text.text = PreferenceUtils.init(this)!!.userName
        sub_text.text = PreferenceUtils.init(this)!!.userEmail
        sub_text.isVisible = true

        /*
        Picasso.get()
            .load(prefs.userProfileImg)
            .into(profileView)
         */

        // 로그아웃 버튼 설정
        val signBtn = findViewById<LinearLayout>(R.id.nav_sign)
        signBtn.background = ContextCompat.getDrawable(applicationContext, R.drawable.nav_sign_out)
        signBtn.setOnClickListener {
            signOut()

        }
        // 마이페이지 버튼 설정
        val photoguideBtn = findViewById<LinearLayout>(R.id.nav_photoguide)
        val likeBtn = findViewById<LinearLayout>(R.id.nav_like)
        photoguideBtn.isVisible = true
        likeBtn.isVisible = true
    }

    fun signOut() {
        // prefs 정보 삭제
        //viewModel.requestLogout()
        PreferenceUtils.init(this)!!.clearToken()
        Log.d("로그아웃","${PreferenceUtils.init(this)!!.getAccessToken()}")
        // 구글 클라이언트 로그아웃
        googleSignInClient.signOut()
            .addOnCompleteListener(this, OnCompleteListener<Void?> {
                // 로그아웃이 성공적으로 완료될 때 호출되는 코드
            })

       //prefs.deleteUserInfo()

        // mainText 안내문구 설정
        val main_text : TextView = findViewById(R.id.mainTextView)
        // 텍스트에 이모지 추가
        val fireWorkUnicode = 0x1F347
        val emojiText = "로그인하고 더 다양한\n${String(Character.toChars(fireWorkUnicode))}포도를 즐겨보세요."
        main_text.text = emojiText

        val spannable = SpannableString(main_text.text)
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#5E498A")), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(Color.WHITE), 3,12 , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#5E498A")), 12, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(Color.WHITE), 16, main_text.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        main_text.text = spannable

        // subText 숨김
        val sub_text = findViewById<TextView>(R.id.subTextView)
        sub_text.isVisible = false

        // 로그인 버튼 설정
        val signBtn = findViewById<LinearLayout>(R.id.nav_sign)
        signBtn.background = ContextCompat.getDrawable(applicationContext, R.drawable.nav_sign_in)
        signBtn.setOnClickListener {
            signIn()
        }

        // photoguide, like 버튼 숨김
        val photoguideBtn = findViewById<LinearLayout>(R.id.nav_photoguide)
        val likeBtn = findViewById<LinearLayout>(R.id.nav_like)
        photoguideBtn.isVisible = false
        likeBtn.isVisible = false

    }

    // 로그아웃
    /*
    private fun signOut() {
        googleSignInClient.signOut()
            .addOnCompleteListener {
                //viewModel.requestLogout() //Prefs에 있는 정보 삭제
                Toast.makeText(this, "로그아웃 되셨습니다!", Toast.LENGTH_SHORT).show()

            }
    }

     */


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
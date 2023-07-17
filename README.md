# Phodo-Android
<div align=Left>
<img src="https://img.shields.io/badge/Android-3DDC84?style=round-square&logo=Android&logoColor=white"/>
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=round-square&logo=Kotlin&logoColor=white"/>
<img src="https://img.shields.io/badge/JetPack-4285F4?style=round-square&logo=JetPack&logoColor=white"/>
<img src="https://img.shields.io/badge/Retrofit2-000000?style=round-square&logo=Retrofit&logoColor=white"/>
</div>
<br>
> 위치별 인생샷 도우미 카메라 앱, 나만의 포토가이드를 만들고 공유할 수 있는 커뮤니티 기반 카메라 서비스

## 📱 ScreenShot
| | | | | | |
| -------- | -------- | -------- | -------- | -------- | -------- |
| <img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%86%E1%85%A1%E1%84%8B%E1%85%B5%E1%84%91%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%8C%E1%85%B5.png?raw=true" width="100" height="180">| <img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%91%E1%85%A9%E1%84%90%E1%85%A9%E1%84%80%E1%85%A1%E1%84%8B%E1%85%B5%E1%84%83%E1%85%B3%20%E1%84%86%E1%85%A9%E1%86%A8%E1%84%85%E1%85%A9%E1%86%A8.png?raw=true" width="100" height="180">| <img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%91%E1%85%A9%E1%84%90%E1%85%A9%E1%84%80%E1%85%A1%E1%84%8B%E1%85%B5%E1%84%83%E1%85%B3%20%E1%84%8C%E1%85%A1%E1%84%89%E1%85%A6%E1%84%92%E1%85%B5%20%E1%84%87%E1%85%A9%E1%84%80%E1%85%B5.png?raw=true" width="100" height="180">|<img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%91%E1%85%A9%E1%84%90%E1%85%A9%E1%84%80%E1%85%A1%E1%84%8B%E1%85%B5%E1%84%83%E1%85%B3%E1%84%8C%E1%85%A5%E1%86%A8%E1%84%8B%E1%85%AD%E1%86%BC%E1%84%92%E1%85%A1%E1%84%80%E1%85%B5.png?raw=true" width="100" height="180"> | <img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%91%E1%85%A9%E1%84%90%E1%85%A9%20%E1%84%80%E1%85%A1%E1%84%8B%E1%85%B5%E1%84%83%E1%85%B3%20%E1%84%86%E1%85%A1%E1%86%AB%E1%84%83%E1%85%B3%E1%86%AF%E1%84%80%E1%85%B5.png?raw=true" width="100" height="180">|<img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%91%E1%85%A9%E1%84%90%E1%85%A9%20%E1%84%80%E1%85%A1%E1%84%8B%E1%85%B5%E1%84%83%E1%85%B3%20%E1%84%86%E1%85%A1%E1%86%AB%E1%84%83%E1%85%B3%E1%86%AF%E1%84%80%E1%85%B5%20(1).png?raw=true" width="100" height="180">|

| | | 
| -------- | -------- |
|<img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%91%E1%85%A9%E1%84%90%E1%85%A9%E1%84%86%E1%85%A2%E1%86%B8%20%E1%84%83%E1%85%AE%E1%86%AF%E1%84%8B%E1%85%A5%E1%84%87%E1%85%A9%E1%84%80%E1%85%B5.png?raw=true" width="100" height="180"> | <img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%91%E1%85%A9%E1%84%90%E1%85%A9%E1%84%89%E1%85%B3%E1%84%91%E1%85%A1%E1%86%BA%20%E1%84%89%E1%85%A1%E1%86%BC%E1%84%89%E1%85%A6%E1%84%87%E1%85%A9%E1%84%80%E1%85%B5.png?raw=true" width="100" height="180">|


## 🗺️ Information
### Features
0. 구글 로그인
1. 마이페이지 조회
2. 사진 촬영하기
3. 앨범 조회하기
4. 포토가이드 둘러보기
5. 포토가이드 적용하기
6. 포토맵 둘러보기
7. 포토가이드 만들기
8. 포토스팟별 포토가이드 조회하기

### Technology Stack
* Tools : Android Studio Dolphin
* Language : Kotlin
* Architecture Pattern : Repository Pattern
* Android Architecture Components(AAC)
* ViewModel
* Naivgation Conponponent
* OKHTTP
* RETROFIT
* SERIALIZATION
* Google Oauth2.0
* KAKAO Map
* OpenCV
* Picasso


## Foldering
```
.
├── base
├── data
│   ├── RemoteDataSource
│   └── RemoteDataSourceImp
├── Repository
│   ├── NetworkModule
│   └── RepositoryModule
├── dto
├── Home
│   ├── camera (for navigation component)
│   ├── gallery (for navigation component)
│   ├── slideshow (for navigation component)
│   ├── HomeActivity
│   └── HomeViewModel
├── PhotoGuide
├── PhotoMap
├── PhotoMaker
├── utils
│   └── PreferenceUtil
├── ApiService
├── ViewModelFactory
└── RetrofitInstance

```

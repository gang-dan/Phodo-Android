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
| | | | | | |

| | | |
| -------- | -------- | -------- |
| | | |


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
* Picaso


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

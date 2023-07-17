# Phodo-Android
<div align=Left>
<img src="https://img.shields.io/badge/Android-3DDC84?style=round-square&logo=Android&logoColor=white"/>
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=round-square&logo=Kotlin&logoColor=white"/>
<img src="https://img.shields.io/badge/JetPack-4285F4?style=round-square&logo=JetPack&logoColor=white"/>
<img src="https://img.shields.io/badge/Retrofit2-000000?style=round-square&logo=Retrofit&logoColor=white"/>
</div>

> 위치별 인생샷 도우미 카메라 앱, 나만의 포토가이드를 만들고 공유할 수 있는 커뮤니티 기반 카메라 서비스

## 📱 ScreenShot
| | | | | | |
| -------- | -------- | -------- | -------- | -------- | -------- |
| <img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%86%E1%85%A1%E1%84%8B%E1%85%B5%E1%84%91%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%8C%E1%85%B5.png?raw=true" width="100" height="180">| <img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%91%E1%85%A9%E1%84%90%E1%85%A9%E1%84%80%E1%85%A1%E1%84%8B%E1%85%B5%E1%84%83%E1%85%B3%20%E1%84%86%E1%85%A9%E1%86%A8%E1%84%85%E1%85%A9%E1%86%A8.png?raw=true" width="100" height="180">| <img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%91%E1%85%A9%E1%84%90%E1%85%A9%E1%84%80%E1%85%A1%E1%84%8B%E1%85%B5%E1%84%83%E1%85%B3%20%E1%84%8C%E1%85%A1%E1%84%89%E1%85%A6%E1%84%92%E1%85%B5%20%E1%84%87%E1%85%A9%E1%84%80%E1%85%B5.png?raw=true" width="100" height="180">|<img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%91%E1%85%A9%E1%84%90%E1%85%A9%E1%84%80%E1%85%A1%E1%84%8B%E1%85%B5%E1%84%83%E1%85%B3%E1%84%8C%E1%85%A5%E1%86%A8%E1%84%8B%E1%85%AD%E1%86%BC%E1%84%92%E1%85%A1%E1%84%80%E1%85%B5.png?raw=true" width="100" height="180"> | <img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%91%E1%85%A9%E1%84%90%E1%85%A9%20%E1%84%80%E1%85%A1%E1%84%8B%E1%85%B5%E1%84%83%E1%85%B3%20%E1%84%86%E1%85%A1%E1%86%AB%E1%84%83%E1%85%B3%E1%86%AF%E1%84%80%E1%85%B5.png?raw=true" width="100" height="180">|<img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%91%E1%85%A9%E1%84%90%E1%85%A9%20%E1%84%80%E1%85%A1%E1%84%8B%E1%85%B5%E1%84%83%E1%85%B3%20%E1%84%86%E1%85%A1%E1%86%AB%E1%84%83%E1%85%B3%E1%86%AF%E1%84%80%E1%85%B5%20(1).png?raw=true" width="100" height="180">|

| | | 
| -------- | -------- |
|<img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%91%E1%85%A9%E1%84%90%E1%85%A9%E1%84%86%E1%85%A2%E1%86%B8%20%E1%84%83%E1%85%AE%E1%86%AF%E1%84%8B%E1%85%A5%E1%84%87%E1%85%A9%E1%84%80%E1%85%B5.png?raw=true" width="100" height="180"> | <img src="https://github.com/gang-dan/Phodo-Android/blob/main/%E1%84%91%E1%85%A9%E1%84%90%E1%85%A9%E1%84%89%E1%85%B3%E1%84%91%E1%85%A1%E1%86%BA%20%E1%84%89%E1%85%A1%E1%86%BC%E1%84%89%E1%85%A6%E1%84%87%E1%85%A9%E1%84%80%E1%85%B5.png?raw=true" width="100" height="180">|


## 🗺️ Information
MVVM 패턴을 적용하기 위해 AAC ViewModel을 활용하는 방식을 시도했으나 구현방법과 개념을 완전히 숙지하지 못하여 제대로 구현하지 못하였습니다.
하지만 시행착오 과정에서 MVVM 패턴과 AAC ViewModel의 관계에 대해 자세히 공부하게 되었고 개인 기술 블로그에 포스팅하였습니다.
[MVC, MVVM 패턴 그리고 ViewModel](https://studyroadmap-kkm.tistory.com/168)
[AAC ViewModel 사용하기](https://studyroadmap-kkm.tistory.com/169)
<br>

또한 그 과정에서 AAC의 Repository를 사용하게 되었습니다. 
현재는 remoteData 밖에 사용하고 있지 않으나 추후 local data 등을 사용하게 될 경우를 대비해 repository를 인터페이스화 하고 Data layer를 캡슐화하도록 수정할 계획입니다.

* 안드로이드 권장 아키텍쳐와 패턴을 사용하려는 시도했으나 시간관계상 개념 공부와 적용을 동시에 하면서 정확히 이해하지 못하고 적용하는 결과를 초래
* 그럼에도 끝까지 파고들어 공부했기 때문에 잘못된 부분을 파악했고 추후 업데이트나 다른 프로젝트를 통해 구현해볼 예정

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

## ToDO
* yolov5 모델 연결하기
* 구글 로그인 jwt 완성하기
* 포토스팟 마커 모듈화 하기
* 아키텍쳐 수정하기

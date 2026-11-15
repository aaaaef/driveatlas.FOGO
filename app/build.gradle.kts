plugins { id("com.android.application") }

android {
    namespace = "com.ssafy.fogo"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.ssafy.fogo"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // 공모전 심사용: 에뮬레이터에서만 시연 기능(도시 예시 재생, 기록 이어받기 등)을 연다. 실차에서는 숨겨진다.
        buildConfigField("boolean", "DEMO", "true")
    }
    buildTypes {
        release {
            // 심사 에뮬레이터에 바로 설치할 수 있도록 디버그 키로 서명한다(스토어 배포용 키 아님).
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    buildFeatures { buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    useLibrary("android.car")
}

dependencies {
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("junit:junit:4.13.2")
    implementation("ai.pleos.playground:Vehicle:2.3.2")
    implementation("ai.pleos.playground:FusedLocation:1.1.3")
    implementation("ai.pleos.playground:fused-location-data:0.0.3")
    implementation("ai.pleos.playground:NaviHelper:3.0.5")
    implementation("ai.pleos.playground:TextToSpeech:3.3.1")
    implementation("com.google.zxing:core:3.5.3")
}

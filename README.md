# FOGO · 팀 Drive Atlas

**내 차가 다닌 곳만 안개가 걷히는 지도. 가족과 달린 곳이 스티커·자석·코스튬이 되고, 차를 바꿔도 지도는 이어집니다.**

FOGO는 Pleos Connect(Android Automotive)용 차량 앱입니다. 주행한 길을 1km 블록으로만 기록해 안개 지도를 걷고, 명소에 처음 도착하면 탑승권과 수집품을 줍니다. 서울·강원·부산 명소 9곳, 지도판, 차고·공방, 월간 티켓북, 친구 지도판, QR 공유, 출퇴근 루틴, 차 생일(누적 1만 km)을 담았습니다.

## 바로 실행하기 (심사용)

| 항목 | 내용 |
| --- | --- |
| APK 위치 | [`apk/DriveAtlas_FOGO_release.apk`](apk/DriveAtlas_FOGO_release.apk) (release 빌드, 에뮬레이터 설치용 디버그 키 서명) |
| 패키지 | `com.ssafy.fogo` · minSdk 28 · targetSdk 34 |
| 테스트 환경 | Pleos Connect v2.1.0 AVD(x86_64, API 34, `Pleos_Connect_API_34`), Windows 11, JDK 21, Gradle 9.4.1, AGP 9.2.1 |
| 차량 신호 | Virtual Console(`http://127.0.0.1:8088`) 기어·속도·와이퍼·문·주행거리, `adb emu geo fix`로 위치 |

```powershell
adb -s emulator-5580 install -r apk/DriveAtlas_FOGO_release.apk
adb -s emulator-5580 shell am start -n com.ssafy.fogo/.MainActivity
adb -s emulator-5580 forward tcp:8088 tcp:8088   # Virtual Console
```

1. 오프닝이 끝나면 `시작하기` → `동의하고 시작`(위치·알림 권한 허용) → `이 친구로 시작`.
2. **빠른 둘러보기**: `아틀라스 → 도시 예시 재생`에서 명소를 고르고 `주행 재생`. 약 6초 뒤 도착 탑승권이 뜹니다.
3. **실제 차량 신호로 주행**: 아래 "에뮬레이터에서 실제 SDK 시연"을 따릅니다. 출발 전 동승석 문을 열고 닫으면 `둘이서`, 뒷문이면 `가족과` 태그가 탑승권에 붙습니다. 비(와이퍼)·밤(전조등·시간) 태그도 차량 신호로 붙습니다.
4. 친구(`친구` 탭), 출퇴근·차 생일(`차고`), 차 바꾸기(`차고 → 기록 이어받기(시연)`)는 에뮬레이터에서 바로 볼 수 있습니다. 시연용 기능은 에뮬레이터에서만 보이고 실차에서는 숨겨집니다.

시연 영상의 흐름은 위 순서와 같습니다.

## 빌드와 설치

Android Studio에서 이 폴더를 열고 내장 JBR과 Android SDK 34를 사용합니다. 공식 Pleos Maven 저장소의 SDK 의존성은 첫 빌드에서 내려받습니다.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat assembleDebug lintDebug
```

APK는 `app/build/outputs/apk/debug/app-debug.apk`에 생성됩니다. Pleos Connect v2.1.0 AVD의 이름이 다른 경우 아래의 AVD 이름을 설치한 이름으로 바꾸세요.

```powershell
$sdk = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
& "$sdk\emulator\emulator.exe" -avd Pleos_Connect_API_34 -port 5580 -gpu host -no-snapshot
```

부팅 후 별도 터미널에서 설치합니다.

```powershell
$sdk = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
& "$sdk\platform-tools\adb.exe" -s emulator-5580 install -r app/build/outputs/apk/debug/app-debug.apk
& "$sdk\platform-tools\adb.exe" -s emulator-5580 shell am start -n com.ssafy.fogo/.MainActivity
```

macOS(Apple Silicon)에서는 SDK Manager에 Mac용 Pleos 이미지 저장소(`connect-avd/2.x/crp-arm64-sys-img.xml`)를 추가해 arm64 이미지를 설치합니다. 빌드·실행 명령은 다음과 같습니다.

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export ANDROID_HOME="$HOME/Library/Android/sdk"
sh ./gradlew assembleDebug lintDebug
"$ANDROID_HOME/emulator/emulator" -avd Pleos_Connect_v2.1.0 -port 5580 -gpu host -no-snapshot -feature -WiFiPacketStream
"$ANDROID_HOME/platform-tools/adb" -s emulator-5580 install -r app/build/outputs/apk/debug/app-debug.apk
```

M1 16GB에서 AVD를 4코어·4GB로 두면 부팅 직후 Pleos 차량 서비스가 응답하지 못해 앱 시작이 ANR로 끝났습니다. 6코어·6GB로 올린 뒤에는 정상 동작했습니다. `scripts/`의 Python 스크립트는 Windows·macOS·Linux의 기본 SDK 위치(또는 `ANDROID_HOME`)에서 adb를 찾습니다.

Virtual Console 페이지는 열리지만 차량 제어가 `503 emulator_inactive`로 실패하면, AVD를 정상 종료한 뒤 같은 실행 명령 끝에 `-feature -WiFiPacketStream`을 붙여 콜드 부팅하세요. 이 PC의 Pleos v2.1.0에서는 기존 네트워크 모드에서 가상 Ethernet과 Console 연결이 복구됐습니다.

## 에뮬레이터에서 실제 SDK 시연

1. ADB로 Virtual Console을 연결합니다. `adb -s emulator-5580 forward tcp:8088 tcp:8088` 실행 후 [Virtual Console](http://127.0.0.1:8088/)을 열어 둡니다. Console은 열린 브라우저 탭이 1초마다 보내는 세션 신호로 활성화되므로, 탭을 닫으면 차량 제어가 `emulator_inactive`로 거부됩니다.
2. 기어 P에서 첫 실행 안내를 마치고 `아틀라스 → 목적지 미리보기 → 위치 기록 시작`을 누른 뒤 Android 위치와 Pleos 위치 권한을 허용합니다. Pleos 내비에 경포대를 설정하고 FOGO로 돌아오면 목적지 카드가 갱신됩니다.
3. `python scripts/replay_location.py GANGNEUNG --point start`로 시작점을 넣습니다. Console에서 브레이크를 밟고 D로 바꾼 뒤 `python scripts/replay_location.py GANGNEUNG`을 실행합니다. FOGO의 주행 화면을 확인한 다음 내비를 앞에 띄워도 블록 기록이 이어집니다.
4. 경로가 끝나면 Virtual Console에서 기어를 P로 바꾸고 FOGO에서 경포대 도착 티켓을 확인합니다. 10초가 지났다면 `python scripts/replay_location.py GANGNEUNG --point arrival`로 최신 위치를 다시 보냅니다.
5. 처음 방문한 경포대에서는 강릉 스티커와 강원 재료가 지급됩니다. Virtual Console에서 청록색 조명을 확인하고, 지도판에서 열린 칸에 스티커를 붙입니다.

## 도시별 예비 재생

1. Pleos 에뮬레이터에 디버그 APK를 설치합니다. 기어를 P에 두고 첫 실행 안내를 마칩니다. `아틀라스 → 도시 예시 재생`을 선택합니다.
2. 강릉 경포대, 서울 경복궁, 부산 해운대해수욕장 등 명소 9곳 중 하나를 고른 뒤 `주행 재생`을 누릅니다. 약 6초 후 도착 티켓이 뜹니다. 재생은 실제 조명을 변경하지 않고 도착 화면에 도시 색을 표시합니다.
3. 도착 탑승권에서 `지도판에 붙이기`를 선택하고, 아이템과 열린 빈 칸을 고른 뒤 `여기 붙이기`를 누릅니다. 같은 시·도의 명소 3곳을 처음 방문하면 자석도 배치할 수 있습니다.
4. `차고 → 공방 가기`에서 코스튬을 제작하고 `입히기`로 장착합니다. `기록 이어받기(시연)`로 차를 바꾸고 이전 탑승권과 티켓북을 확인합니다.
5. 발표 전 반복 연습에는 `시연 설정 → 시연 기록 초기화`를 사용합니다. 이 메뉴는 에뮬레이터용 디버그 APK에서 표시됩니다.

기본 음성은 Pleos 차량 내 한국어 합성입니다. 설정에서 Pleos My Project 자격 증명을 입력하면 해당 앱 프로세스에서 하이브리드 음성을 사용할 수 있습니다. 자격 증명은 APK·파일·앱 설정에 저장하지 않습니다. SDK 재생이 시작되지 않으면 한국어 내장 음원을 재생합니다.

시연 기록은 기기 내부에만 저장합니다. 지도판은 등록 경로 주변의 1km 격자이며, 명소 좌표는 시연용 근삿값입니다. 개발 과정과 검증 결과는 [RECORD.md](RECORD.md)에 있습니다.


## 테스트

- 도착 판정·음성 대체·로컬 데이터 회귀 테스트는 `assembleDebugAndroidTest`로 빌드합니다. 시연 APK와 테스트 APK를 같은 Pleos AVD에 설치하고 `adb -s emulator-5580 shell am instrument --user 10 -w com.ssafy.fogo.test/androidx.test.runner.AndroidJUnitRunner`를 실행합니다. 사용자 ID가 다른 AVD에서는 `adb shell am get-current-user`로 확인한 값을 사용합니다.
- 세 도시 재생, 강원 자석, 공방, 티켓북, 차량 이전 UI는 `python scripts/test_demo.py --reset`으로 검증합니다. 이 명령은 지정한 에뮬레이터의 FOGO 앱 데이터를 초기화합니다.
- 2026-11-15 기준: 계측 테스트 17건 통과, `test_demo.py` 7단계 통과(Pleos Connect v2.1.0 AVD).

## 오픈소스·글꼴 출처

| 이름 | 쓰는 곳 | 라이선스 |
| --- | --- | --- |
| [Galmuri](https://github.com/quiple/galmuri) (quiple) | 한글 픽셀 글꼴 | SIL OFL 1.1 · `app/src/main/assets/licenses/galmuri.txt` |
| [Press Start 2P](https://fonts.google.com/specimen/Press+Start+2P) | 영문 픽셀 글꼴 | SIL OFL 1.1 · `app/src/main/assets/licenses/pressstart2p.txt` |
| [Silkscreen](https://github.com/googlefonts/silkscreen) | 숫자·라벨 | SIL OFL 1.1 · `app/src/main/assets/licenses/silkscreen.txt` |
| [ZXing core 3.5.3](https://github.com/zxing/zxing) | 탑승권·친구 코드 QR 생성 | Apache License 2.0 · `app/src/main/assets/licenses/zxing.txt` |
| Pleos Playground SDK (Vehicle, FusedLocation, NaviHelper, TextToSpeech) | 차량 신호·위치·내비·음성 | Pleos 개발자 약관 |

시연용 도착 음원(`res/raw/arrival_*.wav`)은 Windows 한국어 음성(Heami)으로 생성했고, 오프닝 영상(`res/raw/fogo_intro.mp4`)은 `scripts/intro/render_intro.py`로 직접 렌더했습니다.

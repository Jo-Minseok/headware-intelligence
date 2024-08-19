# ⛑️ 스마트 안전 헬멧 HeadWare-Intelligence (HI)

<div align="center">
    <h3> Project Progress</h3>
    <a href="https://headware-intelligence.notion.site/5d05bd39b6f94036b9247e35d3040202?pvs=4"><img width = "30%" src = "https://img.shields.io/badge/Notion-000000?style=plastic&logo=notion&logoColor=ffffff"/></a><br>
    <a href="https://hits.seeyoufarm.com"><img width="13%" src="https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FJo-Minseok%2Fheadware-intelligence&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false"/></a>                        
</div>

## CONTENTS

- [Team](#Team)
  - [Intro](#Intro)
  - [Composition](#Composition)
- [PROJECT INFORMATION](#PROJECT-INFORMATION)
  - [Mean](#Mean)
  - [Goal](#Goal)
  - [Explain](#Explain)
- [STACKS](#STACKS)
  - [Collaboration](#collaboration)
  - [Tool](#Tool)
  - [Database](#Database)
  - [API](#API)
  - [Language](#Language)
  - [Framework](#Framework)
  - [Deployment](#Deployment)
- [FUNCTION](#FUNCTION)
- [SOURCE](#source)

## TEAM

### Intro

- NAME : <strong><em>Head Metal</em></strong>
- MEAN : 비록 단단한 안전모를 만들 수는 없겠지만, 사고 발생 후에 빠른 대처로 당신의 머리를 단단하게 지키겠습니다. 음악 헤비메탈 장르 이름을 모방하여 짓게 됨.

### Composition

<table align="center">
    <th>역할</th>
    <th>이름</th>
    <th>Github</th>
    <th>담당 파트</th>
    <th>수행 내용</th>
    <tr>
        <td>팀장</td>
        <td>조민석</td>
        <td><a href="https://github.com/Jo-Minseok">@Jo-Minseok</a></td>
        <td>PM, BE, APP, HW</td>
        <td>
        PM
            <ul>
                <li>Git 관리</li>
                <li>Notion Project 관리</li>
                <li>서버 환경 구축</li>
            </ul>
        BE
            <ul>
                <li>로그인, 계정 생성</li>
                <li>작업장 관련 처리</li>
                <li>사고 발생 처리</li>
                <li>FCM Notification</li>
                <li>DB 구축</li>
                <li>행위 웹 소켓 통신</li>
                <li>accident, account SOLID Refactor</li>
                <li>WAS, DB 서버 관리</li>
                <li>버그 수정</li>
            </ul>
        APP
            <ul>
                <li>자동 로그인 기능</li>
                <li>기기 권한 및 접근</li>
                <li>헬멧 등록</li>
                <li>BLE 통신, GPS, FCM 알림</li>
                <li>API 통신</li>
                <li>입력 값 검증</li>
                <li>전체 코드 리팩토링</li>
                <li>서브 기능(작업장 CRUD, 작업자 CRUD)</li>
                <li>버그 수정</li>
            </ul>
        HW
            <ul>
                <li>전체 기능 제작</li>
                <li>시제품 제작</li>
            </ul>
        ETC
            <ul>
                <li>공모전 대회 준비 및 일정 관리</li>
                <li>계정 테스트</li>
                <li>메인 기능 테스트(지도, 헬멧)</li>
            </ul>
        </td>
    </tr>
    <tr>
        <td>팀원</td>
        <td>전진호</td>
        <td><a href="https://github.com/right5625">@right5625</a></td>
        <td>APP, BE</td>
        <td>
        BE
        <ul>
            <li>기상청 API 요청 및 전처리</li>
            <li>사고 처리내역</li>
            <li>사고 발생내역(지도)</li>
            <li>사고 트랜드 머신러닝</li>
            <li>촬영 이미지 호출</li>
            <li>계정 찾기</li>
        </ul>
        APP
        <ul>
            <li>NAVER 지도 사고내역 표기</li>
            <li>API 통신</li>
            <li>웹 소켓 통신</li>
            <li>사고 처리내역</li>
            <li>사고 추세 그래프</li>
            <li>계정 찾기</li>
            <li>메인 화면 기능</li>
            <li>계정 정보 보기</li>
            <li>계정 정보 변경</li>
            <li>전체 코드 리팩토링</li>
            <li>이미지 출력</li>
            <li>서브 기능(작업장 CRUD, 작업자 CRUD)</li>
        </ul>
        ETC
            <ul>
                <li>서브 기능 테스트(작업장 CRUD)</li>
            </ul>
        </td>
    </tr>
    <tr>
        <td>팀원</td>
        <td>채승룡</td>
        <td><a href="https://github.com/chaeseungryong">@chaeseungryong</a></td>
        <td>APP, DOCS</td>
        <td>
        APP
        <ul>
            <li>UI/UX 디자인</li>
            <li>네비게이션 화면 전환</li>
            <li>회원가입 기능</li>
            <li>로그인 기능</li>
            <li>기타 메뉴</li>
        </ul>
        HW
        <ul>
        <li>시제품 제작</li>
        </ul>
        ETC
        <ul>
            <li>문서 작업</li>
            <li>재정 관리</li>
            <li>공모전 발표 자료, 일정 관리</li>
            <li>서브 기능 테스트(작업장 CRUD)</li>
        </ul>
        </td>
    </tr>
</table>

## PROJECT INFORMATION

> 동의대학교 2024학년도 캡스톤디자인(시스템설계) </br>
> PERIOD: [정규 기간] 2024.03.01 - 2024.06.14 (3 Month) + [추가 기간]2024.07.20 - 2024.08.20 (1 Month)</br>

### Mean

📃 머리 'Head' + 하드웨어 'Hardware' + 인공지능 (Artificial Intelligence) = 'HEADWARE INTELLIGENCE'<br>
약어로 HI(Headware Intelligence). 사고가 발생해도 빠른 대응과 치료로 근로자의 목숨을 지켜 가족의 품으로 안전하게 돌아가 인사하라는 의미

### Goal

🥇 연간 발생하는 중대재해로 인한 사망자 수를 감소시키기 위해 안전모에 다양한 기능을 추가하여 애플리케이션과 연동함으로써 부상자에 대한 골든 타임을 확보하는 것. 주 고객층은 도심지의 건설 현장에서 근무하는 근로자

### Explain

📃 안전모와 ESP32-S3-CAM을 결합하여 시제품을 제작, 사고 발생 시 다양한 기능으로 빠른 안전 조치를 취할 수 있도록 안전 관리자가 알 수 있다. 안전 관리자는 안전에 대해 효율적으로 관리할 수 있는 어플리케이션 개발

## STACKS

### Collaboration

<img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">

### Tool

<img src="https://img.shields.io/badge/android%20studio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white"> 
<img src="https://img.shields.io/badge/visual%20studio%20code-007ACC?style=for-the-badge&logo=visualstudiocode&logoColor=white"> 
<img src="https://img.shields.io/badge/dbeaver-382923?style=for-the-badge&logo=dbeaver&logoColor=white"> 
<img src="https://img.shields.io/badge/POSTMAN-FF6C37?style=for-the-badge&logo=postman&logoColor=white"> 
<img src="https://img.shields.io/badge/arduino%20ide-00878F?style=for-the-badge&logo=arduino&logoColor=white"> 
<img src="https://img.shields.io/badge/FIGMA-F24E1E?style=for-the-badge&logo=figma&logoColor=white">

### Database

<img src="https://img.shields.io/badge/maria%20DB-003545?style=for-the-badge&logo=mariadb&logoColor=white">
<img src="https://img.shields.io/badge/sql%20alchemy-D71F00?style=for-the-badge&logo=sqlalchemy&logoColor=white">

### API

<img src="https://img.shields.io/badge/NAVER MAP SDK-03C75A?style=for-the-badge&logo=Naver&logoColor=white"> <img src="https://img.shields.io/badge/기상청%20API-246FDB?style=for-the-badge&logo=Avira&logoColor=white">

### Language

<img src="https://img.shields.io/badge/arduino%20C++-00878F?style=for-the-badge&logo=arduino&logoColor=white"> <img src="https://img.shields.io/badge/python-3776AB?style=for-the-badge&logo=python&logoColor=white"> <img src="https://img.shields.io/badge/kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">

### Framework

<img src="https://img.shields.io/badge/jetpack%20compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"> <img src="https://img.shields.io/badge/fastapi-009688?style=for-the-badge&logo=fastapi&logoColor=white">

### Deployment

<img src="https://img.shields.io/badge/uvicorn-A100FF?style=for-the-badge&logo=gunicorn&logoColor=white"> <img src="https://img.shields.io/badge/raspberry%20pi-A22846?style=for-the-badge&logo=raspberrypi&logoColor=white"><br>

```md
Develop Version : 1.0.0
BackEnd Server : Uvicorn
```

## FUNCTION

<details>
    <summary><strong>💡 HARDWARE</strong></summary>
    <ul>
        <li>충격 센서를 활용하여 사고 감지 및 사고 알림 HTTP 통신을 이용</li>
        <li>BLE 통신을 이용하여 애플리케이션 데이터 통신 및 애플리케이션 제어 (GPS, WIFI, USER ID, WORK ID)</li>
        <li>웹소켓 통신을 이용하여 스피커와 카메라 모듈을 이용하여 안전 관리자가 사고자의 카메라와 스피커를 작동할 수 있도록 웹소켓 통신을 이용 </li>
        <li>조도 센서를 이용하여 주변 빛 양을 감지를 통해 LED 자동으로 제어</li>
        <li>긴급 버튼을 누를 경우 백엔드 서버로 HTTP 통신</li>
        <li>OLED 디스플레이를 이용하여 헬멧 상태 출력</li>
    </ul>
</details>
<details>
    <summary><strong>💡 APP</strong></summary>
    <ul>
        <li>하드웨어에서 전송된 위치정보를 이용하여 사고자의 위치를 지도로 확인</li>
        <li>머신러닝을 활용하여 안전 관심도 추세 확인</li>
        <li>사고 다발 구역 표시</li>
        <li>담당 근로자들의 헬멧의 카메라, 스피커를 제어</li>
        <li>BLE를 이용하여 하드웨어와 데이터 통신</li>
        <li>사고 처리내역 확인 가능</li>
        <li>사고 내역 처리 기능</li>
        <li>사고 발생 시 FCM 알림</li>
    </ul>
</details>
<details>
    <summary><strong>💡 BACKEND</strong></summary>
    <ul>
        <li>사고 감지 데이터 저장, 긴급 호출 및 FCM 신호 알림 전송</li>
        <li>사고 내역 전송</li>
        <li>웹소켓을 이용하여 하드웨어와 애플리케이션 통신 </li>
        <li>계정 관련(로그인, 회원가입, 계정 정보 변경) 통신</li>
        <li>머신러닝 활용하여 추세 데이터 전송</li>
        <li>사고 처리 내역 전송</li>
        <li>카메라 촬영 이미지 저장</li>
    </ul>
</details>

## SOURCE

```
├─ App
│   └─ app
│       └─ src
│           └─ main
│               └─ java
│                   └─ com.headmetal.headwareintelligence
│                       ├─ ApiAction.kt
│                       ├─ ApiService.kt
│                       ├─ BluetoothLeService.kt
│                       ├─ CommonAction.kt
│                       ├─ CommonComposable.kt
│                       ├─ CommonMap.kt
│                       ├─ CompanyInfo.kt
│                       ├─ Countermeasures.kt
│                       ├─ Etc.kt
│                       ├─ FindId.kt
│                       ├─ FindPw.kt
│                       ├─ Helmet.kt
│                       ├─ License.kt
│                       ├─ Loading.kt
│                       ├─ Login.kt
│                       ├─ Main.kt
│                       ├─ MainActivity.kt
│                       ├─ Map.kt
│                       ├─ Menu.kt
│                       ├─ MyFirebaseMessagingService.kt
│                       ├─ NavigationBar.kt
│                       ├─ NullMap.kt
│                       ├─ Privacy.kt
│                       ├─ Processing.kt
│                       ├─ RetrofitInstance.kt
│                       ├─ Signup.kt
│                       ├─ Trend.kt
│                       ├─ Work.kt
│                       └─ WorkList.kt
├─ Arduino
│   ├─ ESP32-S3-WROOM-CAM
│   │   ├─ ESP32-S3-WROOM-CAM.ino
│   │   ├─ camera_pins.h
│   │   └─ module_pins.h
│   └─ Firebeetle-2-ESP32-S3-CAM
│       ├─ Firebeetle-2-ESP32-S3-CAM.ino
│       ├─ camera_pins.h
│       └─ module_pins.h
├─ Backend
│   ├─ accident
│   │   ├─ uploaded_images
│   │   └─ accident_api.py
│   ├─ account
│   │   ├─ account_schema.py
│   │   ├─ company_list.py
│   │   ├─ forgot_crud.py
│   │   ├─ forgot_router.py
│   │   ├─ login_crud.py
│   │   ├─ login_router.py
│   │   ├─ register_crud.py
│   │   ├─ register_router.py
│   │   ├─ register_service.py
│   │   ├─ update_account_crud.py
│   │   ├─ update_account_router_router.py
│   │   └─ update_account_service.py
│   ├─ db
│   │   ├─ db_connection.py
│   │   └─ models.py
│   ├─ fcm_notification
│   │   └─ fcm_function.py
│   ├─ marker
│   │   ├─ accident_marker_crud.py
│   │   ├─ accident_marker_router.py
│   │   └─ accident_marker_schema.py
│   ├─ processing
│   │   ├─ processing_contents_crud.py
│   │   └─ processing_contents_router.py
│   ├─ trend
│   │   ├─ accident_trend_crud.py
│   │   └─ accident_trend_router.py
│   ├─ weather
│   │   ├─ data_preprocessing.py
│   │   ├─ original data.xlsx
|   |   ├─ processing data.xlsx
|   |   ├─ weather_api_config.py
|   |   └─ weather_api.py
│   ├─ work
│   │   ├─ work_crud.py
│   │   └─ work_router.py
│   ├─ common.py
│   ├─ main.py
│   ├─ setup.py
│   └─ test_data_insert.py
├─ Database
│   ├─ TABLECREATE.SQL
│   └─ TABLEDROP.sql
├─ .gitignore
├─ README.md
└─ requirements.txt
```

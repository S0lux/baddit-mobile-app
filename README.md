[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://opensource.org/licenses/)
# Baddit Mobile App

A Jetpack Compose mobile application that is heavily inspired by [Sync Client](https://www.reddit.com/r/redditsync/) for [Reddit](https://reddit.com/).

Made by a group of students for their Mobile Application Introductory course at [Vietnam University of Information Technology](https://www.uit.edu.vn/).


## Authors

- [@S0lux](https://www.github.com/S0lux)
- [@raccooncancode](https://www.github.com/raccooncancode)
- [@Tkhangds](https://www.github.com/Tkhangds)
- [@NTDKhoa04](https://www.github.com/NTDKhoa04)
- [@PhuscBui](https://www.github.com/PhuscBui)


## Acknowledgements

 - [Sync for Reddit](https://www.reddit.com/r/redditsync/) - Overall UI Design inspiration


## Installation

### Prerequisites
* [Android Studio](https://developer.android.com/studio)
* [Baddit Express server](https://github.com/S0lux/baddit-express)
* Git

### Steps

#### 1. Clone the project

```bash
  git clone https://github.com/S0lux/baddit-mobile.git
```

#### 2. Open the project in Android Studio

#### 3. Add keystore file
- Add keystore file to `%PROJECT_ROOT%/app/signing/baddit_key.jks`
- Create a `keystore.properties` file in the same directory

#### 4. Sync Gradle dependencies

#### 5. Modify backend address
- Go to `%PROJECT_ROOT%/app/src/main/java/com/example/baddit/di/`
- Open `AppModule.kt`
- Modify `provideAPI()` to point to your backend's address

#### 6. Build & Run app


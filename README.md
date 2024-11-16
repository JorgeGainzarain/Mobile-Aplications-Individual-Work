# Weather-Mobile-App

## Description
This project is an individual work assignment for a Mobile Applications course. It's an Android application developed using Kotlin and Android Studio.

## Features
- User authentication with Firebase
- Real-time data synchronization using Firebase Realtime Database
- Google Maps integration for location-based services
- Material Design UI components for a modern look and feel
- SQLite database for local data storage
- Lifecycle-aware components for efficient resource management

## Project Structure
- `app/`: Contains the primary source code and resources for the Android application
  - `src/`: Holds the Java/Kotlin source code
    - `main/`: Main application code
    - `androidTest/`: Instrumented tests
    - `test/`: Unit tests
- `build.gradle.kts`: Main Gradle script for project configuration
- `settings.gradle.kts`: Specifies project settings
- `gradlew` and `gradlew.bat`: Gradle wrapper scripts
- `gradle/`: Contains Gradle wrapper files
- `google-services.json`: Configuration for Google services (e.g., Firebase)
- `proguard-rules.pro`: Rules for code optimization and obfuscation

## Dependencies
- AndroidX Core
- Moshi
- AppCompat
- Material Components
- ConstraintLayout
- Navigation
- Activity
- Firebase (Auth, Database, Firestore)
- JUnit and Espresso (for testing)
- Google Maps and Location APIs
- SQLite
- Lifecycle Extensions

## Configuration
- Application ID: "es.usj.individualassessment"
- Compile SDK: 34
- Minimum SDK: 30
- Target SDK: 34
- View Binding and Data Binding: Enabled
- Java Compatibility: 1.8
- JVM Target: 1.8

## Installation
1. Clone the repository: git clone https://github.com/neodr/Mobile-Aplications-Individual-Work.git
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Run the app on an emulator or physical device.

## Usage
1. Launch the application on your Android device or emulator.
2. Sign up for a new account or log in if you already have one.
3. Explore the various features of the app, including map functionality and real-time data updates.
4. Use the navigation components to move between different screens of the app.
5. Check the local SQLite database for offline data access.

## Contributing
This is an individual project for academic purposes. Contributions are not currently accepted.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

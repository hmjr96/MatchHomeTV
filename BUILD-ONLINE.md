# Build MatchHomeTV APK online with GitHub Actions

No Android Studio is required on your PC.

## 1. Create a GitHub repository

1. Sign in to GitHub.
2. Create a new repository, for example `MatchHomeTV`.
3. Extract this ZIP.
4. Upload **all files and folders inside the extracted project root**, including the hidden `.github` folder.
5. Commit the files to the `main` branch.

Important: `build.gradle`, `settings.gradle`, `app/`, and `.github/` must all be at the repository root.

## 2. Build the APK

The workflow starts automatically after the upload. You can also run it manually:

1. Open the repository.
2. Open **Actions**.
3. Choose **Build Android TV APK**.
4. Click **Run workflow**.
5. Open the finished successful run.
6. Under **Artifacts**, download `MatchHomeTV-debug-apk`.
7. Extract that artifact ZIP. Inside it is `app-debug.apk`.

## 3. Install safely on the TV

For the first test, install `app-debug.apk` as a normal app. Launch it and test remote navigation and opening apps before selecting it as the default Home launcher.

If the TV asks which Home app to use, choose **Just once** for the first tests. Only choose **Always** after you are satisfied that navigation and app launching work correctly on your specific TV.

## Build environment

- JDK 17
- Gradle 8.9
- Android API 35
- Android Build Tools 35.0.0
- Android Gradle Plugin 8.7.3

The workflow runs Android Lint first, then creates the debug APK only if the build steps succeed.

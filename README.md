# Match Home TV Launcher

Android TV launcher prototype with:
- Large match-of-the-day hero occupying roughly half of the screen.
- Automatic 8-second match slider with safe local fallback data.
- Two-row TV-friendly grid of installed apps.
- D-pad focus scaling and visible focus border.
- HOME intent support so compatible Android TV devices can offer it as a launcher.
- No external runtime libraries and no network requirement in v1.0.

## Compatibility
- minSdk 23 (Android 6.0+)
- targetSdk 35
- Standard Android TV / AOSP TV launchers that allow third-party HOME apps.
- Some Google TV / manufacturer firmware may prevent changing the default home app without ADB/OEM-specific steps.

## Build
Open the folder in Android Studio, allow SDK 35 to install if prompted, then Build > Build APK(s).

## Important
This v1.0 intentionally uses local demo match slides so a failed web/API response can never break the launcher. Replace the demo MatchSlide data in MainActivity.java with a verified feed only after device testing.

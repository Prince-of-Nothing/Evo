# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Evo Security** is an Android security application with an app icon-style dashboard featuring modular security tools. The app includes link interception capabilities, real-time security news feed, file/URL scanning, and push notifications for security alerts. The UI features a dark theme with icon-based navigation resembling smartphone app icons.

**Package**: `com.evo.security`
**Target SDK**: 36 (Android 14+)
**Min SDK**: 24 (Android 7.0)

## Architecture

### Core Components

1. **MainActivity** (`MainActivity.kt`): Main dashboard activity with app icon-style navigation featuring:
   - HomeScreen with 56x56dp module icons in 4-column grid
   - Navigation to dedicated screens for each security tool
   - Push notification handling for critical security updates
2. **LinkInterceptorActivity** (`LinkInterceptorActivity.kt`): Link interception service that:
   - Intercepts all HTTP/HTTPS links via intent filters
   - Validates URLs using `validateWithService()` method
   - Shows either browser chooser dialog (safe) or blocked link dialog (unsafe)
3. **SecurityRepository** (`repository/SecurityRepository.kt`): Data layer managing:
   - Security news with severity levels (Low, Medium, High, Critical)
   - File/URL scanning simulation with threat detection
   - Sample security alerts with realistic data
4. **NotificationService** (`service/NotificationService.kt`): Push notification system for:
   - Critical security alerts with severity-based prioritization
   - Notification channels and permission handling
5. **UI Components**:
   - **HomeScreen**: App icon-style dashboard with 56x56dp cards and labels below
   - **ModuleIcon**: Individual app icon component with emoji and title
   - **UrlCheckerScreen**: Dedicated screen for file/URL scanning
   - **NewsFeedScreen**: Dedicated screen for security news with cards
   - **BrowserChooserDialog**: Browser selection for safe links
   - **BlockedLinkDialog**: Warning dialog for unsafe links

### Key Technical Details

- **UI Framework**: Jetpack Compose with Material3 design system
- **Architecture Pattern**: App icon-style navigation with dedicated screens for each module
- **State Management**: StateFlow for reactive security news updates
- **Async Operations**: Coroutines with proper Dispatchers for network/file operations
- **Notifications**: Android notification channels with severity-based prioritization
- **Intent Handling**: App registers as handler for all HTTP/HTTPS links via AndroidManifest
- **Browser Detection**: Uses PackageManager to discover installed browsers
- **Data Models**: Structured models for SecurityNews, FileCheckRequest, and results

### Manifest Configuration

The app declares itself as a handler for HTTP/HTTPS links using intent filters:
```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="http" />
    <data android:scheme="https" />
</intent-filter>
```

## Build Commands

### Development
```bash
# Build debug APK
./gradlew assembleDebug

# Install debug APK to connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest
```

### Release
```bash
# Build release APK
./gradlew assembleRelease

# Bundle for Play Store
./gradlew bundleRelease
```

### Cleaning
```bash
# Clean build artifacts
./gradlew clean
```

## Development Notes

### URL Validation Service
The `validateWithService()` method in LinkInterceptorActivity currently returns `true` for all URLs (placeholder implementation). This should be replaced with actual security validation logic.

### Dependencies Management
Dependencies are managed using Gradle Version Catalog (`gradle/libs.versions.toml`). Key dependencies include:
- Jetpack Compose BOM 2024.09.00
- Kotlin 2.0.21
- Android Gradle Plugin 8.13.0

### ViewBinding
ViewBinding is enabled in the module but not currently used since the UI is built with Compose.

### Testing
- Unit tests: `app/src/test/java/com/evo/security/`
- Instrumentation tests: `app/src/androidTest/java/com/evo/security/`
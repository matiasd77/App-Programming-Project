// Polis University Android Application - Main Application class that initializes the app and Hilt dependency injection

package com.polis.university

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Main Application class that serves as the entry point for the Android app
@HiltAndroidApp // Enables Hilt dependency injection for the entire application
class PolisUniversityApp : Application() // Extends Android's Application class to provide app-level functionality

import java.io.FileInputStream
import java.util.Properties

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
val hasKeystoreProperties = keystorePropertiesFile.exists() && run {
    try {
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
        keystoreProperties.containsKey("storeFile") &&
        keystoreProperties.containsKey("storePassword") &&
        keystoreProperties.containsKey("keyAlias") &&
        keystoreProperties.containsKey("keyPassword")
    } catch (e: Exception) {
        false
    }
}

plugins {
    id("com.android.application")
}

android {
    namespace = "ink.wenmo.ime"
    compileSdk = 36

    defaultConfig {
        applicationId = "ink.wenmo.ime"
        minSdk = 26
        targetSdk = 36
        versionCode = 4
        versionName = "0.4.0"
    }

    signingConfigs {
        create("release") {
            if (hasKeystoreProperties) {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    }

    buildTypes {
        release {
            if (hasKeystoreProperties) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                // If keystore is not available, we don't apply the signing config
                // This allows building unsigned release APKs or fallbacks cleanly in CI
                signingConfig = null
            }
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

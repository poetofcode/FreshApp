import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinSerialization)
    // id("com.google.gms.google-services") version "4.4.2" apply false
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation("io.coil-kt:coil-compose:2.6.0")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            api("org.jetbrains.skiko:skiko:0.7.58")
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.client.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            implementation("io.github.kevinnzou:compose-webview-multiplatform:1.9.2")
            // implementation("io.github.rizmaulana:compose-stacked-snackbar:1.0.4")
            implementation("com.github.skydoves:flexible-bottomsheet-material:0.1.3")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation("ch.qos.logback:logback-classic:1.5.2")
        }
    }
}

//repositories {
//    mavenCentral()
//}

android {
    namespace = "com.poetofcode.freshapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.poetofcode.freshapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        register("release") {
            val secretsPropertiesFile = rootProject.file("keystore/secret.properties")

            // println("Curr dir: ${rootProject.buildscript.sourceFile}")

            val secretProperties = Properties()
            if (secretsPropertiesFile.exists()) {
                println("secretsPropertiseFile exists!")
                secretsPropertiesFile.bufferedReader().use { secretProperties.load(it) }

                keyAlias = secretProperties.getProperty("keyAlias")
                keyPassword = secretProperties.getProperty("keyPassword")
                storeFile = file(secretProperties.getProperty("storeFile"))
                storePassword = secretProperties.getProperty("storePassword")
            }
        }
    }

    // apply(plugin = "com.google.gms.google-services")
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            // proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            isDebuggable = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
        implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
        implementation("com.google.firebase:firebase-messaging")
        implementation("com.google.firebase:firebase-analytics")
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.poetofcode.freshapp"
            packageVersion = "1.0.0"
        }

        jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.desktop/java.awt.peer=ALL-UNNAMED") // recommended but not necessary

        if (System.getProperty("os.name").contains("Mac")) {
            jvmArgs("--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
        }
    }
}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hilt.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.gs.app.netsuiteerp"
    compileSdk = 34

    android.buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.gs.app.netsuiteerp"
        minSdk = 31
        targetSdk = 34
        versionCode = 2
        versionName = "0.0.2"

        multiDexEnabled = true

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("../../../sharedLibs/AppCommonLibs/keystore/platform.keystore")
            storePassword = "grandstream"
            keyAlias = "grandstream"
            keyPassword = "grandstream"
        }
    }

    buildTypes {
        // The CONSUMER_KEY and CONSUMER_SECRET of the integration (They only appear when you create the integration)
        val NETSUITE_CONSUMER_KEY = "XXXXXX" // Replace with your consumer key
        val NETSUITE_CONSUMER_SECRET = "XXXXXX" // Replace with your account ID
        // The company's Account ID: Such as "11111_DEMO1"
        val NETSUITE_ACCOUNT_ID = "XXXXXX" // Replace with your account ID
        // The company's Account ID in the URL of NetSuite API: Such as "11111-demo1", "https://11111-demo1.suitetalk.api.netsuite.com
        val NETSUITE_ACCOUNT_ID_IN_URL = "XXXXXX" // Replace with your account ID
        // The callback URI for the login authorization with OAuth 2.0:
        // 1. The authorization sign-in result is returned to the app via this URI.
        // 2. The app needs to add the corresponding intent-filter in the AndroidManifest.xml for the Activity that receives the login authorization result:
        //    Such as, "xxx://xxx/xxx" (grandstream://com.exaple/login/callback)
        val NETSUITE_REDIRECT_URI = "XXXXXX" // Replace with your account ID

        debug {
            buildConfigField("String", "NETSUITE_CONSUMER_KEY", "\"$NETSUITE_CONSUMER_KEY\"")
            buildConfigField("String", "NETSUITE_CONSUMER_SECRET", "\"$NETSUITE_CONSUMER_SECRET\"")
            buildConfigField("String", "NETSUITE_ACCOUNT_ID", "\"$NETSUITE_ACCOUNT_ID\"")
            buildConfigField("String", "NETSUITE_ACCOUNT_ID_IN_URL", "\"$NETSUITE_ACCOUNT_ID_IN_URL\"")
            buildConfigField("String", "NETSUITE_REDIRECT_URI", "\"$NETSUITE_REDIRECT_URI\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            buildConfigField("String", "NETSUITE_CONSUMER_KEY", "\"$NETSUITE_CONSUMER_KEY\"")
            buildConfigField("String", "NETSUITE_CONSUMER_SECRET", "\"$NETSUITE_CONSUMER_SECRET\"")
            buildConfigField("String", "NETSUITE_ACCOUNT_ID", "\"$NETSUITE_ACCOUNT_ID\"")
            buildConfigField("String", "NETSUITE_ACCOUNT_ID_IN_URL", "\"$NETSUITE_ACCOUNT_ID_IN_URL\"")
            buildConfigField("String", "NETSUITE_REDIRECT_URI", "\"$NETSUITE_REDIRECT_URI\"")
        }
    }

//    buildFeatures {
//        dataBinding = true
//        viewBinding = true
//        buildConfig = true
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    afterEvaluate {
        tasks.named("assembleRelease") {
            finalizedBy("copyAndRenameApkTask")
        }
    }
}

val copyAndRenameApkTask by tasks.registering(Copy::class) {
    val config = project.android.defaultConfig
    val versionName = config.versionName
    val createDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    val destDir = File("../output/")
    from("${buildDir}/intermediates/apk/release/app-release.apk")
    into(destDir)
    rename { _ -> "NetsuiteERP_v${versionName}_$createDate.apk" }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.swiperefreshlayout)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

//    implementation(libs.okhttp)
//    implementation(libs.okio)
//    implementation(libs.google.gson)
    implementation(libs.retrofit)
    api(libs.com.squareup.retrofit2.converter.gson2)

    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.android.compiler)

    implementation(project(":mvi"))
    implementation(project(":toast"))
}

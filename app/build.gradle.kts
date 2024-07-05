import java.util.Properties

val adProperties = listOf("AD_ID_FLASHCARDS", "AD_ID_TESTS", "AD_ID_WRITING", "AD_ID_SHARE_CARD", "AD_ID_SHARE_COLLECTION_PDF", "AD_ID_SHARE_COLLECTION", "AD_ID_IMPORT_COLLECTION", "AD_ID_CREATE_BACKUP", "AD_ID_RESTORE_BACKUP")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.kappdev.wordbook"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kappdev.wordbook"
        minSdk = 24
        targetSdk = 34
        versionCode = 11
        versionName = "1.7.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ksp {
            arg("room.schemaLocation", "$projectDir/db_schemas")
            arg("room.generateKotlin", "true")
        }

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        manifestPlaceholders["AD_MOB_APPLICATION_ID"] = properties.getProperty("AD_MOB_APPLICATION_ID")

        adProperties.forEach { property ->
            buildConfigField("String", property, "\"${properties.getProperty(property)}\"")
        }
    }

    bundle {
        language {
            enableSplit = false
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    /* Default */
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    /* Custom */
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    implementation(platform("androidx.compose:compose-bom:2023.10.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.animation:animation-graphics:1.6.2")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha13")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    /* Firebase */
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")

    /* In-app updates */
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    /* Dagger - Hilt */
    val daggerVersion = "2.48"
    implementation ("com.google.dagger:hilt-android:$daggerVersion")
    ksp("com.google.dagger:hilt-android-compiler:$daggerVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    /* Coroutines */
    val coroutinesVersion = "1.7.1"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutinesVersion")

    /* Google - accompanist */
    val accompanistVersion = "0.31.4-beta"
    implementation("com.google.accompanist:accompanist-insets:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-insets-ui:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")

    /* Room */
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    /* Data store */
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    /* Glance app widget */
    implementation("androidx.glance:glance:1.0.0")
    implementation("androidx.glance:glance-appwidget:1.0.0")

    /* Coil image */
    implementation("io.coil-kt:coil-compose:2.4.0")

    /* JSON convertor */
    implementation("com.google.code.gson:gson:2.10.1")

    /* Animation Lottie */
    val lottieVersion = "6.2.0"
    implementation("com.airbnb.android:lottie-compose:$lottieVersion")

    /* Android Image Cropper */
    implementation("com.vanniktech:android-image-cropper:4.5.0")

    /* Google AdMob */
    implementation("com.google.android.gms:play-services-ads:23.0.0")

    /* Default tests */
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
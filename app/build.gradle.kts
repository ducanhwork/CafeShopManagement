
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.group3.application"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.group3.application"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Enable MultiDex
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    
    // Lifecycle components
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)
    annotationProcessor(libs.lifecycle.compiler)
    
    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.gson)
    
    // Dependency Injection
    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)
    
    // Image Loading
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    
    // Security
    implementation(libs.security.crypto)
    
    // Charts
    implementation(libs.mpandroidchart)
    
    // QR Code
    implementation(libs.zxing.core)
    implementation(libs.zxing.embedded)
    
    // Animations
    implementation(libs.lottie)
    
    // Shimmer
    implementation(libs.shimmer)
    
    // Pagination
    implementation(libs.paging.runtime)
    
    // Swipe Refresh
    implementation(libs.swiperefreshlayout)
    
    // Work Manager
    implementation(libs.work.runtime)
    
    // Additional dependencies from main branch
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.14")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    implementation("org.projectlombok:lombok:1.18.38")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation(libs.circleimageview)
    implementation(libs.core.i18n)
    implementation("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

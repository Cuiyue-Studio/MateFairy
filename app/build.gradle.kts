plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "com.example.matefairy01"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.matefairy01"
        minSdk = 35
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk { abiFilters.add("arm64-v8a") }
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    androidResources {
        // 注意：onnx/json 加进白名单是为后续接入 ONNX Runtime + tokenizer 准备
        // 如果被压缩，模型无法 mmap 加载，体积虚胖且推理变慢，且不会报错最难排查
        noCompress += listOf("bundle", "glb", "usdz", "wav", "onnx", "json")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.bom))
    implementation(libs.core)
    implementation(libs.platform)
    implementation(libs.foundation)
    implementation(libs.design)
    implementation(libs.sense)
    implementation(libs.tracking)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.squareup.okhttp)
    // 记忆系统 L2/L3：Room 提供 SQLite ORM + FTS；lifecycle-process 用于 onPause flush 钩子
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.lifecycle.process)
    implementation(project(":editor-asset"))
    testImplementation(libs.junit)
    testImplementation("org.json:json:20231013") // 增加 org.json 的 JVM 依赖以供单元测试使用
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.androidx.ui.tooling.preview)
}

configurations.all {
    resolutionStrategy {
        exclude("androidx.compose.ui", "ui")
        exclude("androidx.compose.ui", "ui-graphics")
        exclude("androidx.compose.ui", "ui-text")
        exclude("androidx.compose.foundation", "foundation")
    }
}

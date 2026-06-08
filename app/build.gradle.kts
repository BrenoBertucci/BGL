plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.bgl"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.bgl"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ---- Chaves de configuração (acessíveis via BuildConfig) ----
        // OBS: a publishable key é pública por design; quem protege os dados é o RLS.
        buildConfigField("String", "SUPABASE_URL", "\"https://gyxsioseuakmetbtlgij.supabase.co\"")
        buildConfigField("String", "SUPABASE_KEY", "\"sb_publishable_oSPkp4BSJj0vS6vrIEwOCg__8awtzK9\"")
        // Cole o seu Client ID da Trakt aqui (https://trakt.tv/oauth/applications):
        buildConfigField("String", "TRAKT_CLIENT_ID", "\"9aaab5652d0accc2c71723a3a623a3e9d6fd09c6652fb2bf489b3812c5b3099e\"")
    }

    buildFeatures {
        buildConfig = true
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // ---- Rede (Supabase + TMDB) ----
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ---- Armazenamento seguro do token ----
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // ---- Lista de resultados ----
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // ---- Carregar imagens (pôsteres do TMDB) ----
    implementation("com.github.bumptech.glide:glide:4.16.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
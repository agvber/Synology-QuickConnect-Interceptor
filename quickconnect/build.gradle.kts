plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.ksp)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {

    implementation("com.squareup.okhttp3:okhttp:3.14.9")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")
    kspTest("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")
//    kspAndroidTest("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")
}
plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.ksp)
    alias(libs.plugins.roomDb)
    alias(libs.plugins.kotlinSerialization)
    application
}

group = "app.jjerrell.choretender.service"

version = "1.0.0"

application {
    mainClass.set("app.jjerrell.choretender.service.ApplicationKt")
    applicationDefaultJvmArgs =
        listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    // Ktor Core
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback)

    // Auth & Sessions
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.sessions)

    // Content Negotiation
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.json)

    // DI
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)

    // DB
    implementation(libs.room.common)
    implementation(libs.room.runtime)
    implementation(libs.room.sqlite.core)
    implementation(libs.room.sqlite.bundled)
    ksp(libs.room.compiler)

    // Testing
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}

room { schemaDirectory("$projectDir/schemas") }

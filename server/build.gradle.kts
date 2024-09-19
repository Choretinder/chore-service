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
    implementation(libs.logback)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.json)

    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)

    implementation(libs.room.common)
    implementation(libs.room.runtime)
    implementation(libs.room.sqlite.core)
    implementation(libs.room.sqlite.bundled)
    ksp(libs.room.compiler)

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}

room { schemaDirectory("$projectDir/schemas") }

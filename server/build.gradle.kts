plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.ksp)
    alias(libs.plugins.roomDb)
    alias(libs.plugins.kotlinSerialization)
    application
    jacoco
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
    implementation(libs.ktor.utils)
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

    // KotlinX
    implementation(libs.kotlinx.datetime)

    // Testing
    testImplementation(libs.ktor.server.tests)
    testImplementation("io.ktor:ktor-client-core:2.3.12")
    testImplementation("io.ktor:ktor-client-cio:2.3.12")
    testImplementation("io.ktor:ktor-client-content-negotiation:2.3.12")
    testImplementation(libs.kotlin.test.junit)
    testImplementation("io.mockk:mockk:1.13.12")
}

room { schemaDirectory("$projectDir/schemas") }

jacoco {
    toolVersion = "0.8.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport") // Generate report after tests run
}

tasks.jacocoTestReport {
    dependsOn("test") // Ensure tests run before generating report
    reports {
        xml.required.set(true)    // Enable XML reports
        csv.required.set(false)
        html.required.set(true)   // Optional: Enable HTML reports
    }
}

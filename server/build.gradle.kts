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
        listOf("-Dio.ktor.development=true") // ${extra["io.ktor.development"] ?: "false"}")
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
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.kotlin.test.coroutines)
    testImplementation(libs.mockk)

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.ktor.client.core)
    testImplementation(libs.ktor.client.content.negotiation)

    testImplementation(libs.koin.test.core)
    testImplementation(libs.koin.test.junit)
}

room { schemaDirectory("$projectDir/schemas") }

jacoco { toolVersion = "0.8.12" }

tasks.withType<Test> {
    finalizedBy("jacocoTestReport") // Generate report after tests run
}

tasks.jacocoTestReport {
    dependsOn("test") // Ensure tests run before generating report
    reports {
        xml.required.set(true) // Enable XML reports
        csv.required.set(false)
        html.required.set(true) // Optional: Enable HTML reports
    }
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) { exclude("**/service/Application*", "**/database/**") }
            }
        )
    )
}

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.ktor).apply(false)
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.roomDb).apply(false)
    alias(libs.plugins.kotlinSerialization).apply(false)
    alias(libs.plugins.spotless)
}

subprojects {
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("${layout.buildDirectory}/**/*.kt")
            ktfmt()
                .kotlinlangStyle()
            licenseHeaderFile(rootProject.file("build-support/copyright.kt"))
            trimTrailingWhitespace()
            endWithNewline()
        }
        kotlinGradle {
            target("**/*.gradle.kts")
            targetExclude("${layout.buildDirectory}/**/*.gradle.kts")
            ktfmt()
                .kotlinlangStyle()
        }
        format("xml") {
            target("**/*.xml")
            targetExclude("**/build/**/*.xml")
            // Look for the first XML tag that isn't a comment (<!--) or the xml declaration (<?xml)
            licenseHeaderFile(rootProject.file("build-support/copyright.xml"), "(<[^!?])")
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}

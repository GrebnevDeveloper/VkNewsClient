import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.vkid.placeholders) apply true
    alias(libs.plugins.ktlint.analisis) apply true
    alias(libs.plugins.detekt.analisis) apply true
}

detekt {
    toolVersion = libs.versions.detektPlugin.toString()
    config.setFrom(files("${project.rootDir}/config/detekt/detekt. yml"))
    buildUponDefaultConfig = true
    parallel = true
}

ktlint {
    version.set(libs.versions.ktlint)
    enableExperimentalRules.set(true)
    verbose.set(true)
    android.set(true)
    filter {
        exclude("**/generated/**")
    }
}

dependencies {
    ktlint(libs.ktlint.rules)
}

vkidManifestPlaceholders {
    val properties = Properties()
    properties.load(file("app/secrets.properties").inputStream())
    val clientId = properties["VKIDClientID"]
    val clientSecret = properties["VKIDClientSecret"]
    init(
        clientId = clientId.toString(),
        clientSecret = clientSecret.toString(),
    )
}
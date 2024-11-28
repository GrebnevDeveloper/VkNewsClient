import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.vkid.placeholders) apply true
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
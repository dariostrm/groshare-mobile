import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "dev.dariostrm.groshare.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "GroShare"
            packageVersion = System.getenv("GITHUB_REF_NAME")
                ?.removePrefix("v")
                ?.takeIf { it.firstOrNull()?.isDigit() == true }
                ?: "1.0.0"

            macOS {
                bundleID = "dev.dariostrm.groshare"
            }

            modules("jdk.unsupported", "java.management")
        }
    }
}
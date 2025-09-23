plugins {
    application
    id("project-report")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "de.mkalb.etpetssim"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jspecify)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

javafx {
    modules = listOf("javafx.controls")
    version = "25"
}

application {
    applicationName = "ExtraterrestrialPetsSimulation"
    mainClass = "de.mkalb.etpetssim.AppLauncher"
    mainModule = "de.mkalb.etpetssim"
    applicationDefaultJvmArgs = listOf(
        "--enable-native-access=ALL-UNNAMED"
    )
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.13.4")
        }
    }
}

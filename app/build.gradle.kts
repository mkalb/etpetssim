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
    implementation("org.jspecify:jspecify:1.0.0")
    testImplementation("org.mockito:mockito-core:5.18.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

javafx {
    modules = listOf("javafx.controls")
    version = "24.0.2"
}

application {
    applicationName = "ExtraterrestrialPetsSimulation"
    mainClass = "de.mkalb.etpetssim.ExtraterrestrialPetsSimulation"
    mainModule = "de.mkalb.etpetssim"
    applicationDefaultJvmArgs = listOf(
        "--enable-native-access=javafx.graphics",
        "--sun-misc-unsafe-memory-access=allow"
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

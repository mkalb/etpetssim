plugins {
    application
    id("project-report")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "de.mkalb.etpetssim"
version = "0.0.1-SNAPSHOT"
val baseName = "ExtraterrestrialPetsSimulation"

base {
    archivesName = baseName
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.jspecify)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

javafx {
    modules = listOf("javafx.controls")
    version = "25.0.1"
}

application {
    applicationName = baseName
    mainClass = "de.mkalb.etpetssim.AppLauncher"
    applicationDefaultJvmArgs = listOf("--enable-native-access=javafx.graphics")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
    // Set JVM args for JavaFX headless testing
    jvmArgs(
        "--enable-native-access=ALL-UNNAMED",
        "-Dprism.order=sw",
        "-Djavafx.headless=true"
    )
}

distributions {
    main {
        distributionBaseName.set(baseName)
        contents {
            from(rootProject.file("README.md"))
            from(rootProject.file("LICENSE"))
            from(rootProject.file("THIRD-PARTY-LICENSES"))
        }
    }
}

tasks.processResources {
    from(
        rootProject.layout.projectDirectory.file("README.md"),
        rootProject.layout.projectDirectory.file("LICENSE"),
        rootProject.layout.projectDirectory.file("THIRD-PARTY-LICENSES")
    )
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "Extraterrestrial Pets Simulation",
            "Implementation-Version" to archiveVersion,
            "Implementation-Vendor" to "Mathias Kalb",
            "Implementation-URL" to "https://github.com/mkalb/etpetssim",
            "Main-Class" to application.mainClass
        )
    }
}

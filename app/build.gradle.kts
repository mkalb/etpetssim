import java.time.Instant

plugins {
    application
    id("project-report")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "de.mkalb.etpetssim"
version = "0.0.1-SNAPSHOT"
val buildTimestamp = Instant.now().toString()
val baseName = "ExtraterrestrialPetsSimulation"

base {
    archivesName = baseName
}

repositories {
    mavenCentral()
}

dependencies {
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
    version = "25"
}

application {
    applicationName = baseName
    mainClass = "de.mkalb.etpetssim.AppLauncher"
    mainModule = "de.mkalb.etpetssim"
    applicationDefaultJvmArgs = listOf(
        "--enable-native-access=javafx.graphics",
        "--enable-native-access=ALL-UNNAMED"
    )
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter("5.13.4")
            targets {
                all {
                    testTask.configure {
                        jvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
                    }
                }
            }
        }
    }
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
            "Main-Class" to application.mainClass,
            "Main-Module" to application.mainModule,
            "Built-By" to System.getProperty("user.name"),
            "Build-Jdk" to System.getProperty("java.version"),
            "Created-By" to "Gradle ${gradle.gradleVersion}",
            "Build-Timestamp" to buildTimestamp
        )
    }
}

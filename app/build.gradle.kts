plugins {
    java
    application
    id("project-report")
}

group = "de.mkalb.etpetssim"
version = "0.0.1-SNAPSHOT"
val baseName = "ExtraterrestrialPetsSimulation"
val applicationModuleName = "de.mkalb.etpetssim"
// JUnit modules that the application module reads when the tests are patched into it.
val junitModuleNames = "org.junit.jupiter.api,org.junit.jupiter.params"

base {
    archivesName = baseName
}

repositories {
    mavenCentral()
}

// JavaFX artifacts are platform-specific. The OpenJFX POMs select the platform through Maven OS profiles,
// which Gradle does not evaluate, so each module is declared explicitly with its classifier and without transitive
// dependencies, which would add empty placeholder jars without classifier.
val javafxPlatform: String = run {
    val osName = providers.systemProperty("os.name").get()
    val osArch = providers.systemProperty("os.arch").get()
    if (osName.startsWith("Windows") && osArch in setOf("amd64", "x86_64")) {
        "win"
    } else {
        throw GradleException("Unsupported platform for JavaFX: os.name='$osName', os.arch='$osArch'. Only Windows x64 is supported.")
    }
}

dependencies {
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.jspecify)

    listOf(libs.javafx.base, libs.javafx.graphics, libs.javafx.controls).forEach { javafxModule ->
        implementation(variantOf(javafxModule) { classifier(javafxPlatform) }) {
            isTransitive = false
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(26)
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

application {
    applicationName = baseName
    mainModule = applicationModuleName
    mainClass = "de.mkalb.etpetssim.AppLauncher"
    applicationDefaultJvmArgs = listOf("--enable-native-access=javafx.graphics")
}

// Tests share packages with the main code, so they are compiled and run patched into the application module.
tasks.compileTestJava {
    val moduleName = applicationModuleName
    val readModules = junitModuleNames
    val testCompileClasspath = sourceSets.test.get().compileClasspath
    val testSourceDirs = sourceSets.test.get().java.sourceDirectories
    options.compilerArgumentProviders.add(CommandLineArgumentProvider {
        listOf(
            "--module-path", testCompileClasspath.asPath,
            "--patch-module", "$moduleName=${testSourceDirs.asPath}",
            "--add-modules", readModules,
            "--add-reads", "$moduleName=$readModules"
        )
    })
}

tasks.withType<Test>().configureEach {
    val moduleName = applicationModuleName
    val readModules = junitModuleNames
    val testRuntimeClasspath = sourceSets.test.get().runtimeClasspath
    // Test resources use a test_ prefix, so they do not shadow main resources inside the patched module.
    val patchedDirs = sourceSets.test.get().output + files(sourceSets.main.get().output.resourcesDir)
    inputs.files(testRuntimeClasspath).withNormalizer(ClasspathNormalizer::class)
    classpath = files()
    jvmArgumentProviders.add(CommandLineArgumentProvider {
        listOf(
            "--module-path", (testRuntimeClasspath - patchedDirs).asPath,
            "--patch-module", "$moduleName=${patchedDirs.asPath}",
            "--add-modules", "ALL-MODULE-PATH",
            "--add-reads", "$moduleName=$readModules"
        )
    })
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform {
        excludeTags("skill")
    }
    // Set JVM args for JavaFX headless testing
    jvmArgs(
        "--enable-native-access=javafx.graphics",
        "-Dprism.order=sw",
        "-Djavafx.headless=true"
    )
}

tasks.register<Test>("skillTest") {
    description = "Runs tests for repository skills."
    group = "verification"

    dependsOn(tasks.testClasses)
    testClassesDirs = sourceSets.test.get().output.classesDirs

    useJUnitPlatform {
        includeTags("skill")
    }

    val i18nConsistencyCheckSource = rootProject.layout.projectDirectory.file(
        ".claude/skills/i18n-consistency/I18nConsistencyCheck.java"
    )
    inputs.file(i18nConsistencyCheckSource)
    systemProperty("i18nConsistencyCheck.source", i18nConsistencyCheckSource.asFile.absolutePath)

    val javaCodeInventorySource = rootProject.layout.projectDirectory.file(
        ".claude/skills/java-code-inventory/JavaCodeInventory.java"
    )
    inputs.file(javaCodeInventorySource)
    systemProperty("javaCodeInventory.source", javaCodeInventorySource.asFile.absolutePath)
}

distributions {
    main {
        distributionBaseName.set(baseName)
        contents {
            from(rootProject.file("README.md"))
            from(rootProject.file("LICENSE"))
            from(rootProject.file("THIRD-PARTY-LICENSES"))
            from(rootProject.file("docs/simulations/")) {
                into("docs/simulations/")
            }
        }
    }
}

tasks.processResources {
    from(
        rootProject.layout.projectDirectory.file("README.md"),
        rootProject.layout.projectDirectory.file("LICENSE"),
        rootProject.layout.projectDirectory.file("THIRD-PARTY-LICENSES"),
        rootProject.layout.projectDirectory.dir("/docs/simulations/")
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
            // Note: Class-Path is intentionally omitted; classpath is set by distribution start scripts
        )
    }
}

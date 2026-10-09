import java.io.ByteArrayOutputStream

plugins {
    java
    application
}

group = "de.mkalb.etpetssim"
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
        languageVersion = JavaLanguageVersion.of(27)
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

// Files that the application reads at runtime (About dialog) and that the distribution ships at its root.
val rootDocumentFiles = files(
    rootProject.file("README.md"),
    rootProject.file("LICENSE"),
    rootProject.file("THIRD-PARTY-LICENSES")
)

distributions {
    main {
        distributionBaseName.set(baseName)
        contents {
            from(rootDocumentFiles)
            // The simulation docs are a manual in the distribution only (not in the JAR).
            from(rootProject.file("docs/simulations")) {
                into("docs/simulations")
            }
            // Images referenced by relative links in README.md and docs/simulations/*.md.
            from(rootProject.file("assets")) {
                into("assets")
            }
        }
    }
}

// Only Windows x64 is supported, so the Unix start script is not needed.
tasks.distZip {
    val unixStartScriptName = baseName
    exclude { it.name == unixStartScriptName }
}

tasks.processResources {
    from(rootDocumentFiles)
}

// The distribution is shipped as ZIP only.
tasks.distTar {
    enabled = false
}

// Runs a Git command and fails with a clear message if Git is unavailable or the command fails.
abstract class GitOutputSource : ValueSource<String, GitOutputSource.Parameters> {
    interface Parameters : ValueSourceParameters {
        val arguments: ListProperty<String>
        val workingDirectory: DirectoryProperty
        val failureHint: Property<String>
    }

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String {
        val description = "git ${parameters.arguments.get().joinToString(" ")}"
        val failure = "Cannot read build information from Git ('$description'). " +
                "Building the JAR or distribution requires Git and a Git working tree."
        val stdout = ByteArrayOutputStream()
        val stderr = ByteArrayOutputStream()
        val exitValue = try {
            execOperations.exec {
                commandLine(listOf("git") + parameters.arguments.get())
                workingDir = parameters.workingDirectory.get().asFile
                standardOutput = stdout
                errorOutput = stderr
                isIgnoreExitValue = true
            }.exitValue
        } catch (e: Exception) {
            throw GradleException("$failure Cause: ${e.cause?.message ?: e.message}", e)
        }
        val output = stdout.toString(Charsets.UTF_8).trim()
        if (exitValue != 0 || output.isEmpty()) {
            val errorText = stderr.toString(Charsets.UTF_8).trim()
            val hint = parameters.failureHint.get()
            throw GradleException(
                "$failure Exit value: $exitValue." +
                        (if (errorText.isEmpty()) "" else " Git error: $errorText") +
                        (if (hint.isEmpty()) "" else " $hint")
            )
        }
        return output
    }
}

// Git values are read lazily, so only the jar task requires Git.
fun gitOutput(vararg arguments: String, failureHint: String = ""): Provider<String> =
    providers.of(GitOutputSource::class) {
        parameters.arguments.set(arguments.toList())
        parameters.workingDirectory.set(rootProject.layout.projectDirectory)
        parameters.failureHint.set(failureHint)
    }

// Project version: derived from the latest reachable release tag 'vMAJOR.MINOR.PATCH[-prerelease]' (SemVer).
// See docs/development/versioning-and-releases.md.
//   Tagged commit:    1.2.3 or 1.2.3-rc.1
//   Untagged commit:  1.2.4-SNAPSHOT (next patch of the tag; the core version for a pre-release tag)
//   Tracked changes:  '-dirty' is appended
fun deriveVersion(describeOutput: String): String {
    val describe = Regex("""^(.+)-(\d+)-g[0-9a-f]+(-dirty)?$""").matchEntire(describeOutput)
        ?: throw GradleException("Unexpected output of 'git describe': '$describeOutput'.")
    val (tag, commitsSinceTag, dirty) = describe.destructured
    val number = """(0|[1-9]\d*)"""
    val identifier = """[0-9A-Za-z-]+"""
    val semVer = Regex("""^v$number\.$number\.$number(-$identifier(?:\.$identifier)*)?$""").matchEntire(tag)
        ?: throw GradleException(
            "Git tag '$tag' is not a valid release tag 'vMAJOR.MINOR.PATCH[-prerelease]'. " +
                    "See docs/development/versioning-and-releases.md."
        )
    val (major, minor, patch, preRelease) = semVer.destructured
    val version = when {
        commitsSinceTag == "0" -> "$major.$minor.$patch$preRelease"
        preRelease.isNotEmpty() -> "$major.$minor.$patch-SNAPSHOT"
        else -> "$major.$minor.${patch.toInt() + 1}-SNAPSHOT"
    }
    return if (dirty.isEmpty()) version else "$version-dirty"
}

val projectVersion: Provider<String> = gitOutput(
    "describe", "--tags", "--match", "v[0-9]*", "--long", "--dirty",
    failureHint = "No release tag 'vMAJOR.MINOR.PATCH' is reachable from HEAD " +
            "(shallow clones may lack tags). See docs/development/versioning-and-releases.md."
).map { deriveVersion(it) }

// The version is applied to the archive tasks (jar, distZip, distTar) instead of project.version, because Gradle reads
// project.version for every dependency resolution. This way only the archive tasks require Git.
tasks.withType<AbstractArchiveTask>().configureEach {
    archiveVersion = projectVersion
}

tasks.jar {
    // Reproducible archive: identical content yields a byte-identical JAR.
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    manifest {
        attributes(
            "Implementation-Title" to "Extraterrestrial Pets Simulation",
            "Implementation-Version" to archiveVersion,
            "Implementation-Vendor" to "Mathias Kalb",
            "Implementation-URL" to "https://github.com/mkalb/etpetssim",
            "Main-Class" to application.mainClass,
            "Created-By" to "Gradle ${gradle.gradleVersion}",
            "Build-Jdk-Spec" to java.toolchain.languageVersion.map { it.toString() },
            "Build-Revision" to gitOutput("rev-parse", "--short", "HEAD"),
            "Build-Commit-Date" to gitOutput("log", "-1", "--format=%cI")
            // Note: Class-Path is intentionally omitted; the distribution start scripts use the module path
        )
    }
}

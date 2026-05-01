package de.mkalb.etpetssim.core;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * File-system storage utilities for application data, logs, and cache files.
 * <p>
 * Directory locations are selected per operating system and created on demand.
 */
public final class AppStorage {

    /**
     * The name of the application, used to create directories for app data, logs, and cache.
     */
    public static final String APP_NAME = "ExtraterrestrialPetsSimulation";

    /**
     * Private constructor to prevent instantiation.
     */
    private AppStorage() {
    }

    /**
     * Creates a directory (if needed) and validates accessibility.
     *
     * @param path directory path to validate
     * @return the validated directory path
     * @throws NullPointerException if {@code path} is {@code null}
     * @throws IOException if creation fails, path is not a directory, or directory is not writable
     */
    private static Path createAndCheckDirectory(Path path) throws IOException {
        Objects.requireNonNull(path, "Path must not be null");
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        if (!Files.isDirectory(path)) {
            throw new IOException("Path " + path + " is not a directory.");
        }
        if (!Files.isWritable(path)) {
            throw new IOException("Path " + path + " is not writable.");
        }
        return path;
    }

    /**
     * Returns the application data directory for the given OS.
     *
     * @param os operating system
     * @return writable data directory path
     * @throws NullPointerException if {@code os} is {@code null}
     * @throws IOException if the directory cannot be created or validated
     */
    private static Path getOrCreateAppDataDir(OperatingSystem os) throws IOException {
        Objects.requireNonNull(os, "Operating system must not be null");
        Path path = switch (os) {
            case WINDOWS -> Paths.get(System.getProperty("user.home"), "AppData", "Local", APP_NAME);
            case MAC -> Paths.get(System.getProperty("user.home"), "Library", "Application Support", APP_NAME);
            case LINUX -> Paths.get(System.getProperty("user.home"), ".config", APP_NAME);
        };
        return createAndCheckDirectory(path);
    }

    /**
     * Returns the log directory for the given OS.
     *
     * @param os operating system
     * @return writable log directory path
     * @throws NullPointerException if {@code os} is {@code null}
     * @throws IOException if the directory cannot be created or validated
     */
    private static Path getOrCreateLogDir(OperatingSystem os) throws IOException {
        Objects.requireNonNull(os, "Operating system must not be null");
        Path path = switch (os) {
            case WINDOWS, LINUX -> getOrCreateAppDataDir(os).resolve("logs");
            case MAC -> Paths.get(System.getProperty("user.home"), "Library", "Logs", APP_NAME);
        };
        return createAndCheckDirectory(path);
    }

    /**
     * Returns the cache directory for the given OS.
     *
     * @param os operating system
     * @return writable cache directory path
     * @throws NullPointerException if {@code os} is {@code null}
     * @throws IOException if the directory cannot be created or validated
     */
    private static Path getOrCreateCacheDir(OperatingSystem os) throws IOException {
        Objects.requireNonNull(os, "Operating system must not be null");
        Path path = switch (os) {
            case WINDOWS -> Paths.get(System.getProperty("user.home"), "AppData", "Local", APP_NAME, "cache");
            case MAC -> Paths.get(System.getProperty("user.home"), "Library", "Caches", APP_NAME);
            case LINUX -> Paths.get(System.getProperty("user.home"), ".cache", APP_NAME);
        };
        return createAndCheckDirectory(path);
    }

    /**
     * Resolves a file name against a directory and enforces that the normalized result stays within that directory.
     */
    private static Path resolveFileInDirectory(Path directory, String fileName) throws IOException {
        Objects.requireNonNull(directory, "Directory must not be null");
        Objects.requireNonNull(fileName, "File name must not be null");

        Path normalizedDirectory = directory.toAbsolutePath().normalize();
        Path resolvedPath = normalizedDirectory.resolve(fileName).toAbsolutePath().normalize();
        if (!resolvedPath.startsWith(normalizedDirectory)) {
            throw new IOException("File name resolves outside of target directory: " + fileName);
        }
        return resolvedPath;
    }

    /**
     * Resolves a file path inside the application data directory.
     *
     * @param fileName file name relative to the data directory
     * @param os operating system
     * @return resolved file path
     * @throws NullPointerException if {@code fileName} or {@code os} is {@code null}
     * @throws IOException if the data directory cannot be created/validated or {@code fileName} escapes that directory
     */
    public static Path getAppDataFile(String fileName, OperatingSystem os) throws IOException {
        Objects.requireNonNull(fileName, "File name must not be null");
        Objects.requireNonNull(os, "Operating system must not be null");
        return resolveFileInDirectory(getOrCreateAppDataDir(os), fileName);
    }

    /**
     * Creates a new file in the application data directory.
     *
     * @param fileName file name relative to the data directory
     * @param os operating system
     * @return path to the created file
     * @throws NullPointerException if {@code fileName} or {@code os} is {@code null}
     * @throws IOException if directory validation fails, file creation fails, or the file already exists
     */
    public static Path createAppDataFile(String fileName, OperatingSystem os) throws IOException {
        Objects.requireNonNull(fileName, "File name must not be null");
        Objects.requireNonNull(os, "Operating system must not be null");
        return Files.createFile(getAppDataFile(fileName, os));
    }

    /**
     * Deletes a file from the application data directory.
     *
     * @param fileName file name relative to the data directory
     * @param os operating system
     * @return {@code true} if a regular file existed and was deleted; otherwise {@code false}
     * @throws NullPointerException if {@code fileName} or {@code os} is {@code null}
     * @throws IOException if directory validation fails or deletion fails
     */
    public static boolean deleteAppDataFile(String fileName, OperatingSystem os) throws IOException {
        Objects.requireNonNull(fileName, "File name must not be null");
        Objects.requireNonNull(os, "Operating system must not be null");
        Path path = getAppDataFile(fileName, os);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            return Files.deleteIfExists(path);
        }
        return false;
    }

    /**
     * Resolves a file path inside the log directory.
     *
     * @param fileName file name relative to the log directory
     * @param os operating system
     * @return resolved log file path
     * @throws NullPointerException if {@code fileName} or {@code os} is {@code null}
     * @throws IOException if the log directory cannot be created/validated or {@code fileName} escapes that directory
     */
    public static Path getLogFile(String fileName, OperatingSystem os) throws IOException {
        Objects.requireNonNull(fileName, "File name must not be null");
        Objects.requireNonNull(os, "Operating system must not be null");
        return resolveFileInDirectory(getOrCreateLogDir(os), fileName);
    }

    /**
     * Creates a temporary file in the cache directory.
     *
     * @param prefix required prefix string
     * @param suffix optional suffix string, may be {@code null}
     * @param os operating system
     * @return path to the created temporary file
     * @throws NullPointerException if {@code prefix} or {@code os} is {@code null}
     * @throws IOException if cache directory validation or temp-file creation fails
     */
    public static Path createTempCacheFile(String prefix, @Nullable String suffix, OperatingSystem os) throws IOException {
        Objects.requireNonNull(prefix, "Prefix must not be null");
        Objects.requireNonNull(os, "Operating system must not be null");
        return Files.createTempFile(getOrCreateCacheDir(os), prefix, suffix);
    }

    /**
     * Operating systems supported by storage path resolution.
     */
    public enum OperatingSystem {
        WINDOWS, MAC, LINUX;

        /**
         * Detects the current operating system from {@code os.name}.
         * <ul>
         *   <li>{@link #WINDOWS} if the lower-case name contains {@code "win"}</li>
         *   <li>{@link #MAC} if the lower-case name contains {@code "mac"}</li>
         *   <li>{@link #LINUX} otherwise</li>
         * </ul>
         *
         * @return detected operating system
         */
        public static OperatingSystem detect() {
            String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
            if (osName.contains("win")) {
                return OperatingSystem.WINDOWS;
            } else if (osName.contains("mac")) {
                return OperatingSystem.MAC;
            } else {
                return OperatingSystem.LINUX;
            }
        }

    }

}

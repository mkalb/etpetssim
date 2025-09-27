package de.mkalb.etpetssim.core;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * AppStorage is a utility class for managing application-specific directories and files.
 * It provides methods to create, access, and delete files in the application data, log, and cache directories.
 * The directories are created based on the operating system and the application name.
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
     * Creates a directory and checks if it is a directory and if it is writable.
     *
     * @param path the path to the directory
     * @return the path to the created directory
     * @throws IOException if an I/O error occurs or if the path is not a directory or not writable
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
     * Gets the application data directory based on the operating system.
     * This method creates the directory if it does not exist and checks if it is writable.
     *
     * @param os the name of the operating system
     * @return the path to the application data directory
     * @throws IOException if the directory cannot be created or is not writable
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
     * Gets the application log directory based on the operating system.
     * This method creates the directory if it does not exist and checks if it is writable.
     *
     * @param os the name of the operating system
     * @return the path to the log directory
     * @throws IOException if the directory cannot be created or is not writable
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
     * Gets the application cache directory based on the operating system.
     * This method creates the directory if it does not exist and checks if it is writable.
     *
     * @param os the name of the operating system
     * @return the path to the cache directory
     * @throws IOException if the directory cannot be created or is not writable
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
     * Gets the application data file path based on the operating system.
     *
     * @param fileName the name of the file to be created or accessed in the app data directory
     * @param os       the operating system
     * @return the path to the application data file
     * @throws IOException if an I/O error occurs or if the path is not a directory or not writable
     */
    public static Path getAppDataFile(String fileName, OperatingSystem os) throws IOException {
        Objects.requireNonNull(fileName, "File name must not be null");
        Objects.requireNonNull(os, "Operating system must not be null");
        return getOrCreateAppDataDir(os).resolve(fileName);
    }

    /**
     * Creates a new application data file with the specified name.
     *
     * @param fileName the name of the file to be created in the app data directory
     * @param os       the operating system
     * @return the path to the newly created application data file
     * @throws IOException if an I/O error occurs or if the path is not a directory or not writable or if the file already exists
     */
    public static Path createAppDataFile(String fileName, OperatingSystem os) throws IOException {
        Objects.requireNonNull(fileName, "File name must not be null");
        Objects.requireNonNull(os, "Operating system must not be null");
        return Files.createFile(getAppDataFile(fileName, os));
    }

    /**
     * Deletes an application data file with the specified name.
     *
     * @param fileName the name of the file to be deleted from the app data directory
     * @param os       the operating system
     * @return true if the file was successfully deleted, false if it did not exist
     * @throws IOException if an I/O error occurs or if the path is not a directory or not writable
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
     * Gets the log file path based on the operating system.
     *
     * @param fileName the name of the log file to be created or accessed in the log directory
     * @param os       the operating system
     * @return the path to the log file
     * @throws IOException if an I/O error occurs or if the path is not a directory or not writable
     */
    public static Path getLogFile(String fileName, OperatingSystem os) throws IOException {
        Objects.requireNonNull(fileName, "File name must not be null");
        Objects.requireNonNull(os, "Operating system must not be null");
        return getOrCreateLogDir(os).resolve(fileName);
    }

    /**
     * Created a new temporary log file at the cache directory with the specified prefix and suffix.
     *
     * @param prefix the prefix for the temporary log file
     * @param suffix the suffix for the temporary log file, can be null
     * @param os     the operating system
     * @return the path to the newly created temporary log file
     * @throws IOException if an I/O error occurs or if the path is not a directory or not writable
     */
    public static Path createTempCacheFile(String prefix, @Nullable String suffix, OperatingSystem os) throws IOException {
        Objects.requireNonNull(prefix, "Prefix must not be null");
        Objects.requireNonNull(os, "Operating system must not be null");
        return Files.createTempFile(getOrCreateCacheDir(os), prefix, suffix);
    }

    /**
     * Enum representing the operating systems supported by the application.
     */
    public enum OperatingSystem {
        WINDOWS, MAC, LINUX;

        /**
         * Detects the operating system based on the system property ("os.name").
         *
         * @return the detected operating system
         */
        public static OperatingSystem detect() {
            String osName = System.getProperty("os.name").toLowerCase();
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

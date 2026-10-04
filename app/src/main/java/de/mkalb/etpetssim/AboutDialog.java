package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.*;
import java.util.regex.*;

/**
 * Displays the application's About dialog.
 * <p>
 * The dialog presents build metadata, the README, the project license, and
 * third-party license information in separate tabs.
 */
public final class AboutDialog {

    private static final int DEFAULT_TEXT_AREA_COLUMNS = 120;
    private static final int DEFAULT_TEXT_AREA_ROWS = 30;
    private static final List<ManifestLine> MANIFEST_LINES = List.of(
            new ManifestLine("Implementation-Title", AppLocalizationKeys.ABOUT_MANIFEST_TITLE),
            new ManifestLine("Implementation-Version", AppLocalizationKeys.ABOUT_MANIFEST_VERSION),
            new ManifestLine("Build-Revision", AppLocalizationKeys.ABOUT_MANIFEST_REVISION),
            new ManifestLine("Build-Commit-Date", AppLocalizationKeys.ABOUT_MANIFEST_COMMIT_DATE),
            new ManifestLine("Build-Jdk-Spec", AppLocalizationKeys.ABOUT_MANIFEST_BUILD_JDK),
            new ManifestLine("Implementation-Vendor", AppLocalizationKeys.ABOUT_MANIFEST_VENDOR),
            new ManifestLine("Implementation-URL", AppLocalizationKeys.ABOUT_MANIFEST_URL));
    private static final Pattern README_FIRST_SECTION_PATTERN = Pattern.compile("^## ", Pattern.MULTILINE);

    private final List<Image> icons;
    private final Font monospacedFont;

    /**
     * Creates a new About dialog helper.
     *
     * @param icons window icons to apply to the dialog stage
     */
    public AboutDialog(List<Image> icons) {
        this.icons = icons;
        monospacedFont = Font.font("Monospaced", 12);
    }

    /**
     * Formats the README for plain-text display.
     * <p>
     * The README starts with an HTML header (logo, title, links, and badges) that is only
     * useful when rendered. The text is therefore shown from the first line starting with
     * {@code "## "}; text without such a line is returned unchanged.
     *
     * @param readme the README text
     * @return the README text from its first second-level heading
     */
    static String formatReadmeText(String readme) {
        Matcher matcher = README_FIRST_SECTION_PATTERN.matcher(readme);
        return matcher.find() ? readme.substring(matcher.start()) : readme;
    }

    /**
     * Shows the About dialog.
     * <p>
     * The dialog contains tabs for version information, the README, the project
     * license, and third-party licenses.
     */
    public void showAboutDialog() {
        Tab tabManifest = createTextAreaTab(
                AppLocalization.getText(AppLocalizationKeys.ABOUT_TAB_VERSION),
                formatManifestSummary());
        Tab tabReadme = createTextAreaTab(
                AppLocalization.getText(AppLocalizationKeys.ABOUT_TAB_README),
                formatReadmeText(getResourceAsString("README.md")));
        Tab tabLicense = createTextAreaTab(
                AppLocalization.getText(AppLocalizationKeys.ABOUT_TAB_LICENSE),
                getResourceAsString("LICENSE"));
        Tab tabThirdParty = createTextAreaTab(
                AppLocalization.getText(AppLocalizationKeys.ABOUT_TAB_THIRD_PARTY_LICENSES),
                getResourceAsString("THIRD-PARTY-LICENSES"));

        TabPane tabPane = new TabPane(tabManifest, tabReadme, tabLicense, tabThirdParty);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        String title = AppLocalization.getText(AppLocalizationKeys.ABOUT_TITLE);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.getDialogPane().setContent(tabPane);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        if (!icons.isEmpty()) {
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(icons);
        }
        alert.showAndWait();
    }

    /**
     * Creates a tab containing a read-only monospaced text area.
     *
     * @param tabTitle title shown on the tab
     * @param text     text displayed inside the tab content area
     * @return configured tab instance
     */
    private Tab createTextAreaTab(String tabTitle, String text) {
        TextArea textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefColumnCount(DEFAULT_TEXT_AREA_COLUMNS);
        textArea.setPrefRowCount(DEFAULT_TEXT_AREA_ROWS);
        textArea.setFont(monospacedFont);

        Tab tab = new Tab(tabTitle);
        tab.setContent(textArea);
        tab.setClosable(false);

        return tab;
    }

    /**
     * Formats a textual summary of selected manifest attributes.
     * <p>
     * Attributes missing from the manifest are shown as a localized placeholder.
     *
     * @return formatted manifest summary text
     */
    private String formatManifestSummary() {
        Map<String, String> mf = readManifestInfo();
        String lineSeparator = System.lineSeparator();
        StringBuilder summary = new StringBuilder();
        for (ManifestLine line : MANIFEST_LINES) {
            String value = mf.getOrDefault(line.attributeName(),
                    AppLocalization.getText(AppLocalizationKeys.ABOUT_MANIFEST_UNKNOWN));
            summary.append(AppLocalization.getFormattedText(line.localizationKey(), value))
                   .append(lineSeparator);
        }
        return summary.toString();
    }

    /**
     * Reads the displayed attributes from {@code META-INF/MANIFEST.MF}.
     *
     * @return map of manifest attribute names to values, or an empty map if unavailable
     */
    private Map<String, String> readManifestInfo() {
        Optional<InputStream> resource = AppResources.getResourceAsStream("META-INF/MANIFEST.MF");
        if (resource.isEmpty()) {
            return Collections.emptyMap();
        }
        try (InputStream is = resource.get()) {
            Manifest manifest = new Manifest(is);
            Attributes attributes = manifest.getMainAttributes();
            Map<String, String> manifestInfo = HashMap.newHashMap(MANIFEST_LINES.size());
            for (ManifestLine line : MANIFEST_LINES) {
                String v = attributes.getValue(line.attributeName());
                if ((v != null) && !v.isBlank()) {
                    manifestInfo.put(line.attributeName(), v);
                }
            }
            return manifestInfo;
        } catch (IOException e) {
            AppLogger.error(e, "AboutDialog: Failed to read MANIFEST.MF");
            return Collections.emptyMap();
        }
    }

    /**
     * Loads a text resource using UTF-8.
     *
     * @param resourceRelativePath resource path relative to the application module root
     * @return resource content, or a fallback message if the resource cannot be read
     */
    private String getResourceAsString(String resourceRelativePath) {
        return AppResources.getResourceAsString(resourceRelativePath, StandardCharsets.UTF_8)
                           .orElse(AppLocalization.getFormattedText(
                                   AppLocalizationKeys.ABOUT_RESOURCE_NOT_FOUND,
                                   resourceRelativePath));
    }

    private record ManifestLine(String attributeName, String localizationKey) {
    }

}

package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.*;
import javafx.application.HostServices;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.*;
import java.util.regex.*;

/**
 * Displays the application's About dialog.
 * <p>
 * The dialog shows the application icon, title, and version in its header, links to the GitHub repository and its
 * issues, and tabs with build metadata, the README, the project license, and third-party license information.
 */
public final class AboutDialog {

    private static final String REPOSITORY_URL = "https://github.com/mkalb/etpetssim";
    private static final String ISSUES_URL = REPOSITORY_URL + "/issues";
    private static final String IMPLEMENTATION_VERSION = "Implementation-Version";
    private static final double HEADER_ICON_SIZE = 64.0d;
    private static final int DEFAULT_TEXT_AREA_COLUMNS = 120;
    private static final int DEFAULT_TEXT_AREA_ROWS = 30;
    private static final int MANIFEST_LINE_CAPACITY_HINT = 64;
    private static final List<ManifestLine> MANIFEST_LINES = List.of(
            new ManifestLine("Implementation-Title", AppLocalizationKeys.ABOUT_MANIFEST_TITLE),
            new ManifestLine(IMPLEMENTATION_VERSION, AppLocalizationKeys.ABOUT_MANIFEST_VERSION),
            new ManifestLine("Build-Revision", AppLocalizationKeys.ABOUT_MANIFEST_REVISION),
            new ManifestLine("Build-Commit-Date", AppLocalizationKeys.ABOUT_MANIFEST_COMMIT_DATE),
            new ManifestLine("Build-Jdk-Spec", AppLocalizationKeys.ABOUT_MANIFEST_BUILD_JDK),
            new ManifestLine("Implementation-Vendor", AppLocalizationKeys.ABOUT_MANIFEST_VENDOR),
            new ManifestLine("Implementation-URL", AppLocalizationKeys.ABOUT_MANIFEST_URL));
    private static final Pattern README_FIRST_SECTION_PATTERN = Pattern.compile("^## ", Pattern.MULTILINE);

    private final List<Image> icons;
    private final HostServices hostServices;

    /**
     * Creates a new About dialog helper.
     *
     * @param icons        window icons to apply to the dialog stage; the largest one is also shown in the header
     * @param hostServices host services of the application, used to open the links in the browser
     */
    public AboutDialog(List<Image> icons, HostServices hostServices) {
        this.icons = icons;
        this.hostServices = hostServices;
    }

    /**
     * Shows the About dialog and waits until it is closed.
     */
    public void show() {
        Map<String, String> manifestInfo = readManifestInfo();

        Tab tabManifest = createTextAreaTab(
                AppLocalization.getText(AppLocalizationKeys.ABOUT_TAB_VERSION),
                formatManifestSummary(manifestInfo));
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

        VBox content = new VBox(createLinkBox(), tabPane);
        content.getStyleClass().add(FXStyleClasses.ABOUT_CONTENT_VBOX);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Dialog<ButtonType> dialog = new Dialog<>();
        FXDialogStyler.applyAppStyle(dialog, icons);
        dialog.setTitle(AppLocalization.getFormattedText(AppLocalizationKeys.WINDOW_TITLE,
                AppLocalization.getText(AppLocalizationKeys.ABOUT_TITLE)));
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setHeader(createHeader(manifestInfo));
        dialogPane.setContent(content);
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);
        dialog.showAndWait();
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
     * Creates the header with the application icon, title, and version.
     *
     * @param manifestInfo manifest attributes read by {@link #readManifestInfo()}
     * @return header node
     */
    private Node createHeader(Map<String, String> manifestInfo) {
        String title = AppLocalization.getText(AppLocalizationKeys.ABOUT_HEADER_TITLE);
        String version = AppLocalization.getFormattedText(AppLocalizationKeys.ABOUT_MANIFEST_VERSION,
                resolveManifestValue(manifestInfo, IMPLEMENTATION_VERSION));
        VBox titleBox = new VBox(
                FXComponentFactory.createLabel(title, FXStyleClasses.ABOUT_TITLE_LABEL),
                FXComponentFactory.createLabel(version, FXStyleClasses.ABOUT_VERSION_LABEL));
        titleBox.getStyleClass().add(FXStyleClasses.ABOUT_HEADER_VBOX);

        HBox header = new HBox();
        header.getStyleClass().add(FXStyleClasses.ABOUT_HEADER_HBOX);
        icons.stream()
             .max(Comparator.comparingDouble(Image::getWidth))
             .ifPresent(icon -> {
                 ImageView iconView = new ImageView(icon);
                 iconView.setFitWidth(HEADER_ICON_SIZE);
                 iconView.setFitHeight(HEADER_ICON_SIZE);
                 iconView.setPreserveRatio(true);
                 iconView.setSmooth(true);
                 header.getChildren().add(iconView);
             });
        header.getChildren().add(titleBox);
        return header;
    }

    private HBox createLinkBox() {
        HBox linkBox = new HBox(
                createLink(AppLocalization.getText(AppLocalizationKeys.ABOUT_LINK_REPOSITORY), REPOSITORY_URL),
                createLink(AppLocalization.getText(AppLocalizationKeys.ABOUT_LINK_ISSUES), ISSUES_URL));
        linkBox.getStyleClass().add(FXStyleClasses.ABOUT_LINK_HBOX);
        return linkBox;
    }

    private Hyperlink createLink(String text, String url) {
        Hyperlink link = new Hyperlink(text);
        link.getStyleClass().add(FXStyleClasses.ABOUT_LINK_HYPERLINK);
        link.setTooltip(new Tooltip(url));
        link.setOnAction(_ -> hostServices.showDocument(url));
        return link;
    }

    /**
     * Creates a tab containing a read-only text area.
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
     * @param manifestInfo manifest attributes read by {@link #readManifestInfo()}
     * @return formatted manifest summary text
     */
    private String formatManifestSummary(Map<String, String> manifestInfo) {
        String lineSeparator = System.lineSeparator();
        StringBuilder summary = new StringBuilder(MANIFEST_LINES.size() * MANIFEST_LINE_CAPACITY_HINT);
        for (ManifestLine line : MANIFEST_LINES) {
            String value = resolveManifestValue(manifestInfo, line.attributeName());
            summary.append(AppLocalization.getFormattedText(line.localizationKey(), value))
                   .append(lineSeparator);
        }
        return summary.toString();
    }

    /**
     * Resolves a manifest attribute value, with a localized placeholder if the attribute is missing.
     *
     * @param manifestInfo  manifest attributes read by {@link #readManifestInfo()}
     * @param attributeName name of the manifest attribute
     * @return attribute value, or the localized "unknown" placeholder
     */
    private String resolveManifestValue(Map<String, String> manifestInfo, String attributeName) {
        return manifestInfo.getOrDefault(attributeName,
                AppLocalization.getText(AppLocalizationKeys.ABOUT_MANIFEST_UNKNOWN));
    }

    /**
     * Reads the displayed attributes from {@code META-INF/MANIFEST.MF}.
     *
     * @return map of manifest attribute names to values, or an empty map if unavailable
     */
    private Map<String, String> readManifestInfo() {
        Optional<InputStream> resource = AppResources.getResourceAsStream("META-INF/MANIFEST.MF");
        if (resource.isEmpty()) {
            return Map.of();
        }
        try (InputStream is = resource.get()) {
            Manifest manifest = new Manifest(is);
            Attributes attributes = manifest.getMainAttributes();
            Map<String, String> manifestInfo = HashMap.newHashMap(MANIFEST_LINES.size());
            for (ManifestLine line : MANIFEST_LINES) {
                String value = attributes.getValue(line.attributeName());
                if ((value != null) && !value.isBlank()) {
                    manifestInfo.put(line.attributeName(), value);
                }
            }
            return manifestInfo;
        } catch (IOException e) {
            AppLogger.error(e, "AboutDialog: Failed to read MANIFEST.MF");
            return Map.of();
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

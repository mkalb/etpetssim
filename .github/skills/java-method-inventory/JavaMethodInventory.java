import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;

public final class JavaMethodInventory {

    private static final Path MAIN_SOURCE_ROOT = Path.of("app", "src", "main", "java");
    private static final Path TEST_SOURCE_ROOT = Path.of("app", "src", "test", "java");
    private static final Path OUTPUT_PATH = Path.of("docs", "planning", "JavaMethodInventory.csv");
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final List<String> CSV_COLUMNS = List.of(
            "source_set",
            "source_path",
            "package_name",
            "declaring_type",
            "declaring_type_kind",
            "member_name",
            "member_kind",
            "parameter_types",
            "visibility",
            "modifiers",
            "return_type",
            "throws_types",
            "annotations",
            "type_parameters",
            "line_number"
    );

    private JavaMethodInventory() {
    }

    public static void main(String[] arguments) {
        try {
            if (arguments.length != 0) {
                throw new UsageException("this generator does not accept arguments");
            }

            Path repositoryRoot = findRepositoryRoot();
            List<MemberDeclaration> declarations = collectDeclarations(repositoryRoot);
            writeInventory(repositoryRoot, declarations);
            System.out.printf(Locale.ROOT, "Generated %s with %d declarations.%n", OUTPUT_PATH, declarations.size());
        } catch (UsageException exception) {
            System.err.println("FAIL usage: " + exception.getMessage());
            System.err.println("Usage: java .github/skills/java-method-inventory/JavaMethodInventory.java");
            System.exit(3);
        } catch (ParseException exception) {
            exception.diagnostics().forEach(JavaMethodInventory::printDiagnostic);
            System.exit(2);
        } catch (IOException exception) {
            System.err.println("FAIL io: " + exception.getMessage());
            System.exit(4);
        } catch (RuntimeException exception) {
            System.err.println("FAIL unexpected: " + exception.getMessage());
            System.exit(4);
        }
    }

    private static Path findRepositoryRoot() throws IOException {
        Path current = Path.of("").toAbsolutePath().normalize();
        while (current != null) {
            if (Files.isDirectory(current.resolve(MAIN_SOURCE_ROOT)) && Files.isDirectory(current.resolve(TEST_SOURCE_ROOT))) {
                return current;
            }
            current = current.getParent();
        }
        throw new IOException("could not locate repository root containing " + MAIN_SOURCE_ROOT + " and " + TEST_SOURCE_ROOT);
    }

    private static List<MemberDeclaration> collectDeclarations(Path repositoryRoot) throws IOException, ParseException {
        List<SourceFile> sourceFiles = collectSourceFiles(repositoryRoot);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IOException("no system Java compiler is available; run this skill with a JDK");
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        List<CompilationUnitTree> compilationUnits;
        Trees trees;
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.ROOT, StandardCharsets.UTF_8)) {
            Iterable<? extends JavaFileObject> javaFiles = fileManager.getJavaFileObjectsFromPaths(
                    sourceFiles.stream().map(SourceFile::path).toList()
            );
            JavacTask task = (JavacTask) compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    List.of("-proc:none"),
                    null,
                    javaFiles
            );
            compilationUnits = new ArrayList<>();
            for (CompilationUnitTree compilationUnit : task.parse()) {
                compilationUnits.add(compilationUnit);
            }
            trees = Trees.instance(task);
        }

        List<Diagnostic<? extends JavaFileObject>> errors = diagnostics.getDiagnostics().stream()
                                                                       .filter(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.ERROR)
                                                                       .toList();
        if (!errors.isEmpty()) {
            throw new ParseException(errors);
        }

        SourcePositions sourcePositions = trees.getSourcePositions();
        List<MemberDeclaration> declarations = new ArrayList<>();
        for (CompilationUnitTree compilationUnit : compilationUnits) {
            Path sourcePath = Path.of(compilationUnit.getSourceFile().toUri()).toAbsolutePath().normalize();
            SourceFile sourceFile = sourceFiles.stream()
                                               .filter(candidate -> Objects.equals(candidate.path(), sourcePath))
                                               .findFirst()
                                               .orElseThrow(() -> new IOException("parsed unexpected source file " + sourcePath));
            String source = Files.readString(sourcePath, StandardCharsets.UTF_8);
            String packageName = compilationUnit.getPackageName() == null ? "" : compilationUnit.getPackageName().toString();
            for (Tree typeDeclaration : compilationUnit.getTypeDecls()) {
                if (typeDeclaration instanceof ClassTree classTree) {
                    collectTypeDeclarations(
                            classTree,
                            compilationUnit,
                            sourcePositions,
                            source,
                            sourceFile,
                            packageName,
                            "",
                            declarations
                    );
                }
            }
        }
        declarations.sort(Comparator.comparing(MemberDeclaration::sourceSet)
                                    .thenComparing(MemberDeclaration::sourcePath)
                                    .thenComparingInt(MemberDeclaration::lineNumber));
        return List.copyOf(declarations);
    }

    private static List<SourceFile> collectSourceFiles(Path repositoryRoot) throws IOException {
        List<SourceFile> sourceFiles = new ArrayList<>();
        collectSourceFiles(repositoryRoot.resolve(MAIN_SOURCE_ROOT), "main", repositoryRoot, sourceFiles);
        collectSourceFiles(repositoryRoot.resolve(TEST_SOURCE_ROOT), "test", repositoryRoot, sourceFiles);
        return List.copyOf(sourceFiles);
    }

    private static void collectSourceFiles(
            Path sourceRoot,
            String sourceSet,
            Path repositoryRoot,
            List<SourceFile> sourceFiles
    ) throws IOException {
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.getFileName().toString().endsWith(".java"))
                 .sorted()
                 .map(path -> path.toAbsolutePath().normalize())
                 .map(path -> new SourceFile(sourceSet, path, toRepositoryPath(repositoryRoot, path)))
                 .forEach(sourceFiles::add);
        }
    }

    private static void collectTypeDeclarations(
            ClassTree classTree,
            CompilationUnitTree compilationUnit,
            SourcePositions sourcePositions,
            String source,
            SourceFile sourceFile,
            String packageName,
            String enclosingType,
            List<MemberDeclaration> declarations
    ) {
        String simpleName = classTree.getSimpleName().toString();
        if (simpleName.isEmpty()) {
            return;
        }
        String declaringType = enclosingType.isEmpty() ? simpleName : enclosingType + "." + simpleName;
        for (Tree member : classTree.getMembers()) {
            switch (member) {
                case MethodTree methodTree -> declarations.add(toMemberDeclaration(
                        methodTree,
                        classTree,
                        compilationUnit,
                        sourcePositions,
                        source,
                        sourceFile,
                        packageName,
                        declaringType
                ));
                case ClassTree nestedType -> collectTypeDeclarations(
                        nestedType,
                        compilationUnit,
                        sourcePositions,
                        source,
                        sourceFile,
                        packageName,
                        declaringType,
                        declarations
                );
                default -> {
                }
            }
        }
    }

    private static MemberDeclaration toMemberDeclaration(
            MethodTree methodTree,
            ClassTree declaringClass,
            CompilationUnitTree compilationUnit,
            SourcePositions sourcePositions,
            String source,
            SourceFile sourceFile,
            String packageName,
            String declaringType
    ) {
        boolean constructor = methodTree.getReturnType() == null;
        boolean compactConstructor = constructor
                && isCompactConstructor(methodTree, declaringClass, compilationUnit, sourcePositions, source);
        MemberKind memberKind = constructor
                ? (compactConstructor ? MemberKind.COMPACT_CONSTRUCTOR : MemberKind.CONSTRUCTOR)
                : MemberKind.METHOD;
        long startPosition = sourcePositions.getStartPosition(compilationUnit, methodTree);
        int lineNumber = Math.toIntExact(compilationUnit.getLineMap().getLineNumber(startPosition));
        return new MemberDeclaration(
                sourceFile.sourceSet(),
                sourceFile.repositoryPath(),
                packageName,
                declaringType,
                declaringClass.getKind().name(),
                constructor ? "<init>" : methodTree.getName().toString(),
                memberKind.name(),
                compactConstructor ? "" : parameterTypes(methodTree, compilationUnit, sourcePositions, source),
                visibility(methodTree, declaringClass, constructor),
                modifiers(methodTree),
                constructor ? "" : methodTree.getReturnType().toString(),
                methodTree.getThrows().stream().map(Tree::toString).collect(java.util.stream.Collectors.joining(", ")),
                methodTree.getModifiers().getAnnotations().stream()
                          .map(AnnotationTree::toString)
                          .collect(java.util.stream.Collectors.joining(" | ")),
                methodTree.getTypeParameters().stream()
                          .map(TypeParameterTree::toString)
                          .collect(java.util.stream.Collectors.joining(", ")),
                lineNumber
        );
    }

    private static boolean isCompactConstructor(
            MethodTree methodTree,
            ClassTree declaringClass,
            CompilationUnitTree compilationUnit,
            SourcePositions sourcePositions,
            String source
    ) {
        if (declaringClass.getKind() != Tree.Kind.RECORD) {
            return false;
        }
        long methodStart = sourcePositions.getStartPosition(compilationUnit, methodTree);
        long bodyStart = sourcePositions.getStartPosition(compilationUnit, methodTree.getBody());
        if ((methodStart < 0) || (bodyStart < methodStart)) {
            return false;
        }
        String header = source.substring(Math.toIntExact(methodStart), Math.toIntExact(bodyStart));
        return !hasConstructorParameterList(header, declaringClass.getSimpleName().toString());
    }

    private static boolean hasConstructorParameterList(String header, String constructorName) {
        for (int position = 0; position < header.length(); ) {
            SourceToken token = nextSourceToken(header, position);
            if (token == null) {
                return false;
            }
            if (token.text().equals(constructorName)) {
                SourceToken followingToken = nextSourceToken(header, token.endPosition());
                if ((followingToken != null) && followingToken.text().equals("(")) {
                    return true;
                }
            }
            position = token.endPosition();
        }
        return false;
    }

    private static String parameterTypes(
            MethodTree methodTree,
            CompilationUnitTree compilationUnit,
            SourcePositions sourcePositions,
            String source
    ) {
        return methodTree.getParameters().stream()
                         .map(parameter -> parameterType(parameter, compilationUnit, sourcePositions, source))
                         .collect(java.util.stream.Collectors.joining(", "));
    }

    private static String parameterType(
            VariableTree parameter,
            CompilationUnitTree compilationUnit,
            SourcePositions sourcePositions,
            String source
    ) {
        String type = parameter.getType().toString();
        long typeStart = sourcePositions.getStartPosition(compilationUnit, parameter.getType());
        long typeEnd = sourcePositions.getEndPosition(compilationUnit, parameter.getType());
        if ((typeStart < 0) || (typeEnd < typeStart)) {
            return type;
        }
        boolean varargs = hasSourceToken(
                source,
                Math.toIntExact(typeStart),
                Math.toIntExact(typeEnd),
                "..."
        );
        SourceToken followingToken = nextSourceToken(source, Math.toIntExact(typeEnd));
        return (varargs || ((followingToken != null) && followingToken.text().equals("..."))) && type.endsWith("[]")
                ? type.substring(0, type.length() - 2) + "..."
                : type;
    }

    private static boolean hasSourceToken(String source, int startPosition, int endPosition, String expectedToken) {
        for (int position = startPosition; position < endPosition; ) {
            SourceToken token = nextSourceToken(source, position);
            if ((token == null) || (token.endPosition() > endPosition)) {
                return false;
            }
            if (token.text().equals(expectedToken)) {
                return true;
            }
            position = token.endPosition();
        }
        return false;
    }

    private static String visibility(MethodTree methodTree, ClassTree declaringClass, boolean constructor) {
        Set<Modifier> flags = methodTree.getModifiers().getFlags();
        if (flags.contains(Modifier.PUBLIC)) {
            return "public";
        }
        if (flags.contains(Modifier.PROTECTED)) {
            return "protected";
        }
        if (flags.contains(Modifier.PRIVATE)) {
            return "private";
        }
        if (constructor && (declaringClass.getKind() == Tree.Kind.ENUM)) {
            return "private";
        }
        if (constructor && isCanonicalRecordConstructor(methodTree, declaringClass)) {
            return visibility(declaringClass.getModifiers().getFlags());
        }
        if ((declaringClass.getKind() == Tree.Kind.INTERFACE)
                || (declaringClass.getKind() == Tree.Kind.ANNOTATION_TYPE)) {
            return "public";
        }
        return "package-private";
    }

    private static boolean isCanonicalRecordConstructor(MethodTree methodTree, ClassTree declaringClass) {
        if (declaringClass.getKind() != Tree.Kind.RECORD) {
            return false;
        }
        if (methodTree.getBody().getStatements().isEmpty()) {
            return true;
        }
        Tree firstStatement = methodTree.getBody().getStatements().getFirst();
        return !(firstStatement instanceof ExpressionStatementTree expressionStatement
                && expressionStatement.getExpression() instanceof MethodInvocationTree invocation
                && invocation.getMethodSelect() instanceof IdentifierTree identifier
                && identifier.getName().contentEquals("this"));
    }

    private static String visibility(Set<Modifier> flags) {
        if (flags.contains(Modifier.PUBLIC)) {
            return "public";
        }
        if (flags.contains(Modifier.PROTECTED)) {
            return "protected";
        }
        if (flags.contains(Modifier.PRIVATE)) {
            return "private";
        }
        return "package-private";
    }

    private static SourceToken nextSourceToken(String source, int startPosition) {
        int position = startPosition;
        while (position < source.length()) {
            char character = source.charAt(position);
            if (Character.isWhitespace(character)) {
                position++;
            } else if (source.startsWith("//", position)) {
                position = skipLineComment(source, position + 2);
            } else if (source.startsWith("/*", position)) {
                position = skipBlockComment(source, position + 2);
            } else if (source.startsWith("\"\"\"", position)) {
                position = skipTextBlock(source, position + 3);
            } else if (character == '"') {
                position = skipQuotedLiteral(source, position + 1, '"');
            } else if (character == '\'') {
                position = skipQuotedLiteral(source, position + 1, '\'');
            } else {
                break;
            }
        }
        if (position >= source.length()) {
            return null;
        }
        if (Character.isJavaIdentifierStart(source.charAt(position))) {
            int endPosition = position + 1;
            while ((endPosition < source.length()) && Character.isJavaIdentifierPart(source.charAt(endPosition))) {
                endPosition++;
            }
            return new SourceToken(source.substring(position, endPosition), endPosition);
        }
        if (source.startsWith("...", position)) {
            return new SourceToken("...", position + 3);
        }
        return new SourceToken(Character.toString(source.charAt(position)), position + 1);
    }

    private static int skipLineComment(String source, int position) {
        int lineEnd = source.indexOf('\n', position);
        return lineEnd < 0 ? source.length() : lineEnd + 1;
    }

    private static int skipBlockComment(String source, int position) {
        int commentEnd = source.indexOf("*/", position);
        return commentEnd < 0 ? source.length() : commentEnd + 2;
    }

    private static int skipTextBlock(String source, int position) {
        while (position < source.length()) {
            if (source.startsWith("\"\"\"", position)) {
                return position + 3;
            }
            if (source.charAt(position) == '\\') {
                position++;
            }
            position++;
        }
        return source.length();
    }

    private static int skipQuotedLiteral(String source, int position, char delimiter) {
        while (position < source.length()) {
            char character = source.charAt(position++);
            if (character == '\\') {
                position++;
            } else if (character == delimiter) {
                break;
            }
        }
        return Math.min(position, source.length());
    }

    private static String modifiers(MethodTree methodTree) {
        Set<Modifier> flags = methodTree.getModifiers().getFlags();
        return Stream.of(Modifier.values())
                     .filter(flags::contains)
                     .filter(modifier -> (modifier != Modifier.PUBLIC)
                             && (modifier != Modifier.PROTECTED)
                             && (modifier != Modifier.PRIVATE))
                     .map(modifier -> modifier.toString().toLowerCase(Locale.ROOT))
                     .collect(java.util.stream.Collectors.joining(" "));
    }

    private static void writeInventory(Path repositoryRoot, List<MemberDeclaration> declarations) throws IOException {
        Path outputPath = repositoryRoot.resolve(OUTPUT_PATH);
        Files.createDirectories(outputPath.getParent());
        List<String> lines = new ArrayList<>(declarations.size() + 1);
        lines.add(String.join(",", CSV_COLUMNS));
        declarations.stream().map(MemberDeclaration::toCsvLine).forEach(lines::add);
        Files.writeString(outputPath, String.join(LINE_SEPARATOR, lines) + LINE_SEPARATOR, StandardCharsets.UTF_8);
    }

    private static String toRepositoryPath(Path repositoryRoot, Path path) {
        return repositoryRoot.relativize(path).toString().replace(path.getFileSystem().getSeparator(), "/");
    }

    private static void printDiagnostic(Diagnostic<? extends JavaFileObject> diagnostic) {
        String sourceName = diagnostic.getSource() == null ? "<unknown>" : diagnostic.getSource().getName();
        System.err.printf(
                Locale.ROOT,
                "FAIL parse: %s:%d:%d: %s%n",
                sourceName,
                diagnostic.getLineNumber(),
                diagnostic.getColumnNumber(),
                diagnostic.getMessage(Locale.ROOT)
        );
    }

    private static String csvField(String value) {
        return '"' + value.replace("\"", "\"\"") + '"';
    }

    private enum MemberKind {
        METHOD,
        CONSTRUCTOR,
        COMPACT_CONSTRUCTOR
    }

    private record SourceFile(String sourceSet, Path path, String repositoryPath) {
    }

    private record SourceToken(String text, int endPosition) {
    }

    private record MemberDeclaration(
            String sourceSet,
            String sourcePath,
            String packageName,
            String declaringType,
            String declaringTypeKind,
            String memberName,
            String memberKind,
            String parameterTypes,
            String visibility,
            String modifiers,
            String returnType,
            String throwsTypes,
            String annotations,
            String typeParameters,
            int lineNumber
    ) {

        private String toCsvLine() {
            return List.of(
                    sourceSet,
                    sourcePath,
                    packageName,
                    declaringType,
                    declaringTypeKind,
                    memberName,
                    memberKind,
                    parameterTypes,
                    visibility,
                    modifiers,
                    returnType,
                    throwsTypes,
                    annotations,
                    typeParameters,
                    Integer.toString(lineNumber)
            ).stream().map(JavaMethodInventory::csvField).collect(java.util.stream.Collectors.joining(","));
        }

    }

    private static final class ParseException extends Exception {

        private final List<Diagnostic<? extends JavaFileObject>> diagnostics;

        private ParseException(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
            this.diagnostics = List.copyOf(diagnostics);
        }

        private List<Diagnostic<? extends JavaFileObject>> diagnostics() {
            return diagnostics;
        }

    }

    private static final class UsageException extends RuntimeException {

        private UsageException(String message) {
            super(message);
        }

    }

}

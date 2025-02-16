package ch.epfl.cs107;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class Submit {

    private static final Path PROJECT_ROOT = Path.of(System.getProperty("user.dir"));
    private static final Path ICCOOP_PROJECT = PROJECT_ROOT.resolve("iccoop");
    private static final Path TARGET_FOLDER = ICCOOP_PROJECT.resolve("target");
    private static final Path SUBMISSION_FILE = TARGET_FOLDER.resolve("submission.zip");

    private static final Path ICCOOP_RESOURCE_FOLDER =
            ICCOOP_PROJECT.resolve("src")
                    .resolve("main")
                    .resolve("resources");

    /** ??? */
    private static final Set<Path> DIRECTORIES_TO_IGNORE = Set.of(
            Path.of("out"), // output folder in Eclipse
            TARGET_FOLDER,
            Path.of(".idea"),
            Path.of("src", ".idea"),
            ICCOOP_RESOURCE_FOLDER.resolve("fonts")
    );
    // Les fichiers dont le nom commence par l'un de ces préfixes sont inclus dans le rendu.
    private static final Set<String> PREFIXES_TO_SUBMIT = Set.of("readme", "conception");
    // Les fichiers dont le nom se termine par l'un de ces suffixes sont inclus dans le rendu.
    private static final Set<String> SUFFIXES_TO_SUBMIT = Set.of(
            ".java", ".png", ".ttf",
            ".wav", ".xml", ".txt", ".md", ".pdf"
            );

    // ============================================================================================
    // ========================================= MAIN =============================================
    // ============================================================================================

    public static void main(String[] args) {
        try {
            var root = ICCOOP_PROJECT.resolve("src").resolve("main");
            var paths = filesToSubmit(root, path -> {
                var fileName = path.getFileName().toString().toLowerCase();
                return DIRECTORIES_TO_IGNORE.stream().noneMatch(path::startsWith)
                        && !fileName.equals("submit.java")
                        && (PREFIXES_TO_SUBMIT.stream().anyMatch(fileName::startsWith)
                        || SUFFIXES_TO_SUBMIT.stream().anyMatch(fileName::endsWith));
            });

            var zipArchive = createZipArchive(ICCOOP_PROJECT.resolve("src"), paths);
            Files.write(SUBMISSION_FILE, zipArchive);
        } catch (IOException e) {
            System.err.println("Erreur inattendue !");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static List<Path> filesToSubmit(Path root, Predicate<Path> keepFile) throws IOException {
        try (var paths = Files.walk(root)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(keepFile)
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
        }
    }

    private static byte[] createZipArchive(Path root, List<Path> paths) throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        try (var zipStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (var path : paths) {
                var relative = root.relativize(path);
                var entryPath = IntStream.range(0, relative.getNameCount())
                        .mapToObj(relative::getName)
                        .map(Path::toString)
                        .collect(Collectors.joining(File.separator, "", ""));
                zipStream.putNextEntry(new ZipEntry(entryPath));
                try (var fileStream = new FileInputStream(path.toFile())) {
                    fileStream.transferTo(zipStream);
                }
                zipStream.closeEntry();
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

}
package file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    public static void writeToFileNOSQL(String text) throws IOException {
        List<String> lines = Arrays.asList(text);
        Path file = Paths.get("result.txt");
        Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }
}

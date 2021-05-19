package de.rexlmanu.bingo.utility;

import java.io.File;
import java.nio.file.Files;
import java.util.Objects;

public class FileUtils {

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (Objects.nonNull(allContents)) {
            for (File file : allContents) {
                if (Files.isSymbolicLink(file.toPath())) continue;
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

}

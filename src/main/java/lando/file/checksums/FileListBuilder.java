package lando.file.checksums;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


final class FileListBuilder {
    
    private static final List<String> EXCLUDE_MACOS_FILES = Arrays.asList(
            ".fseventsd",
            ".Spotlight-V100",
            ".Trashes",
            "._.Trashes",
            ".DS_Store",
            ".TemporaryItems",
            ".DocumentRevisions-V100",
            ".VolumeIcon.icns",
            "Backups.backupdb");

    List<FileToRead> findAllFilesToRead(Path dir) throws Exception {
        
        return Files.walk(dir)
            .filter(f -> ! EXCLUDE_MACOS_FILES.contains(f.getFileName().toString()))
            .filter(f -> Files.isRegularFile(f) && ! Files.isSymbolicLink(f))
            .map(f -> {
                try {
                    return new FileToRead(f, Files.size(f));
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());
    }
}

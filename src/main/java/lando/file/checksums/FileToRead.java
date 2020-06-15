package lando.file.checksums;

import java.nio.file.Path;
import java.util.Objects;

final class FileToRead {

    private final Path file;
    private final long size;
    
    FileToRead(Path file, long size) {
        this.file = Objects.requireNonNull(file);
        this.size = size;
    }
    
    Path getFile() {
        return file;
    }
    
    long getSize() {
        return size;
    }
}

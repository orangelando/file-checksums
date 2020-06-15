package lando.file.checksums;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class FileChecksums {

    private final Path dir;
    private final ZonedDateTime whenStarted;
    private final ZonedDateTime whenFinished;
    private final List<Checksum> checksums;
    
    public FileChecksums(
            Path dir, 
            ZonedDateTime whenStarted,
            ZonedDateTime whenFinished,
            List<Checksum> checksums) {
        
        this.dir = Objects.requireNonNull(dir);
        this.whenStarted = Objects.requireNonNull(whenStarted);
        this.whenFinished = Objects.requireNonNull(whenFinished);
        this.checksums = Objects.requireNonNull(checksums);
    }
    
    public Path getDir() {
        return dir;
    }
    
    public ZonedDateTime getWhenStarted() {
        return whenStarted;
    }
    
    public ZonedDateTime getWhenFinished() {
        return whenFinished;
    }
    
    public List<Checksum> getChecksums() {
        return Collections.unmodifiableList(checksums);
    }
}

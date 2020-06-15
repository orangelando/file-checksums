package lando.file.checksums;

import java.math.BigInteger;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.Duration;
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
    
    public int getNumFiles() {
        return checksums.size();
    }
    
    public Long getTotalSize() {
        return checksums.stream().mapToLong(c -> c.getSize()).sum();
    }
    
    public String getTotalSizeHumanReadable() {
        long size = getTotalSize();
        
        //XXX: extract this into a testable class
        DecimalFormat fmt = new DecimalFormat("#,##0.0");
        double t = 0.9;
        long s = 1024;
        
        long kib = s;
        long mib = s*s;
        long gib = s*s*s;
        long tib = s*s*s*s;
        long pib = s*s*s*s*s;
        
        final long d;
        final String m;
        
        if(      size >= t*pib ) { d = pib; m = "PiB"; }
        else if( size >= t*tib ) { d = tib; m = "TiB"; }
        else if( size >= t*gib ) { d = gib; m = "GiB"; }
        else if( size >= t*mib ) { d = mib; m = "MiB"; }
        else if( size >= t*kib ) { d = kib; m = "KiB"; }
        else { d = 1; m = ""; }
        
        return fmt.format((double)size/d) + " " + m;
    }
    
    public String getTotalReadTimeHumanReadable() {
        long t2 = getWhenFinished().toInstant().toEpochMilli();
        long t1 = getWhenStarted().toInstant().toEpochMilli();
        Duration d = Duration.ofMillis(t2 - t1);
        
        return d.toString().substring(2).toLowerCase();
    }
    
    public List<Checksum> getChecksums() {
        return Collections.unmodifiableList(checksums);
    }
}

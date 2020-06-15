package lando.file.checksums;

import java.time.ZonedDateTime;

public final class Checksum {
    
    private final String file;
    private final ZonedDateTime mtime;
    private final Long size;
    private final ZonedDateTime whenStartedReading;
    private final long readTimeMillis;
    private final String digestType;
    private final String digestHex;

    public Checksum(
            String file, 
            ZonedDateTime mtime, 
            long size, 
            ZonedDateTime whenStartedReading, 
            long readTimeMillis,
            String digestType,
            String digestHex) {
        
        this.file = file;
        this.mtime = mtime;
        this.size = size;
        this.whenStartedReading = whenStartedReading;
        this.readTimeMillis = readTimeMillis;
        this.digestType = digestType;
        this.digestHex = digestHex;
    }

    public String getFile() {
        return file;
    }
    
    public ZonedDateTime getMTime() {
        return mtime;
    }
    
    public Long getSize() {
        return size;
    }
    
    public ZonedDateTime getWhenStartedReading() {
        return whenStartedReading;
    }
    
    public long getReadTimeMillis() {
        return readTimeMillis;
    }
    
    public String getDigestType() {
        return digestType;
    }
    
    public String getDigestHex() {
        return digestHex;
    }
}

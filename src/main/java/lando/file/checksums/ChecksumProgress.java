package lando.file.checksums;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

final class ChecksumProgress {    
    
    private final List<Checksum> updates = new ArrayList<Checksum>();
    private final AtomicInteger numFilesRead = new AtomicInteger(0);
    private final AtomicLong numBytesRead = new AtomicLong(0);
    private final ZonedDateTime whenStarted;
    private final int numFilesToRead;
    private final long numBytesToRead;
    
    
    ChecksumProgress(ZonedDateTime whenStarted, int numFilesToRead, long numBytesToRead) {
        this.whenStarted = whenStarted;
        this.numFilesToRead = numFilesToRead;
        this.numBytesToRead = numBytesToRead;
    }
    
    int getNumFilesToRead() {
        return numFilesToRead;
    }
    
    long getNumBytesToRead() {
        return numBytesToRead;
    }
    
    int getNumFilesRead() {
        return numFilesRead.get();
    }
    
    long getNumBytesRead() {
        return numBytesRead.get();
    }
    
    ZonedDateTime getWhenStarted() {
        return whenStarted;
    }

    void addUpdate(Checksum checksum) {
        synchronized(updates) {
            numFilesRead.incrementAndGet();
            numBytesRead.addAndGet(checksum.getSize());
            updates.add(checksum);
        }
    }
    
    List<Checksum> takeUpdates() {
        var listToReturn = new ArrayList<Checksum>();
        
        synchronized(updates) {
            listToReturn.addAll(updates);
            updates.clear();
        }
        
        return listToReturn;
    }
}

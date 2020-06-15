package lando.file.checksums;

import java.util.concurrent.Future;

final class ChecksumsOperation {
    
    private final Future<FileChecksums> checksums;
    private final ChecksumProgress progress;
    
    ChecksumsOperation(
            Future<FileChecksums> checksums, 
            ChecksumProgress progress) {
        
        this.checksums = checksums;
        this.progress = progress;
    }

    Future<FileChecksums> getFileChecksums() {
        return checksums;
    }
    
    ChecksumProgress getProgress() {
        return progress;
    }
}

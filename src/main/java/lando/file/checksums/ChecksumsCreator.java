package lando.file.checksums;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import org.apache.commons.codec.digest.DigestUtils;

final class ChecksumsCreator {

    ChecksumsOperation startReadingChecksumsAsync(Path dir) throws Exception {

        var out = System.out;

        out.println("building file list...");

        var filesToRead = new FileListBuilder()
                .findAllFilesToRead(dir);

        long totalBytes = filesToRead.stream().mapToLong(f -> f.getSize()).sum();

        var fmt = new DecimalFormat("#,##0");

        out.printf("found %s files for %s bytes%n", 
                fmt.format(filesToRead.size()),
                fmt.format(totalBytes));

        var progress = new ChecksumProgress(ZonedDateTime.now(), filesToRead.size(), totalBytes);
        var completed = new ArrayList<Checksum>();
        var executor = Executors.newSingleThreadExecutor();
        var fut = executor.submit(() -> {

            var whenStarted = ZonedDateTime.now();

            filesToRead.forEach(f -> {
                try {

                    String digest;

                    ZonedDateTime whenStartedReading = ZonedDateTime.now();

                    try(InputStream is = Files.newInputStream(f.getFile())) {
                        digest = DigestUtils.sha384Hex(is);
                    }

                    ZonedDateTime whenFinishedReading = ZonedDateTime.now();

                    ZonedDateTime mtime = Files.getLastModifiedTime(f.getFile())
                            .toInstant()
                            .atZone(ZoneId.systemDefault());

                    long readTimeMillis = 
                            whenFinishedReading.toInstant().toEpochMilli() - 
                            whenStartedReading.toInstant().toEpochMilli();

                    Checksum checksum = new Checksum(
                            dir.relativize(f.getFile()).toString(), 
                            mtime, 
                            f.getSize(), 
                            whenStartedReading,
                            readTimeMillis,
                            "SHA-384",
                            digest);
                    
                    progress.addUpdate(checksum);
                    completed.add(checksum);

                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            });

            var whenFinished = ZonedDateTime.now();
            
            completed.sort( (a, b) -> a.getFile().compareTo(b.getFile()));

            return new FileChecksums(dir, whenStarted, whenFinished, completed);
        });

        executor.shutdown();

        return new ChecksumsOperation(fut, progress);
    }
}

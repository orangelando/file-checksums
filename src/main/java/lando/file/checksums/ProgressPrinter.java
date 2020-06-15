package lando.file.checksums;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

final class ProgressPrinter {
    
    private final ChecksumProgress prog;
    private final PrintStream out = System.out;
    
    ProgressPrinter(ChecksumProgress prog) {
        this.prog = Objects.requireNonNull(prog);
    }

    void printUpdates() {
        
        List<Checksum> updatesSince = prog.takeUpdates();
        
        if( updatesSince.isEmpty() ) {
            out.println("...");
            return;
        }
        
        var sizeFmt = new DecimalFormat("#,##0");
        var rateFmt = new DecimalFormat("0.0");
        
        updatesSince.forEach(c -> {
            out.printf("%16s %20s %10s MiB/s %s%n",
                    sizeFmt.format(c.getSize()),
                    Duration.ofMillis(c.getReadTimeMillis()),
                    rateFmt.format(mbPerSec(c.getSize(), c.getReadTimeMillis())),
                    c.getFile()
                    );
        });
        
        long totalBytesRead = updatesSince.stream().mapToLong(c -> c.getSize()).sum();
        long totalMillisReading = updatesSince.stream().mapToLong(c -> c.getReadTimeMillis()).sum();
        
        out.printf("%16s %20s %10s MiB/s %n",
                sizeFmt.format(totalBytesRead),
                Duration.ofMillis(totalMillisReading),
                rateFmt.format(mbPerSec(totalBytesRead, totalMillisReading))
                );
        
        ZonedDateTime now = ZonedDateTime.now();
        
        long runtimeMillis = now.toInstant().toEpochMilli() - prog.getWhenStarted().toInstant().toEpochMilli();
        
        out.println();
        
        out.printf("%s / %s = %5.1f%% %n",
                sizeFmt.format(prog.getNumBytesRead()),
                sizeFmt.format(prog.getNumBytesToRead()),
                100.0*prog.getNumBytesRead()/prog.getNumBytesToRead());
        
        out.printf("running for %s%n", 
                Duration.ofMillis(runtimeMillis)
                    .toString()
                    .substring(2)
                    .toLowerCase());
        
        long bytesToGo = prog.getNumBytesToRead() - prog.getNumBytesRead();
        double bytesPerMilli = (double)prog.getNumBytesRead()/runtimeMillis;
        double millisToGo = bytesToGo/bytesPerMilli;
        
        out.printf("estimated time left %s%n",
                Duration.ofMillis((long)millisToGo)
                    .toString()
                    .substring(2)
                    .toLowerCase());
        
        out.println();
    }
    
    private double mbPerSec(long bytes, long millis) {
        double megaBytes = bytes/1024.0/1024.0;
        double seconds = millis/1000.0;
        
        return megaBytes/seconds;
    }

}

package lando.file.checksums;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
    
    private static boolean isExcluded(Path path) {
        String name = path.getFileName().toString();
        
        return EXCLUDE_MACOS_FILES.contains(name);
    }
    
    List<FileToRead> findAllFilesToRead(Path rootDir) throws Exception {
        
        var list = new ArrayList<FileToRead>();
        var numVisited = new AtomicInteger(0);
        
        Files.walkFileTree(rootDir, new FileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if( isExcluded(dir) ) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                
                if( numVisited.incrementAndGet() % 10_000 == 0 ) {
                    System.out.println("visited " + new DecimalFormat("#,##0").format(numVisited.get()) + " files so far...");
                }
                
                if( ! isExcluded(file) && attrs.isRegularFile() && ! attrs.isSymbolicLink() ) {
                    list.add(new FileToRead(file, attrs.size()));
                }
                
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                
                if( isExcluded(file)) {
                    System.out.println("file visit failed but is OK " + (exc == null ? "null exception" : exc.getMessage()));
                    return FileVisitResult.CONTINUE;
                }
                
                if( exc != null ) {
                    throw exc;
                }
                
                throw new IllegalArgumentException("file visit failed for " + file + " but no exception given");
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if( exc != null ) {
                    System.out.println("postVisitDirectory with non null exception: " + dir);
                    exc.printStackTrace(System.out);
                }
                
                return FileVisitResult.CONTINUE;
            }
            
        });
        
        //
        System.out.println("writing tmp file...");
        try(BufferedWriter wout = Files.newBufferedWriter(Paths.get("/Users/oroman/tmp/filelist.txt"))) {
            for(FileToRead f: list) {
                wout.write(f.getFile().toString());
                wout.write("\n");
            }
        }
        System.out.println("done.");
        
        return list;
    }
}

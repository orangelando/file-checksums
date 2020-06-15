package lando.file.checksums;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lando.file.checksums.json.LongSerializer;
import lando.file.checksums.json.PathSerializer;
import lando.file.checksums.json.ZonedDateTimeSerializer;

final class FSMapper {
    
    private final Path outputDir;
    private final ObjectMapper jsonMapper;
    
    FSMapper(Path outputDir) {
        this.outputDir = Objects.requireNonNull(outputDir);
        this.jsonMapper = new ObjectMapper();
        this.jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        SimpleModule module = new SimpleModule();
        
        module.addSerializer(Path.class, new PathSerializer());
        module.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer());
        module.addSerializer(Long.class, new LongSerializer());
        
        this.jsonMapper.registerModule(module);
    }

    Path save(FileChecksums checksums, String name) throws Exception {
        
        var dt = LocalDateTime.now().toString();
        var path = outputDir.resolve(name + "." + dt + ".json.gz");
        
        try(OutputStream fout = Files.newOutputStream(path);
            GZIPOutputStream gout = new GZIPOutputStream(fout, 4096)) {
            jsonMapper.writeValue(gout, checksums);
        }
        
        return path;
    }
    
    FileChecksums load(Path path) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

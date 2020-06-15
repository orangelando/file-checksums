package lando.file.checksums.json;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class PathSerializer extends StdSerializer<Path> {
  
    private static final long serialVersionUID = 1L;

    public PathSerializer() {
        this(null);
    }
   
    public PathSerializer(Class<Path> t) {
        super(t);
    }
 
    @Override
    public void serialize(
            Path value, 
            JsonGenerator jgen, 
            SerializerProvider provider) throws IOException, JsonProcessingException {
        
        jgen.writeString(value.toString());
    }
}

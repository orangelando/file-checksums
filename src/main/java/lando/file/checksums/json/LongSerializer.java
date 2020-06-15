package lando.file.checksums.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class LongSerializer extends StdSerializer<Long>  {

    private static final long serialVersionUID = 1L;

    public LongSerializer() {
        this(null);
    }
   
    public LongSerializer(Class<Long> t) {
        super(t);
    }
 
    @Override
    public void serialize(
            Long value, 
            JsonGenerator jgen, 
            SerializerProvider provider) throws IOException, JsonProcessingException {
        
        jgen.writeString(value.toString());
    }
}

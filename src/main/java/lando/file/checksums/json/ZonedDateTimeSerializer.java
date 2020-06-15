package lando.file.checksums.json;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ZonedDateTimeSerializer extends StdSerializer<ZonedDateTime>  {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private static final long serialVersionUID = 1L;

    public ZonedDateTimeSerializer() {
        this(null);
    }
   
    public ZonedDateTimeSerializer(Class<ZonedDateTime> t) {
        super(t);
    }
 
    @Override
    public void serialize(
            ZonedDateTime value, 
            JsonGenerator jgen, 
            SerializerProvider provider) throws IOException, JsonProcessingException {
        
        jgen.writeString(value.format(FMT));
    }
}

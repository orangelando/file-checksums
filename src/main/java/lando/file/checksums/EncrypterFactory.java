package lando.file.checksums;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.commons.codec.binary.Hex;

final class EncrypterFactory {

    private EncrypterFactory() {
        
    }
    
    static Encrypter createFromProps(Properties props) throws Exception {
        return new Encrypter(
                props.getProperty("crypto.password").toCharArray(),
                Hex.decodeHex(props.getProperty("crypto.salt")),
                Hex.decodeHex(props.getProperty("crypto.iv"))
                );
    }
    
    static Encrypter createFromProps(Path propsPath) throws Exception {
        return createFromProps(loadProps(propsPath));
    }
    
    private static Properties loadProps(Path path) throws Exception {
        Properties props = new Properties();
        
        try(InputStream is = Files.newInputStream(path)) {
            props.load(is);
        }
        
        return props;
    }
}

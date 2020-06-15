package lando.file.checksums;

import java.nio.file.Paths;

public class DecryptTestApp {

    public static void main(String [] args) throws Exception {
        
        Encrypter encrypter = EncrypterFactory.createFromProps(
                Paths.get("/Users/oroman/Desktop/checksum-credentials.properties"));
        
        encrypter.decrypt(
                Paths.get("/Users/oroman/Desktop/checksum-test.2020-06-15T01:06:50.079117.json.gz.enc"), 
                Paths.get("/Users/oroman/Desktop/descrypt.json.gz"));
        
        System.out.println("done.");
    }
}

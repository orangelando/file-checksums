package lando.file.checksums;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoExplore {
    
    

    public static void main(String [] args) throws Exception {
        
       
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(password, salt, PASSWORD_ITERATIONS, KEY_LENGTH);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        //encrypt
        cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));
        //AlgorithmParameters params = cipher.getParameters();
        //byte[] ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
        
        //System.out.println(ivBytes.length);
        
        //for(byte b: ivBytes) {
            //System.out.print(b);
            //System.out.print(" ");
        //}
        
        //System.out.println();

        //do encrypt
        byte[] encBytes = cipher.doFinal(Files.readAllBytes(clearFile));
        
        Files.write(encryptedFile, encBytes);
        
        testDecrypt();
        
        System.out.println("done.");  
        
    }
    
    static char[] password = "foobar123".toCharArray();
    static Path clearFile = Paths.get("/Users/oroman/Desktop/checksums.json.gz");
    static Path encryptedFile = Paths.get("/Users/oroman/Desktop/checksums.json.gz.enc");
    static Path decryptedFile = Paths.get("/Users/oroman/Desktop/decrypted.json.gz");
    
    //I have no place to store the salt and initialization vector!
    static byte[] salt = {
            -10,  59, -59, -36, 
            111,  93,  12, -78, 
             81,  54,  59, -70, 
            -26, -76, -42, -88};
    
    static byte[] iv = {
              85, -119,  40,   15, 
             117,   60, -89, -128, 
            -115,   18,  38,   20, 
            -111,  118, 120,   57};
    
    private static void testDecrypt() throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(password, salt, PASSWORD_ITERATIONS, KEY_LENGTH);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        //decrypt
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        //AlgorithmParameters params = cipher.getParameters();
        //byte[] ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
        
        //System.out.println(ivBytes.length);
        
        //for(byte b: ivBytes) {
            //System.out.print(b);
            //System.out.print(" ");
        //}
        
        //System.out.println();

        //do decrypt
        byte[] decBytes = cipher.doFinal(Files.readAllBytes(encryptedFile));
        
        Files.write(decryptedFile, decBytes);
    }
    
    private static final int PASSWORD_ITERATIONS = 65536; // vs brute force
    private static final int KEY_LENGTH          = 256;

    /*
    private char[]     pass                = "password".toCharArray(); // hardcoded or read me from a file
    private byte[]     salt                = new byte[20]; // for more confusion
    private byte[]     ivBytes             = null;

    private Cipher createCipher(boolean encryptMode) throws Exception {

        if (!encryptMode && ivBytes == null) {
            throw new IllegalStateException("ivBytes is null");
        }

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(pass, salt, PASSWORD_ITERATIONS, KEY_LENGTH);

        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        int mode = encryptMode ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;

        if (ivBytes == null) {

            cipher.init(mode, secret);
            AlgorithmParameters params = cipher.getParameters();
            ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();

        } else {

            cipher.init(mode, secret, new IvParameterSpec(ivBytes));
        }

        return cipher;
    }

    public String encode(String plainText) throws Exception {

        Cipher cipher = createCipher(true);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

        return new String(encryptedBytes);

    }
    
      @Override
    public String decode(String encodedText) throws Exception {

        Cipher cipher = createCipher(false);

        return new String(cipher.doFinal(encodedText.getBytes()), "UTF-8");
    }
    */
}

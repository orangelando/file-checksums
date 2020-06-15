package lando.file.checksums;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

final class Encrypter {
    
    private static final int BUF_SIZE            = 8192; //seems to be a good buffer size
    private static final int PASSWORD_ITERATIONS = 65536; // vs brute force
    private static final int KEY_LENGTH          = 256;
    
    private final char[] password;
    private final byte[] salt;
    private final byte[] iv;
    
    Encrypter(char[] password, byte[] salt, byte[] iv) {
        this.password = Objects.requireNonNull(password);
        this.salt = Objects.requireNonNull(salt);
        this.iv = Objects.requireNonNull(iv);
    }

    void encrypt(Path srcFile, Path dstFile) throws Exception {
        doCrypto(srcFile, dstFile, Cipher.ENCRYPT_MODE);
    }
    
    void decrypt(Path srcFile, Path dstFile) throws Exception {
        doCrypto(srcFile, dstFile, Cipher.DECRYPT_MODE);
    }
    
    private void doCrypto(Path srcFile, Path dstFile, int cipherMode) throws Exception {
        Cipher cipher = createCipher(cipherMode);
        
        byte[] inBuf = new byte[BUF_SIZE];
        byte[] outBuf = new byte[cipher.getOutputSize(inBuf.length)];
        
        long numBytesToRead = Files.size(srcFile);
        long numBytesReadSoFar = 0;
        
        try(InputStream in = Files.newInputStream(srcFile);
            OutputStream out = Files.newOutputStream(dstFile)) {
            
            while( numBytesReadSoFar < numBytesToRead) {
                
                int bytesToRead = Math.min(inBuf.length, (int)(numBytesToRead - numBytesReadSoFar));
                int bytesActuallyRead = in.read(inBuf, 0, bytesToRead);
                numBytesReadSoFar += bytesActuallyRead;
                
                int bytesWritten = cipher.update(inBuf, 0, bytesActuallyRead, outBuf, 0);
                
                out.write(outBuf, 0, bytesWritten);
            }
            
            int bytesWritten = cipher.doFinal(outBuf, 0);
            out.write(outBuf, 0, bytesWritten);
        }
    }
    
    private Cipher createCipher(int cipherMode) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(password, salt, PASSWORD_ITERATIONS, KEY_LENGTH);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        cipher.init(cipherMode, secret, new IvParameterSpec(iv));
        
        return cipher;
    }
}

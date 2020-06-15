package lando.file.checksums;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

final class FtpUploader {

    void upload(FtpUploadParams params, Path srcFile) throws Exception {
        
        Path dstFile = Paths.get(params.getDir()).resolve(srcFile.getFileName());
        
        System.out.println("uploading to " + dstFile);
        
        FTPClient ftpClient = new FTPClient();
        
        try {
            ftpClient.connect(params.getServer(), params.getPort());
            ftpClient.login(params.getUser(), params.getPassword());
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            try(InputStream in = Files.newInputStream(srcFile);
                OutputStream out = ftpClient.storeFileStream(dstFile.toString())) {
            
                IOUtils.copy(in, out);
            }
 
            boolean completed = ftpClient.completePendingCommand();
            
            if (! completed) {
                System.out.println("Unable to upload file...");
            }
            
        } finally {
            if( ftpClient.isConnected() ) {
                try { ftpClient.logout(); } catch(Exception e) {}
                try { ftpClient.disconnect(); } catch(Exception e) {}
                
            }
        }
    }
}

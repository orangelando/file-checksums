package lando.file.checksums;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public final class WriteChecksumsApp {
    
    private static final String CREDENTIALS_FILE_OPT = "-credentialsFile";
    private static final String ENCRYPT_OPT = "-encrypt";
    private static final String UPLOAD_OPT = "-upload";
    
    
    @Option(name="-dir", required=true, usage="dir to generate checksums for")
    private String dir;
    
    @Option(name="-name", required=true, usage="name to embed in checksum file-name, cannot contain dots")
    private String name;
    
    @Option(name="-outputDir", required=true, usage="dir to put checksums file in")
    private String outputDir;
    
    @Option(name=CREDENTIALS_FILE_OPT, required=false, usage="properties file containing ftp and file encryption credentials")
    private String credentialsFile;
    
    @Option(name=ENCRYPT_OPT, required=false, usage="Also save an encrypted checksum file", depends=CREDENTIALS_FILE_OPT)
    private boolean encrypt;
    
    @Option(name=UPLOAD_OPT, required=false, usage="Upload encrypted file via ftp", depends= {ENCRYPT_OPT, CREDENTIALS_FILE_OPT})
    private boolean upload;
    
    private WriteChecksumsApp() {
    }
    
    public static final void main(String [] args) throws Exception {
        WriteChecksumsApp app = new WriteChecksumsApp();
        CmdLineParser cliParser = new CmdLineParser(app);
        
        try {
            cliParser.parseArgument(args);
        } catch(CmdLineException e) {
            printUsageAndExit(e);
        }
        
        app.execute();
    }
    
    private static void printUsageAndExit(CmdLineException e) {
        var out = System.out;
        
        out.println(e.getMessage());
        e.getParser().printSingleLineUsage(out);
        System.exit(1);
    }
    
    private void execute() throws Exception {
        
        var checksums = createChecksums();
        var checksumPath = saveChecksums(checksums);
        
        System.out.println("wrote " + checksumPath);
        
        if( encrypt ) {
            Properties props = loadCredentialProps();
            
            var encryptedPath = saveEncrypted(checksumPath, props);
            
            System.out.println("wrote " + encryptedPath);
            
            if( upload ) {
                uploadFtp(encryptedPath, props);
            }
            
        }
        
        System.out.println("done.");
    }
    
    private Properties loadCredentialProps() throws Exception {
        Properties props = new Properties();
        
        try(InputStream is = Files.newInputStream(Paths.get(credentialsFile))) {
            props.load(is);
        }
        
        return props;
    }

    private FileChecksums createChecksums() throws Exception {
        var creator = new ChecksumsCreator();
        var op = creator.startReadingChecksumsAsync(Paths.get(dir));
        var printer = new ProgressPrinter(op.getProgress());
        
        do {
            try {
                op.getFileChecksums().get(3, TimeUnit.SECONDS);
            } catch(Exception e) {
                //something happened, could have been cancelled, could have timed out.
            }
            printer.printUpdates();
        } while( ! op.getFileChecksums().isDone() ); 
        
        return op.getFileChecksums().get();
    }

    
    private Path saveChecksums(FileChecksums checksums) throws Exception {
        var mapper = new FSMapper(Paths.get(outputDir));
        
        return mapper.save(checksums, name);
    }
    
    private Path saveEncrypted(Path checksumPath, Properties props) throws Exception {
        var path = checksumPath.getParent().resolve(checksumPath.getFileName().toString() + ".enc");
        var encrypter = EncrypterFactory.createFromProps(props);
        
        encrypter.encrypt(checksumPath, path);
        
        return path;
    }
    
    private void uploadFtp(Path encryptedPath, Properties props) throws Exception {
        var params = FtpUploadParams.fromProps(props);
        var uploader = new FtpUploader();
        
        uploader.upload(params, encryptedPath);
    }
}

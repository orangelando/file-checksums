package lando.file.checksums;

import java.util.Objects;
import java.util.Properties;

final class FtpUploadParams {
    
    static FtpUploadParams fromProps(Properties props) {
        return new FtpUploadParams(
                props.getProperty("ftp.server"),
                Integer.parseInt(props.getProperty("ftp.port", "21"), 10),
                props.getProperty("ftp.user"),
                props.getProperty("ftp.password"),
                props.getProperty("ftp.dir"));
    }

    private final String server;
    private final int port;
    private final String user;
    private final String password;
    private final String dir;
    
    FtpUploadParams(String server, int port, String user, String password, String dir) {
        this.server = Objects.requireNonNull(server);
        this.port = port;
        this.user = Objects.requireNonNull(user);
        this.password = Objects.requireNonNull(password);
        this.dir = Objects.requireNonNull(dir);
    }

    String getServer() {
        return server;
    }
    
    int getPort() {
        return port;
    }

    String getUser() {
        return user;
    }

    String getPassword() {
        return password;
    }
    
    String getDir() {
        return dir;
    }
}

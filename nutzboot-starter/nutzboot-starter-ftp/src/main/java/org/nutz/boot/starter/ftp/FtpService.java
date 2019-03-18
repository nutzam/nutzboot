package org.nutz.boot.starter.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@IocBean
public class FtpService {
    private static final Log log = Logs.get();
    private String host;
    private int port;
    private String username;
    private String password;
    private int timeout;
    private static String LOCAL_CHARSET = "UTF-8";
    // FTP协议里面,规定文件名编码为iso-8859-1
    private static String SERVER_CHARSET = "ISO-8859-1";

    public FTPClient connect() {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(host, port);
            ftpClient.login(username, password);
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                ftpClient.disconnect();
                log.info("[FtpService] FTP logon denied");
                return null;
            } else {
                log.info("[FtpService] FTP logon success");
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setDefaultTimeout(timeout * 1000);
            ftpClient.setBufferSize(3072);
            ftpClient.sendCommand("OPTS UTF8", "ON");
            ftpClient.setControlEncoding(LOCAL_CHARSET);
        } catch (IOException e) {
            log.error("[FtpService] FTP config error", e);
        }
        return ftpClient;
    }

    public boolean upload(String filePath, String fileName, InputStream input) {
        boolean result = false;
        FTPClient ftpClient = null;
        try {
            ftpClient = connect();
            if (ftpClient == null) {
                return false;
            }
            if (!ftpClient.changeWorkingDirectory(filePath)) {
                //如果目录不存在创建目录
                String[] dirs = filePath.split("/");
                String tempPath = "";
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir))
                        continue;
                    tempPath += "/" + dir;
                    if (!ftpClient.changeWorkingDirectory(tempPath)) {
                        if (!ftpClient.makeDirectory(tempPath)) {
                            return result;
                        } else {
                            ftpClient.changeWorkingDirectory(tempPath);
                        }
                    }
                }
            }
            fileName = new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
            if (!ftpClient.storeFile(fileName, input)) {
                return result;
            }
            result = true;
        } catch (IOException e) {
            log.error("[FtpService] error when ftp upload file", e);
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    public boolean delete(String fileNameHasPath) {
        boolean result = false;
        FTPClient ftpClient = null;
        try {
            ftpClient = connect();
            if (ftpClient == null) {
                return false;
            }
            fileNameHasPath = new String(fileNameHasPath.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
            ftpClient.dele(fileNameHasPath);
            result = true;
        } catch (IOException e) {
            log.error("[FtpService] error when ftp delete file", e);
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    public void download(String fileNameHasPath, OutputStream outputStream) {
        FTPClient ftpClient = null;
        try {
            ftpClient = connect();
            if (ftpClient == null) {
                return;
            }
            fileNameHasPath = new String(fileNameHasPath.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
            Streams.writeAndClose(outputStream, ftpClient.retrieveFileStream(fileNameHasPath));
        } catch (IOException e) {
            log.error("[FtpService] error when ftp download file", e);
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                }
            }
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}

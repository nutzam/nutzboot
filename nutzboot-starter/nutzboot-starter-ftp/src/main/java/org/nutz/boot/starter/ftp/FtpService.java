package org.nutz.boot.starter.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.nutz.boot.starter.ftp.FtpStarter.*;

@IocBean(create = "init")
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
    @Inject
    protected PropertiesProxy conf;

    public void init() {
        if (conf.getBoolean(PROP_ENABLED, false)) {
            host = conf.get(PROP_SERVER_HOST, "");
            port = conf.getInt(PROP_SERVER_PORT, 21);
            username = conf.get(PROP_SERVER_USERNAME, "");
            password = conf.get(PROP_SERVER_PASSWORD, "");
            timeout = conf.getInt(PROP_SERVER_TIMEOUT, 30);
            //连接一下测试是否配置正确
            FTPClient ftpClient = connect();
            if (ftpClient != null) {
                if (ftpClient.isConnected()) {
                    try {
                        //退出登录
                        ftpClient.logout();
                        //关闭连接
                        ftpClient.disconnect();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

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
}

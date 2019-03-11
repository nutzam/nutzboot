package org.nutz.boot.starter.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.IOException;
import java.io.InputStream;

@IocBean
public class FtpService {
    private static final Log log = Logs.get();
    private String host;
    private int port;
    private String username;
    private String password;
    private int timeout;
    private static String LOCAL_CHARSET = "UTF-8";
    // FTP协议里面，规定文件名编码为iso-8859-1
    private static String SERVER_CHARSET = "ISO-8859-1";

    public FTPClient connect() {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(host, port);
            //登录服务器
            ftpClient.login(username, password);
            //判断返回码是否合法
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                //不合法时断开连接
                ftpClient.disconnect();
                log.info("[FtpService] FTP用户名或密码错误");
                return null;
            } else {
                log.info("[FtpService] FTP连接成功");
            }
            //设置被动模式
            ftpClient.enterLocalPassiveMode();
            //设置文件类型
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //设置超时时间
            ftpClient.setDefaultTimeout(timeout * 1000);
            //设置缓冲区大小
            ftpClient.setBufferSize(3072);
            //设置字符编码
            ftpClient.sendCommand("OPTS UTF8", "ON");
            ftpClient.setControlEncoding(LOCAL_CHARSET);
        } catch (IOException e) {
            log.error("[FtpService] FTP配置错误,请检查配置");
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
            // 切换到上传目录
            if (!ftpClient.changeWorkingDirectory(filePath)) {
                // 如果目录不存在创建目录
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
            // 上传文件
            fileName = new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
            if (!ftpClient.storeFile(fileName, input)) {
                return result;
            }
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != input) {
                try {
                    // 关闭输入流
                    input.close();
                } catch (IOException e) {
                }
            }
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
        return result;
    }

    public boolean delete(String filePath, String fileName) {
        boolean result = false;
        FTPClient ftpClient = null;
        try {
            ftpClient = connect();
            if (ftpClient == null) {
                return false;
            }
            fileName = new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
            ftpClient.changeWorkingDirectory(filePath);
            ftpClient.dele(fileName);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
        return result;
    }

    public InputStream download(String fileNameHasPath) {
        FTPClient ftpClient = null;
        try {
            ftpClient = connect();
            if (ftpClient == null) {
                return null;
            }
            fileNameHasPath = new String(fileNameHasPath.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
            InputStream inputStream = ftpClient.retrieveFileStream(fileNameHasPath);
            ftpClient.completePendingCommand();
            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
        return null;
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

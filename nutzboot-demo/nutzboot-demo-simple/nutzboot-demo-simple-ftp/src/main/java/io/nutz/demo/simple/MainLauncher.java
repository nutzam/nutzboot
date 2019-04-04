package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.boot.starter.ftp.FtpService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.Files;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;

@IocBean(create = "init")
public class MainLauncher {
    private static final Log log = Logs.get();

    private static String filePath = "/upload/file/";
    private static String fileName = "demo.txt";
    @Inject
    private FtpService ftpService;

    @At("/download")
    @Ok("void")
    public void download(HttpServletResponse response) {
        try {
            response.setHeader("Content-Type", "application/java-archive");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            ftpService.download(filePath + fileName, response.getOutputStream());
        } catch (Exception e) {
            log.error(e);
        }
    }

    public static void main(String[] args) {
        new NbApp().setPrintProcDoc(true).start();
    }



    public void init() {
        try {
            File file = new File(fileName);
            Files.write(file, "大鲨鱼最帅".getBytes("UTF-8"));
            if (ftpService.upload(filePath, fileName, Files.findFileAsStream("demo.txt"))) {
                log.info("上传成功");
            } else {
                log.info("上传失败");
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ftpService.download(filePath + fileName, outputStream);
            log.info(fileName + "::" + outputStream.toString("UTF-8"));
        } catch (Exception e) {
            log.error(e);
        }
    }
}

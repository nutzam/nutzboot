package org.nutz.boot.starter.fastdfs;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.TrackerServer;
import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.img.Images;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import static org.nutz.boot.starter.fastdfs.FastdfsStarter.*;

@IocBean(create = "init", depose = "close")
public class FastdfsService {
    private static final Log log = Logs.get();
    private static final String PRE = "fastdfs.";
    private static String IMAGE_WATERMARK_SUFFIX = "-wmark";
    private static String IMAGE_THUMB_SUFFIX = "-thumb";
    private static int IMAGE_THUMB_WIDTH = 150;
    private static int IMAGE_THUMB_HEIGHT = 150;
    private static final int DEFAULT_LOCATION = Images.WATERMARK_CENTER;
    private static final float DEFAULT_OPACITY = 0.5F;
    private static final int DEFAULT_MARGIN = 0;
    private static final String FILENAME_SEPERATOR = "/";
    private static final String EXT_SEPERATOR = ".";
    private static FilePool filePool;

    @Inject
    private PropertiesProxy conf;
    private FastDfsClientFactory fastDfsClientFactory;
    private FastDfsClientPool fastDfsClientPool;

    public void init() {
        Properties prop = new Properties();
        for (String key : conf.keySet()) {
            if (key.startsWith(PRE)) {
                prop.put(key, conf.get(key));
            }
        }
        IMAGE_WATERMARK_SUFFIX = conf.get(PROP_IMAGE_WATERMARKSUFFIX, "-wmark");
        IMAGE_THUMB_SUFFIX = conf.get(PROP_IMAGE_THUMBSUFFIX, "-thumb");
        IMAGE_THUMB_WIDTH = conf.getInt(PROP_IMAGE_THUMBWIDTH, 150);
        IMAGE_THUMB_HEIGHT = conf.getInt(PROP_IMAGE_THUMBHEIGHT, 150);
        GenericObjectPoolConfig cfg = new GenericObjectPoolConfig();
        cfg.setMaxIdle(conf.getInt(PROP_POOL_MAXIDLE, 10));
        cfg.setMinIdle(conf.getInt(PROP_POOL_MINIDLE, 1));
        cfg.setMaxTotal(conf.getInt(PROP_POOL_MAXTOTAL, 20));
        cfg.setMaxWaitMillis(conf.getInt(PROP_POOL_MAXWAITMILLIS, 6000));
        fastDfsClientFactory = new FastDfsClientFactory(prop);
        fastDfsClientPool = new FastDfsClientPool(fastDfsClientFactory, cfg);
        filePool = NutFilePool.getOrCreatePool(conf.get(PROP_FILEPOOL_PATH, Disks.home() + "/fastdfs_tmp"), conf.getInt(PROP_FILEPOOL_SIZE, 200));
    }

    public void close() {
        if (fastDfsClientPool != null) {
            fastDfsClientPool.close();
        }
    }

    /**
     * 生成带Token的文件访问路径
     *
     * @param filename 文件名
     * @return
     */
    public String getFileTokenUrl(String filename) {
        try {
            String baseUrl = conf.get(PROP_HTTP_TOKEN_BASE_URL, "");
            String secretKey = conf.get(PROP_HTTP_SECRET_KEY, "");
            if (Strings.isBlank(filename) || Strings.isBlank(secretKey) || Strings.isBlank(baseUrl)) {
                log.info("[FastdfsService] filename & http_token_base_url & http_secret_key must not empty");
                return "";
            }
            int pos = filename.indexOf(FILENAME_SEPERATOR);
            String tokenFilename = filename.substring(pos + 1);
            long ts = Times.getTS();
            StringBuilder sb = new StringBuilder();
            sb.append(baseUrl);
            if (!baseUrl.endsWith(FILENAME_SEPERATOR))
                sb.append(FILENAME_SEPERATOR);
            sb.append(filename);
            sb.append("?token=");
            sb.append(getToken(tokenFilename, ts, secretKey));
            sb.append("&ts=");
            sb.append(ts);
            return sb.toString();
        } catch (Exception e) {
            log.error(e);
        }
        return "";
    }

    /**
     * 生成带Token的图片访问路径
     *
     * @param filename 文件名
     * @param type     0-原图 1水印图 2缩略图
     * @return
     */
    public String getImageTokenUrl(String filename, int type) {
        try {
            String baseUrl = conf.get(PROP_HTTP_TOKEN_BASE_URL, "");
            String secretKey = conf.get(PROP_HTTP_SECRET_KEY, "");
            if (Strings.isBlank(filename) || Strings.isBlank(secretKey) || Strings.isBlank(baseUrl)) {
                log.info("[FastdfsService] filename & http_token_base_url & http_secret_key must not empty");
                return "";
            }
            int pos = filename.indexOf(FILENAME_SEPERATOR);
            String tokenFilename = filename.substring(pos + 1);
            tokenFilename = getFileName(type, tokenFilename);
            filename = getFileName(type, filename);
            long ts = Times.getTS();
            StringBuilder sb = new StringBuilder();
            sb.append(baseUrl);
            if (!baseUrl.endsWith(FILENAME_SEPERATOR))
                sb.append(FILENAME_SEPERATOR);
            sb.append(filename);
            sb.append("?token=");
            sb.append(getToken(tokenFilename, ts, secretKey));
            sb.append("&ts=");
            sb.append(ts);
            return sb.toString();
        } catch (Exception e) {
            log.error(e);
        }
        return "";
    }

    /**
     * 获取Token
     *
     * @param filename   文件名
     * @param timestamp  时间戳
     * @param secret_key 密钥
     * @return
     * @throws Exception
     */
    private String getToken(String filename, long timestamp, String secret_key) throws Exception {
        byte[] bsFilename = filename.getBytes(conf.get(PROP_CHARSET, "UTF-8"));
        byte[] bsKey = secret_key.getBytes(conf.get(PROP_CHARSET, "UTF-8"));
        byte[] bsTimestamp = (new Long(timestamp)).toString().getBytes(conf.get(PROP_CHARSET, "UTF-8"));
        byte[] buff = new byte[bsFilename.length + bsKey.length + bsTimestamp.length];
        System.arraycopy(bsFilename, 0, buff, 0, bsFilename.length);
        System.arraycopy(bsKey, 0, buff, bsFilename.length, bsKey.length);
        System.arraycopy(bsTimestamp, 0, buff, bsFilename.length + bsKey.length, bsTimestamp.length);
        return Lang.digest("MD5", buff, null, 1);
    }

    /**
     * 获取文件名
     *
     * @param type          0-原图 1水印图 2缩略图
     * @param tokenFilename 文件名
     * @return
     */
    private String getFileName(int type, String tokenFilename) {
        StringBuilder sb = new StringBuilder();
        String filename = tokenFilename.substring(0, tokenFilename.indexOf(EXT_SEPERATOR));
        String ext = tokenFilename.substring(tokenFilename.indexOf(EXT_SEPERATOR));
        sb.append(filename);
        switch (type) {
            case 1:
                sb.append(IMAGE_WATERMARK_SUFFIX);
                break;
            case 2:
                sb.append(IMAGE_THUMB_SUFFIX);
                break;
        }
        sb.append(ext);
        return sb.toString();
    }

    /**
     * 上传文件(一次性读取全部字节,尽量不要使用,比较耗内存)
     *
     * @param file     文件字节
     * @param ext      后缀名
     * @param metaInfo 元信息
     * @return
     */
    public String uploadFile(byte[] file, String ext, Map<String, String> metaInfo) {
        String path = "";
        TrackerServer trackerServer = null;
        StorageClient1 storageClient1 = null;
        try {
            trackerServer = fastDfsClientPool.borrowObject();
            storageClient1 = new StorageClient1(trackerServer, null);
            NameValuePair data[] = null;
            if (Lang.isNotEmpty(metaInfo)) {
                data = new NameValuePair[metaInfo.size()];
                int index = 0;
                for (Map.Entry<String, String> entry : metaInfo.entrySet()) {
                    data[index] = new NameValuePair(entry.getKey(), entry.getValue());
                    index++;
                }
            }
            path = storageClient1.uploadFile1(file, ext, data);
        } catch (Exception e) {
            throw Lang.makeThrow("[FastdfsService] upload file error : %s", e.getMessage());
        } finally {
            if (trackerServer != null)
                fastDfsClientPool.returnObject(trackerServer);
            storageClient1 = null;
        }
        return path;
    }

    /**
     * 上传文件
     *
     * @param local_filename 文件路径
     * @param ext            后缀名
     * @param metaInfo       元信息
     * @return
     */
    public String uploadFile(String local_filename, String ext, Map<String, String> metaInfo) {
        String path = "";
        TrackerServer trackerServer = null;
        StorageClient1 storageClient1 = null;
        try {
            trackerServer = fastDfsClientPool.borrowObject();
            storageClient1 = new StorageClient1(trackerServer, null);
            NameValuePair data[] = null;
            if (Lang.isNotEmpty(metaInfo)) {
                data = new NameValuePair[metaInfo.size()];
                int index = 0;
                for (Map.Entry<String, String> entry : metaInfo.entrySet()) {
                    data[index] = new NameValuePair(entry.getKey(), entry.getValue());
                    index++;
                }
            }
            path = storageClient1.uploadFile1(local_filename, ext, data);
        } catch (Exception e) {
            throw Lang.makeThrow("[FastdfsService] upload file error : %s", e.getMessage());
        } finally {
            if (trackerServer != null)
                fastDfsClientPool.returnObject(trackerServer);
            storageClient1 = null;
        }
        return path;
    }

    /**
     * 上传从文件
     *
     * @param local_filename 文件路径
     * @param originalPath   源文件路径（含groupId）
     * @param prefixName     从文件名后缀
     * @param ext            从文件类型
     * @param metaInfo       元信息
     */
    public String uploadSalveFile(String local_filename, String originalPath, String prefixName, String ext, Map<String, String> metaInfo) {
        String path = "";
        TrackerServer trackerServer = null;
        StorageClient1 storageClient1 = null;
        try {
            trackerServer = fastDfsClientPool.borrowObject();
            storageClient1 = new StorageClient1(trackerServer, null);
            //StorageClient storageClient=new StorageClient(trackerServer,null);
            NameValuePair data[] = null;
            if (Lang.isNotEmpty(metaInfo)) {
                data = new NameValuePair[metaInfo.size()];
                int index = 0;
                for (Map.Entry<String, String> entry : metaInfo.entrySet()) {
                    data[index] = new NameValuePair(entry.getKey(), entry.getValue());
                    index++;
                }

            }
            path = storageClient1.uploadFile1(originalPath, prefixName, local_filename, ext, data);
        } catch (Exception e) {
            throw Lang.makeThrow("[FastdfsService] upload file error : %s", e.getMessage());
        } finally {
            if (trackerServer != null)
                fastDfsClientPool.returnObject(trackerServer);
            storageClient1 = null;
        }
        return path;
    }

    /**
     * 上传从文件(一次性读取全部字节,尽量不要使用,比较耗内存)
     *
     * @param file         从文件字节
     * @param originalPath 源文件路径（含groupId）
     * @param prefixName   从文件名后缀
     * @param ext          从文件类型
     * @param metaInfo     元信息
     */
    public String uploadSalveFile(byte[] file, String originalPath, String prefixName, String ext, Map<String, String> metaInfo) {
        String path = "";
        TrackerServer trackerServer = null;
        StorageClient1 storageClient1 = null;
        try {
            trackerServer = fastDfsClientPool.borrowObject();
            storageClient1 = new StorageClient1(trackerServer, null);
            //StorageClient storageClient=new StorageClient(trackerServer,null);
            NameValuePair data[] = null;
            if (Lang.isNotEmpty(metaInfo)) {
                data = new NameValuePair[metaInfo.size()];
                int index = 0;
                for (Map.Entry<String, String> entry : metaInfo.entrySet()) {
                    data[index] = new NameValuePair(entry.getKey(), entry.getValue());
                    index++;
                }

            }
            path = storageClient1.uploadFile1(originalPath, prefixName, file, ext, data);
        } catch (Exception e) {
            throw Lang.makeThrow("[FastdfsService] upload file error : %s", e.getMessage());
        } finally {
            if (trackerServer != null)
                fastDfsClientPool.returnObject(trackerServer);
            storageClient1 = null;
        }
        return path;
    }

    /**
     * 下载文件
     *
     * @param fullFilename 文件路径
     * @return
     */
    public byte[] downLoadFile(String fullFilename) {
        byte[] data = null;
        TrackerServer trackerServer = null;
        StorageClient1 storageClient1 = null;
        try {
            trackerServer = fastDfsClientPool.borrowObject();
            storageClient1 = new StorageClient1(trackerServer, null);
            data = storageClient1.downloadFile1(fullFilename);
        } catch (Exception e) {
            throw Lang.makeThrow("[FastdfsService] download file error : %s", e.getMessage());
        } finally {
            if (trackerServer != null)
                fastDfsClientPool.returnObject(trackerServer);
            storageClient1 = null;
        }
        return data;
    }

    /**
     * 下载文件
     *
     * @param fullFilename 文件路径
     * @param outputStream 输出流
     * @return
     */
    public void downLoadFile(String fullFilename, OutputStream outputStream) {
        TrackerServer trackerServer = null;
        StorageClient1 storageClient1 = null;
        try {
            trackerServer = fastDfsClientPool.borrowObject();
            String suffx = fullFilename.substring(fullFilename.lastIndexOf(EXT_SEPERATOR));
            File file = filePool.createFile(suffx);
            storageClient1 = new StorageClient1(trackerServer, null);
            storageClient1.downloadFile1(fullFilename, file.getAbsolutePath());
            Streams.writeAndClose(outputStream, new FileInputStream(file));
        } catch (Exception e) {
            throw Lang.makeThrow("[FastdfsService] download file error : %s", e.getMessage());
        } finally {
            if (trackerServer != null)
                fastDfsClientPool.returnObject(trackerServer);
            storageClient1 = null;
        }
    }

    /**
     * 删除文件
     *
     * @param fullFilename 文件路径
     * @return
     */
    public int deleteFile(String fullFilename) {
        int result = 1;
        TrackerServer trackerServer = null;
        StorageClient1 storageClient1 = null;
        try {
            trackerServer = fastDfsClientPool.borrowObject();
            storageClient1 = new StorageClient1(trackerServer, null);
            result = storageClient1.deleteFile1(fullFilename);
        } catch (Exception e) {
            throw Lang.makeThrow("[FastdfsService] delete file error : %s", e.getMessage());
        } finally {
            if (trackerServer != null)
                fastDfsClientPool.returnObject(trackerServer);
            storageClient1 = null;
        }
        return result;
    }

    /**
     * 上传图片并生成缩略图、水印图
     *
     * @param image     原图
     * @param watermark 水印图
     * @param ext       后缀名
     * @param metaInfo  元信息
     * @return
     */
    public String uploadImage(byte[] image, byte[] watermark, String ext, Map<String, String> metaInfo) {
        return uploadImage(image, watermark, ext, metaInfo, DEFAULT_OPACITY, DEFAULT_LOCATION, DEFAULT_MARGIN);
    }

    /**
     * 上传图片并生成缩略图、水印图
     *
     * @param image     原图
     * @param watermark 水印图
     * @param ext       后缀名
     * @param metaInfo  元信息
     * @param opacity   透明度
     * @param pos       位置
     * @param margin    水印距离四周的边距 默认为0
     * @return
     */
    public String uploadImage(byte[] image, byte[] watermark, String ext, Map<String, String> metaInfo, float opacity, int pos, int margin) {
        String path = "";
        TrackerServer trackerServer = null;
        StorageClient1 storageClient1 = null;
        ByteArrayOutputStream thumbOs = new ByteArrayOutputStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            trackerServer = fastDfsClientPool.borrowObject();
            storageClient1 = new StorageClient1(trackerServer, null);
            NameValuePair data[] = null;
            if (Lang.isNotEmpty(metaInfo)) {
                data = new NameValuePair[metaInfo.size()];
                int index = 0;
                for (Map.Entry<String, String> entry : metaInfo.entrySet()) {
                    data[index] = new NameValuePair(entry.getKey(), entry.getValue());
                    index++;
                }
            }
            //保存原图
            path = storageClient1.uploadFile1(image, ext, data);
            //保存水印图 作为原图的salve file
            BufferedImage bufferedImage = Images.addWatermark(image, watermark, opacity, pos, margin);
            Images.write(bufferedImage, ext, os);
            storageClient1.uploadFile1(path, IMAGE_WATERMARK_SUFFIX, os.toByteArray(), ext, data);
            //保存缩略图
            BufferedImage read = Images.read(image);
            BufferedImage bufferedImageThumb = Images.zoomScale(read, IMAGE_THUMB_WIDTH, IMAGE_THUMB_HEIGHT);
            Images.write(bufferedImageThumb, ext, thumbOs);
            storageClient1.uploadFile1(path, IMAGE_THUMB_SUFFIX, thumbOs.toByteArray(), ext, data);

        } catch (Exception e) {
            throw Lang.makeThrow("[FastdfsService] upload images error : %s", e.getMessage());
        } finally {
            Streams.safeClose(os);
            Streams.safeClose(thumbOs);
            try {
                if (trackerServer != null)
                    fastDfsClientPool.returnObject(trackerServer);
                storageClient1 = null;
            } catch (Exception e) {
                log.error(e);
            }
        }
        return path;
    }

}

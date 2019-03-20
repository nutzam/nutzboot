package org.nutz.boot.starter.fastdfs;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.TrackerServer;
import org.nutz.img.Images;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Properties;

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
    private static final String SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR = "/";
    private static final String EXT_SEPERATOR = ".";

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
        IMAGE_WATERMARK_SUFFIX = conf.get(PRE + "image.waterMarkSuffix", "-wmark");
        IMAGE_THUMB_SUFFIX = conf.get(PRE + "image.thumbSuffix", "-thumb");
        IMAGE_THUMB_WIDTH = conf.getInt(PRE + "image.thumbWidth", 150);
        IMAGE_THUMB_HEIGHT = conf.getInt(PRE + "image.thumbHeight", 150);
        GenericObjectPoolConfig cfg = new GenericObjectPoolConfig();
        cfg.setMaxIdle(conf.getInt(PRE + "pool.maxIdle", 10));
        cfg.setMinIdle(conf.getInt(PRE + "pool.minIdle", 1));
        cfg.setMaxTotal(conf.getInt(PRE + "pool.maxTotal", 20));
        cfg.setMaxWaitMillis(conf.getInt(PRE + "pool.maxWaitMillis", 6000));
        fastDfsClientFactory = new FastDfsClientFactory(prop);
        fastDfsClientPool = new FastDfsClientPool(fastDfsClientFactory, cfg);
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
     * @param type     0-原图 1水印图 2缩略图
     * @return
     */
    public String generateTokenUrl(String filename, int type) {
        try {
            String baseUrl = conf.get(PRE + "http_token_base_url");
            String secretKey = conf.get(PRE + "http_secret_key");
            if (Strings.isBlank(filename) || Strings.isBlank(secretKey) || Strings.isBlank(baseUrl)) {
                log.info("[FastdfsService] filename & http_token_base_url & http_secret_key must not empty");
                return "";
            }
            int pos = filename.indexOf(SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR);
            String tokenFilename = filename.substring(pos + 1);
            tokenFilename = getFileName(type, tokenFilename);
            filename = getFileName(type, filename);
            long ts = Times.getTS();
            StringBuilder sb = new StringBuilder();
            sb.append(baseUrl);
            if (!baseUrl.endsWith(SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR))
                sb.append(SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR);
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
        byte[] bsFilename = filename.getBytes(conf.get(PRE + "charset", "UTF-8"));
        byte[] bsKey = secret_key.getBytes(conf.get(PRE + "charset", "UTF-8"));
        byte[] bsTimestamp = (new Long(timestamp)).toString().getBytes(conf.get(PRE + "charset", "UTF-8"));
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
     * 上传文件
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
     * 上传从文件
     *
     * @param file         从文件
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
        ByteArrayInputStream is = new ByteArrayInputStream(image);
        ByteArrayInputStream waterIs = new ByteArrayInputStream(watermark);
        ByteArrayOutputStream thumbOs = new ByteArrayOutputStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            BufferedImage bufferedImage = Images.addWatermark(image, watermark, opacity, pos, margin);
            Images.write(bufferedImage, ext, os);
            byte[] waterFile = os.toByteArray();
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
            storageClient1.uploadFile1(path, IMAGE_WATERMARK_SUFFIX, waterFile, ext, data);
            //保存缩略图
            is.reset();
            BufferedImage read = Images.read(is);
            BufferedImage bufferedImageThumb = Images.zoomScale(read, IMAGE_THUMB_WIDTH, IMAGE_THUMB_HEIGHT);
            Images.write(bufferedImageThumb, ext, thumbOs);
            storageClient1.uploadFile1(path, IMAGE_THUMB_SUFFIX, thumbOs.toByteArray(), ext, data);

        } catch (Exception e) {
            throw Lang.makeThrow("[FastdfsService] upload images error : %s", e.getMessage());
        } finally {
            Streams.safeClose(is);
            Streams.safeClose(waterIs);
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

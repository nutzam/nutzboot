# nutzboot-starter-fastdfs

* 作者：曹石 & 大鲨鱼
* 使用方法，直接注入即可
```java
@Inject
private FastdfsService fastdfsService;
```
* 配置文件
```text


fastdfs.connect_timeout_in_seconds=20
fastdfs.network_timeout_in_seconds=20
fastdfs.charset=UTF-8
fastdfs.http_anti_steal_token=true
fastdfs.http_secret_key=FastDFS666666
fastdfs.http_token_base_url=http://127.0.0.1:8001
fastdfs.http_tracker_http_port=16666
fastdfs.tracker_servers=127.0.0.1:22122

fastdfs.pool.maxIdle=10
fastdfs.pool.minIdle=1
fastdfs.pool.maxTotal=15
fastdfs.pool.maxWaitMillis=6000
fastdfs.image.waterMarkSuffix=-wmark
fastdfs.image.thumbSuffix=-thumb
fastdfs.image.thumbWidth=150
fastdfs.image.thumbHeight=150

```
package org.nutz.boot.starter.fastdfs;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.fastdfs.TrackerServer;

public class FastDfsClientPool extends GenericObjectPool<TrackerServer> {

    public FastDfsClientPool(PooledObjectFactory<TrackerServer> factory) {
        super(factory);
    }

    public FastDfsClientPool(PooledObjectFactory<TrackerServer> factory, GenericObjectPoolConfig<TrackerServer> config) {
        super(factory, config);
    }

    public FastDfsClientPool(PooledObjectFactory<TrackerServer> factory, GenericObjectPoolConfig<TrackerServer> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}

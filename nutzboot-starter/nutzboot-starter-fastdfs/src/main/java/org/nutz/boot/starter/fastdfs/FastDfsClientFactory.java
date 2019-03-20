package org.nutz.boot.starter.fastdfs;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.ProtoCommon;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.net.SocketAddress;
import java.util.Properties;

public class FastDfsClientFactory implements PooledObjectFactory<TrackerServer> {
    private Log log = Logs.get();

    public FastDfsClientFactory(Properties props) {
        try {
            ClientGlobal.initByProperties(props);
        } catch (Exception e) {
            log.error("init pool factory error", e);
        }
    }

    @Override
    public PooledObject<TrackerServer> makeObject() throws Exception {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        return new DefaultPooledObject<>(trackerServer);
    }

    @Override
    public void destroyObject(PooledObject<TrackerServer> pooledObject) throws Exception {
        TrackerServer trackerServer = pooledObject.getObject();
        if (trackerServer != null && trackerServer.getSocket().isConnected()) {
            ProtoCommon.closeSocket(trackerServer.getSocket());
        }
    }

    @Override
    public boolean validateObject(PooledObject<TrackerServer> pooledObject) {
        try {
            TrackerServer trackerServer = pooledObject.getObject();
            return ProtoCommon.activeTest(trackerServer.getSocket());
        } catch (Exception e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public void activateObject(PooledObject<TrackerServer> pooledObject) throws Exception {
        TrackerServer trackerServer = pooledObject.getObject();
        if (trackerServer != null) {
            if (trackerServer.getSocket() != null && !trackerServer.getSocket().isConnected()) {
                SocketAddress socketAddress = trackerServer.getSocket().getRemoteSocketAddress();
                if (socketAddress != null) {
                    trackerServer.getSocket().connect(socketAddress);
                }
            }
        } else {
            this.makeObject();
        }

    }

    @Override
    public void passivateObject(PooledObject<TrackerServer> pooledObject) throws Exception {

    }
}

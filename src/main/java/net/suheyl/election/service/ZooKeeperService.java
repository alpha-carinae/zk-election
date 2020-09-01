package net.suheyl.election.service;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Service
public class ZooKeeperService implements Watcher{

    private ZooKeeper zooKeeper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${spring.cloud.zookeeper.connect-string}")
    private String zookeeperConnectString;

    @Value(value = "${zookeeper.session.timeout}")
    private int zookeeperSessionTimeout;

    @Value(value = "${server.port}")
    private String port;

    @PostConstruct
    private void init() {
        try {
            zooKeeper = new ZooKeeper(zookeeperConnectString, zookeeperSessionTimeout, this);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String createNode(String path, boolean watch, boolean isEphemeralAndSequential) throws KeeperException, InterruptedException {

        Stat stat = zooKeeper.exists(path, watch);

        if (stat == null) {
            CreateMode createMode = isEphemeralAndSequential ? CreateMode.EPHEMERAL_SEQUENTIAL : CreateMode.PERSISTENT;
            String data = null;
            try {
                data = InetAddress.getLocalHost().getCanonicalHostName() + ":" + port;
            } catch (UnknownHostException e) {
                logger.error("Could not get canonical host name!", e);
            }
            return zooKeeper.create(path, data == null ? null : data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
        }

        return path;
    }

    public boolean watchNode(String path, boolean watch) throws KeeperException, InterruptedException {

        Stat stat = zooKeeper.exists(path, watch);
        return stat != null;
    }

    public boolean watchNode(String path, Watcher watcher) throws KeeperException, InterruptedException {

        Stat stat = zooKeeper.exists(path, watcher);
        return stat != null;
    }

    public List<String> getChildren(String path, boolean watch) throws KeeperException, InterruptedException {

        return zooKeeper.getChildren(path, watch);
    }

    @Override
    public void process(WatchedEvent event) {

        logger.info("Received event: {}", event.toString());

    }
}

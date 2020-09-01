package net.suheyl.election.entity;

import net.suheyl.election.service.ZooKeeperService;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Node {

    public static final String NODE_PREFIX = "n_";

    @Value(value = "${zookeeper.application-root-path}")
    private String applicationRootNode;

    @Value(value = "${zookeeper.leader-election-path}")
    private String leaderElectionNode;

    @Value(value = "${node.id}")
    private String id;

    @Autowired
    private ZooKeeperService zooKeeperService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String nodeFullPath;

    private String watchNodePath;

    public void makeLeaderOffer() throws KeeperException, InterruptedException {

        nodeFullPath = zooKeeperService.createNode(applicationRootNode + leaderElectionNode + "/" + NODE_PREFIX, false, true);

        logger.info("Node {} made leader offer: {}", id, nodeFullPath);
    }

    public String getNodePath() {
        return nodeFullPath.substring(nodeFullPath.lastIndexOf("/") + 1);
    }

    public String getId() {
        return id;
    }

    public void setWatchNodePath(String watchNodePath) {
        this.watchNodePath = watchNodePath;
    }

    public String getWatchNodePath() {
        return watchNodePath;
    }

    public String getWatchNodeFullPath() {
        return applicationRootNode + leaderElectionNode + "/" + watchNodePath;
    }
}

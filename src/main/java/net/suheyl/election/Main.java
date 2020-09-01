package net.suheyl.election;

import net.suheyl.election.entity.Node;
import net.suheyl.election.service.ZooKeeperService;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Component
public class Main implements Watcher {

    @Autowired
    private ZooKeeperService zooKeeperService;

    @Value(value = "${zookeeper.application-root-path}")
    private String applicationRootNode;

    @Value(value = "${zookeeper.leader-election-path}")
    private String leaderElectionNode;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Node node;

    @PostConstruct
    public void initialize() {
        try {
            String rootPath = zooKeeperService.createNode(applicationRootNode, false, false);
            if (rootPath != null) {
                logger.info("created application root path: " + rootPath);

                String electionPath = zooKeeperService.createNode(applicationRootNode + leaderElectionNode, false, false);
                logger.info("created election path: " + electionPath);
            }

            node.makeLeaderOffer();
            performElection();

        } catch (KeeperException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void performElection() {

        try {
            List<String> childNodePaths = zooKeeperService.getChildren(applicationRootNode + leaderElectionNode, false);
            Collections.sort(childNodePaths);

            logger.info("Number of nodes: {}", childNodePaths.size());

            int nodePlaceInLine = childNodePaths.indexOf(node.getNodePath());

            if (nodePlaceInLine == 0) {

                logger.info("LEADER: Node {}", node.getId());
            } else {

                node.setWatchNodePath(childNodePaths.get(nodePlaceInLine - 1));

                logger.info("FOLLOWER: Node {} | Setting watch on {}...", node.getId(), node.getWatchNodePath());

                boolean watchSuccessful = zooKeeperService.watchNode(node.getWatchNodeFullPath(), this);
                if (!watchSuccessful) {
                    logger.error("Probably node gone missing before setting watch, perform election again...");
                    performElection();
                }
            }

        } catch (KeeperException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void process(WatchedEvent event) {

        if (event.getType() == Event.EventType.NodeDeleted) {
            logger.info("EVENT: Node {} deleted", event.getPath());

            if (event.getPath().equals(node.getWatchNodeFullPath())) {
                performElection();
            }
        }
    }
}

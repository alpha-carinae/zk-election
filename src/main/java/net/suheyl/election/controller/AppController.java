package net.suheyl.election.controller;

import net.suheyl.election.entity.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @Autowired
    private Node node;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/request1")
    public String testCall1() {

        String response = String.format("[NODE-%s] - request1", node.getId());
        logger.info(response);
        return response;
    }

    @GetMapping("/request2")
    public String testCall2() {

        String response = String.format("[NODE-%s] - request2", node.getId());
        logger.info(response);
        return response;
    }

    @GetMapping("/request3")
    public String testCall3() {

        String response = String.format("[NODE-%s] - request3", node.getId());
        logger.info(response);
        return response;
    }
}

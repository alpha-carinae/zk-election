package net.suheyl.election;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ZkElectionApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ZkElectionApplication.class, args);
    }

}

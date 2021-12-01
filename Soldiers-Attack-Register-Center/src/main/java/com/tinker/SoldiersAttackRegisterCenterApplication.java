package com.tinker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author t.k
 * @date 2021/12/1 14:49
 */
@SpringBootApplication
@EnableEurekaServer
public class SoldiersAttackRegisterCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoldiersAttackRegisterCenterApplication.class, args);
    }
}

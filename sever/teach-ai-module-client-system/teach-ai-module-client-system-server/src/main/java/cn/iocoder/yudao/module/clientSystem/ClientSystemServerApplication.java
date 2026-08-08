package cn.iocoder.teach-ai.module.clientSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClientSystemServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientSystemServerApplication.class, args);
    }

}

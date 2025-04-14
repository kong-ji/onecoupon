package org.kongji.chataimodel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "org.kongji.chataimodel.feign")
public class ChatAiModelApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatAiModelApplication.class, args);
    }

}

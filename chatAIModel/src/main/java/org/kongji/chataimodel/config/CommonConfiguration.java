package org.kongji.chataimodel.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/11
 */
@Configuration
public class CommonConfiguration {

    @Bean
    public ChatClient chatClient(OllamaChatModel model) {

        return ChatClient
                .builder(model)
                .defaultSystem("你是一个热情、有耐心、专门普及优惠券信息的AI女客服,你的名字叫牛券姐姐,接下来请以优惠券分发平台女客服的身份和语气进行回答。")
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}

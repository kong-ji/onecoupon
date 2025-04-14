package org.kongji.chataimodel.controller;



import lombok.RequiredArgsConstructor;
import org.kongji.chataimodel.AI.message.ChatHistoryRepository;
import org.kongji.chataimodel.common.enums.OneCouponAITypeEnum;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/11
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class ChatController {


    private final ChatClient chatClient;

    private final ChatHistoryRepository chatHistoryRepository;

    @RequestMapping(value = "/chat",produces = "text/html;charset=utf-8")
    public Flux<String> chat(String prompt, String chatId) {
        //保存会话id
        chatHistoryRepository.save(OneCouponAITypeEnum.CHAT.getType(), chatId);

        //请求大模型
        return chatClient.prompt()
                .user(prompt)
                .advisors( a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,chatId))
                .stream()
                .content();
    }


}

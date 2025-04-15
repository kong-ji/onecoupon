package org.kongji.chataimodel.controller;

import com.kongji.onecoupon.framework.result.Result;
import lombok.RequiredArgsConstructor;
import org.kongji.chataimodel.AI.dto.resp.CouponTemplateQueryRespDTO;
import org.kongji.chataimodel.AI.message.ChatHistoryRepository;
import org.kongji.chataimodel.common.enums.OneCouponAITypeEnum;
import org.kongji.chataimodel.feign.OneCouponTemplateClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/14
 */
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class OneCouponAIRemindController {

    private final ChatClient serviceChatClient;
    private final ChatHistoryRepository chatHistoryRepository;

    
    @RequestMapping(value = "/service", produces = "text/html;charset=utf-8")
    public Flux<String> service(String prompt, String chatId) {

        chatHistoryRepository.save(OneCouponAITypeEnum.SERVICE.getType(), chatId);

        return   serviceChatClient.prompt()
                .user(prompt)
                .advisors(a->a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content();
    }
}


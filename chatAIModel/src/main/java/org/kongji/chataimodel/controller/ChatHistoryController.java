package org.kongji.chataimodel.controller;

import lombok.RequiredArgsConstructor;
import org.kongji.chataimodel.AI.dto.resp.OneCouponAIHistoryMessageDTO;
import org.kongji.chataimodel.AI.message.ChatHistoryRepository;
import org.kongji.chataimodel.AI.message.OneCouponChatMemory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/14
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/history")
public class ChatHistoryController {
    private final ChatHistoryRepository chatHistoryRepository;
    private final ApplicationContext applicationContext;

    @GetMapping("/{type}")
    public List<String> findChatHistoryList(@PathVariable String type){
        return chatHistoryRepository.getChatIds(type);
    }

    @GetMapping("/{type}/{chatId}")
    public List<OneCouponAIHistoryMessageDTO> findChatHistoryDetail(@PathVariable String type, @PathVariable String chatId){
        ChatMemory chatMemory = getBeanType(type);
        List<Message> messages = chatMemory.get(chatId, Integer.MAX_VALUE);
        if (messages == null) {
            return null;
        }
        return messages.stream().map(OneCouponAIHistoryMessageDTO::new).toList();
    }
    private OneCouponChatMemory getBeanType(String type) {
        Map<String, OneCouponChatMemory> maps = applicationContext.getBeansOfType(OneCouponChatMemory.class);
        switch (type){
            case "chat":
                return maps.get("chatMemory");
            case "service":
                return maps.get("serviceMemory");
            case "game":
                return maps.get("gameMemory");
            default:
                return maps.get("chatMemory");
        }
    }
}

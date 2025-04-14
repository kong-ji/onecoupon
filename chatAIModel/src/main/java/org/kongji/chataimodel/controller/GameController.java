package org.kongji.chataimodel.controller;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.kongji.chataimodel.common.constant.SystemConstant;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/14
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class GameController {


    private final ChatClient gameChatClient;

    @RequestMapping(value = "/game", produces = "text/html;charset=utf-8")
    public Flux<String> chat(String prompt, String chatId) {
        return gameChatClient.prompt(SystemConstant.GAME_SYSTEM_PROMPT)
                .user(prompt)
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content();

    }
}

package org.kongji.chataimodel.config;


import io.micrometer.observation.ObservationRegistry;
import org.kongji.chataimodel.AI.message.OneCouponChatMemory;
import org.kongji.chataimodel.AI.model.AlibabaOpenAiChatModel;
import org.kongji.chataimodel.AI.tools.OneCouponTool;
import org.kongji.chataimodel.common.constant.SystemConstant;
import org.kongji.chataimodel.common.enums.OneCouponAIHistoryKeyEnum;
import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/11
 */

/**
 *
 * AI 客户端配置
 */
@Configuration
public class CommonConfiguration {
    @Autowired
    RedisTemplate<String, Message> redisTemplate;

    @Bean
    ChatMemory chatMemory() {
        return new OneCouponChatMemory(redisTemplate, 0);
    }
    @Bean
    ChatMemory gameMemory() {
        return new OneCouponChatMemory(redisTemplate, 1);
    }
    @Bean
    ChatMemory serviceMemory() {
        return new OneCouponChatMemory(redisTemplate, 2);
    }
    @Bean
    public ChatClient chatClient(OllamaChatModel model, ChatMemory chatMemory) {

        return ChatClient
                .builder(model)
                .defaultSystem("你是一全能AI助手")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(chatMemory)
                    )
                .build();
    }

    @Bean
    public ChatClient gameChatClient(OpenAiChatModel model, ChatMemory gameMemory) {
        return ChatClient.builder(model)
                .defaultSystem(SystemConstant.GAME_SYSTEM_PROMPT)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultAdvisors(new MessageChatMemoryAdvisor(gameMemory))
                .build();
    }

    @Bean
    public ChatClient serviceChatClient(AlibabaOpenAiChatModel model, ChatMemory serviceMemory,OneCouponTool oneCouponTool) {
        return ChatClient.builder(model)
                .defaultSystem(SystemConstant.SERVICE_SYSTEM_PROMPT)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultAdvisors(new MessageChatMemoryAdvisor(serviceMemory))
                .defaultTools(oneCouponTool)
                .build();
    }

    @Bean
    public AlibabaOpenAiChatModel alibabaOpenAiChatModel(OpenAiConnectionProperties commonProperties, OpenAiChatProperties chatProperties, ObjectProvider<RestClient.Builder> restClientBuilderProvider, ObjectProvider<WebClient.Builder> webClientBuilderProvider, ToolCallingManager toolCallingManager, RetryTemplate retryTemplate, ResponseErrorHandler responseErrorHandler, ObjectProvider<ObservationRegistry> observationRegistry, ObjectProvider<ChatModelObservationConvention> observationConvention) {
        String baseUrl = StringUtils.hasText(chatProperties.getBaseUrl()) ? chatProperties.getBaseUrl() : commonProperties.getBaseUrl();
        String apiKey = StringUtils.hasText(chatProperties.getApiKey()) ? chatProperties.getApiKey() : commonProperties.getApiKey();
        String projectId = StringUtils.hasText(chatProperties.getProjectId()) ? chatProperties.getProjectId() : commonProperties.getProjectId();
        String organizationId = StringUtils.hasText(chatProperties.getOrganizationId()) ? chatProperties.getOrganizationId() : commonProperties.getOrganizationId();
        Map<String, List<String>> connectionHeaders = new HashMap<>();
        if (StringUtils.hasText(projectId)) {
            connectionHeaders.put("OpenAI-Project", List.of(projectId));
        }

        if (StringUtils.hasText(organizationId)) {
            connectionHeaders.put("OpenAI-Organization", List.of(organizationId));
        }
        RestClient.Builder restClientBuilder = restClientBuilderProvider.getIfAvailable(RestClient::builder);
        WebClient.Builder webClientBuilder = webClientBuilderProvider.getIfAvailable(WebClient::builder);
        OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(baseUrl).apiKey(new SimpleApiKey(apiKey)).headers(CollectionUtils.toMultiValueMap(connectionHeaders)).completionsPath(chatProperties.getCompletionsPath()).embeddingsPath("/v1/embeddings").restClientBuilder(restClientBuilder).webClientBuilder(webClientBuilder).responseErrorHandler(responseErrorHandler).build();
        AlibabaOpenAiChatModel chatModel = AlibabaOpenAiChatModel.builder().openAiApi(openAiApi).defaultOptions(chatProperties.getOptions()).toolCallingManager(toolCallingManager).retryTemplate(retryTemplate).observationRegistry((ObservationRegistry) observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP)).build();
        Objects.requireNonNull(chatModel);
        observationConvention.ifAvailable(chatModel::setObservationConvention);
        return chatModel;
    }
}

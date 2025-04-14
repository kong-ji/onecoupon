package org.kongji.chataimodel.AI.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.Message;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OneCouponAIHistoryMessageDTO {
    private String role;
    private String content;

    public OneCouponAIHistoryMessageDTO(Message message) {
        switch (message.getMessageType()) {
            case USER:
                role = "user";
                break;
            case ASSISTANT:
                role = "assistant";
                break;
            case SYSTEM:
                role = "system";
                break;
            default:
                role = "unknown";
                break;
        }
        this.content = message.getText();
    }
}

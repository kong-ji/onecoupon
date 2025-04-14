package org.kongji.chataimodel.AI.message;

import java.util.List;

/**
 * @author kong-ji
 * @version 1.0
 * @data 2025/4/14
 */
public interface ChatHistoryRepository {
    /**
     * 保存会话id
     * @param type 业务类型
     * @param chatId 会话id
     */
    void save(String type, String chatId);

    /**
     * 获取指定类型的会话ids
     * @param type 业务类型
     * @return 会话集合列表
     */
    List<String> getChatIds(String type);
}

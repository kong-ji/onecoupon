

package com.kongji.onecoupon.merchant.admin.mq.base;

import lombok.*;

import java.io.Serializable;

/**
 * 消息体包装器
 * <p>
 * 作者：kongji
 *    
 *     
 */
@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@RequiredArgsConstructor
public final class MessageWrapper<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息发送 Keys
     */
    @NonNull
    private String keys;

    /**
     * 消息体
     */
    @NonNull
    private T message;

    /**
     * 消息发送时间
     */
    private Long timestamp = System.currentTimeMillis();
}

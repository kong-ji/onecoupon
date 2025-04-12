

package com.kongji.onecoupon.merchant.admin.service.basics.chain;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 抽象商家后管业务责任链组件
 * @param <T>
 */
public interface MerchantAdminAbstractChainHandler<T> extends Ordered {
    /**
     * 责任链执行逻辑
     * @param requestParam 入参
     */
    void handler(T requestParam);

    /**
     *
     * @return 责任链标识符
     */
    String mark();

}
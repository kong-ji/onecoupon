  

package com.kongji.onecoupon.merchant.admin.service.basics.chain;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家后管责任链上下文容器
 */
@Component
public class MerchantAdminChainContext<T> implements  CommandLineRunner{
    @Autowired
    private ApplicationContext applicationContext;
    //存储责任链
    private final Map<String, List<MerchantAdminAbstractChainHandler>> abstractChainHandlerContainer = new HashMap<>();

    public void handler(String mark,T request){
        //判断责任链是否初始化
        List<MerchantAdminAbstractChainHandler> merchantAdminAbstractChainHandlers = abstractChainHandlerContainer.get(mark);
        if(ObjectUtil.isEmpty(merchantAdminAbstractChainHandlers)){
            throw new RuntimeException(String.format("[%s] Chain of Responsibility ID is undefined.", mark));
        }
        //依次调用对应责任链的处理器
        merchantAdminAbstractChainHandlers.forEach(
                each -> each.handler(request)
        );
    }


//    /**
//     * 初始化类的应用上下文属性
//     * @param applicationContext
//     * @throws BeansException
//     */
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }

    /**
     * 初始化责任链
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        //获取责任链的处理器bean
        Map<String, MerchantAdminAbstractChainHandler> beansOfType = applicationContext.getBeansOfType(MerchantAdminAbstractChainHandler.class);
        //将责任链写入责任链map
        beansOfType.forEach((beanName,bean)->{
            //判断map中是否存在，不存在则写入
            List<MerchantAdminAbstractChainHandler> orDefault = abstractChainHandlerContainer.getOrDefault(bean.mark(), new ArrayList<>());
            orDefault.add(bean);
            abstractChainHandlerContainer.put(bean.mark(), orDefault);
        });
        //根据order对每条责任链进行排序
        abstractChainHandlerContainer.forEach((mark,handlers)->{
            handlers.sort(Comparator.comparing(each->each.getOrder()));
        });
    }
}
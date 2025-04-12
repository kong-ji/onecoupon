

package com.kongji.onecoupon.framework.errorcode;

/**
 * 平台错误码｜定义错误码抽象接口，由各错误码类实现接口方法
 * <p>
 * 作者：kongji
 * 加星球群：早加入就是优势！500人内部沟通群，分享的知识总有你需要的 <a href="https://t.zsxq.com/cw7b9" />
 *
 */
public interface IErrorCode {

    /**
     * 错误码
     */
    String code();

    /**
     * 错误信息
     */
    String message();
}

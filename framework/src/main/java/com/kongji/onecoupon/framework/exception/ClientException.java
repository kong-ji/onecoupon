  

package com.kongji.onecoupon.framework.exception;

import com.kongji.onecoupon.framework.errorcode.BaseErrorCode;
import com.kongji.onecoupon.framework.errorcode.IErrorCode;

/**
 * 客户端异常｜用户发起调用请求后因客户端提交参数或其他客户端问题导致的异常
 * <p>
 * 作者：kongji
 * 加星球群：早加入就是优势！500人内部沟通群，分享的知识总有你需要的 <a href="https://t.zsxq.com/cw7b9" />
 *
 */
public class ClientException extends AbstractException {

    public ClientException(IErrorCode errorCode) {
        this(null, null, errorCode);
    }

    public ClientException(String message) {
        this(message, null, BaseErrorCode.CLIENT_ERROR);
    }

    public ClientException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ClientException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "ClientException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}

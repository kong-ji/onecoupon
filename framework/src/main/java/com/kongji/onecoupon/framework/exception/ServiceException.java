  

package com.kongji.onecoupon.framework.exception;

import com.kongji.onecoupon.framework.errorcode.BaseErrorCode;
import com.kongji.onecoupon.framework.errorcode.IErrorCode;

import java.util.Optional;

/**
 * 服务端运行异常｜请求运行过程中出现的不符合业务预期的异常
 * <p>
 * 作者：kongji
 * 加星球群：早加入就是优势！500人内部沟通群，分享的知识总有你需要的 <a href="https://t.zsxq.com/cw7b9" />
 *
 */
public class ServiceException extends AbstractException {

    public ServiceException(String message) {
        this(message, null, BaseErrorCode.SERVICE_ERROR);
    }

    public ServiceException(IErrorCode errorCode) {
        this(null, errorCode);
    }

    public ServiceException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ServiceException(String message, Throwable throwable, IErrorCode errorCode) {
        super(Optional.ofNullable(message).orElse(errorCode.message()), throwable, errorCode);
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}

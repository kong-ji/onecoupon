

package com.kongji.onecoupon.framework.exception;

import com.kongji.onecoupon.framework.errorcode.BaseErrorCode;
import com.kongji.onecoupon.framework.errorcode.IErrorCode;

/**
 * 远程服务调用异常｜比如订单调用支付失败，向上抛出的异常应该是远程服务调用异常
 * <p>
 * 作者：kongji
 * 加星球群：早加入就是优势！500人内部沟通群，分享的知识总有你需要的 <a href="https://t.zsxq.com/cw7b9" />
 *
 */
public class RemoteException extends AbstractException {

    public RemoteException(String message) {
        this(message, null, BaseErrorCode.REMOTE_ERROR);
    }

    public RemoteException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public RemoteException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "RemoteException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}

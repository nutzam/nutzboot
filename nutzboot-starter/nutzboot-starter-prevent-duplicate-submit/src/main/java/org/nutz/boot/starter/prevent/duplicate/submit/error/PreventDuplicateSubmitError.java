package org.nutz.boot.starter.prevent.duplicate.submit.error;

/**
 * @author 黄川 306955302@qq.com
 * @date: 2018/8/14
 * 描述此类：
 */
public class PreventDuplicateSubmitError extends Exception {

    private static final long serialVersionUID = 1L;

    String detailMessage;

    public PreventDuplicateSubmitError(String msg) {
        super(msg);
        this.detailMessage = msg;
    }

    public String getDetailMessage() {
        return this.detailMessage;
    }
}

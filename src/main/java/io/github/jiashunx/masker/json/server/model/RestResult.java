package io.github.jiashunx.masker.json.server.model;

/**
 * 响应对象
 * @author jiashunx
 */
public class RestResult {

    /**
     * 成功与否
     */
    protected boolean success;
    /**
     * 响应码
     */
    protected int code = 0;
    /**
     * 响应信息
     */
    protected String message = "";
    /**
     * 响应报文
     */
    protected Object data = "";

    public RestResult() {}

    public static RestResult ok() {
        return new RestResult().setCode(0).setMessage("").setSuccess(true).setData("");
    }

    public static RestResult ok(Object data) {
        return ok().setData(data);
    }

    public static RestResult ok(Object data, String message) {
        return ok(data).setMessage(message);
    }

    public static RestResult okWithMessage(String message) {
        return ok().setMessage(message);
    }

    public static RestResult fail() {
        return new RestResult().setCode(1).setMessage("").setSuccess(false).setData("");
    }

    public static RestResult failWithMessage(String message) {
        return fail().setMessage(message);
    }

    public static RestResult fail(int code, String message) {
        return fail().setCode(code).setMessage(message);
    }

    public boolean isSuccess() {
        return success;
    }

    public RestResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public int getCode() {
        return code;
    }

    public RestResult setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public RestResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public RestResult setData(Object data) {
        this.data = data;
        return this;
    }

}

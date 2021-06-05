package cn.lbin.miaosha.execption;

public class GlobalException extends RuntimeException{
    private String msg;

    public GlobalException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}

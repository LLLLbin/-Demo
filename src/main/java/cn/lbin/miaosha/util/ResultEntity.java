package cn.lbin.miaosha.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultEntity<T> {
    public static final String SUCCESS="SUCCESS";
    public static final String FAILED="FAILED";
    private String message;
    private T data;
    private String result;

    public static <T> ResultEntity<T> successWithoutData(){
        return new ResultEntity<T>(null,null,SUCCESS);
    }

    public static <T> ResultEntity<T> successWithData(T data){
        return new ResultEntity<T>(null,data,SUCCESS);
    }

    public static <T> ResultEntity<T> failed(String message){
        return new ResultEntity<T>(message,null,FAILED);
    }
}

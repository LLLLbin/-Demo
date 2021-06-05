package cn.lbin.miaosha.execption;

import cn.lbin.miaosha.constant.MsgConstant;
import cn.lbin.miaosha.util.ResultEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ResponseBody
@ControllerAdvice
public class GlobalExecptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResultEntity exceptionHandler(Exception e) {
        if (e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return ResultEntity.failed(ex.getMsg());
        }
        if (e instanceof BindException) {
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String message = error.getDefaultMessage();
            return ResultEntity.failed(message);
        } else {
            return ResultEntity.failed(MsgConstant.ATTR_NAME_MESSAGE);
        }

    }
}

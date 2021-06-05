package cn.lbin.miaosha.validate;

import cn.lbin.miaosha.util.ValidateMobile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        boolean required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (required) {
            return ValidateMobile.isMobile(value);
        } else {
            if (value == null || value.length() == 0) {
                return true;
            } else {
                return ValidateMobile.isMobile(value);
            }
        }
    }
}

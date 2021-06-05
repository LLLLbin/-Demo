package cn.lbin.miaosha.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateMobile {
    public static Pattern pattern=Pattern.compile("1\\d{10}");

    public static boolean isMobile(String stc){
        Matcher matcher = pattern.matcher(stc);
        return matcher.matches();
    }

}

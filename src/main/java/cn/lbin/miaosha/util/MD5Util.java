package cn.lbin.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    private static final String salt = "1a2b3c4d";

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    public static String inputPassFromPass(String inputPass) {
        String str = ""+salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String fromPassToDbPass(String fromPass,String salt){
        String str = ""+salt.charAt(0) + salt.charAt(2) + fromPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDbPass(String inputPass,String salt){
        String fromPass = inputPassFromPass(inputPass);
        String dbPass = fromPassToDbPass(fromPass, salt);
        return dbPass;
    }
    public static void main(String[] args) {
//        System.out.println(inputPassToDbPass("121522", "1a2b3c4d5"));
        System.out.println(inputPassFromPass("121522"));//7433b0ee2ed16432b6edacab9e646cbf
        System.out.println(fromPassToDbPass("7433b0ee2ed16432b6edacab9e646cbf","1a2b3c4d"));
        System.out.println(inputPassToDbPass("121522","1a2b3c4d"));
    }
}

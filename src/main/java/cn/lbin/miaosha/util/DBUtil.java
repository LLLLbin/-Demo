package cn.lbin.miaosha.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBUtil {

    private static Logger logger= LoggerFactory.getLogger(DBUtil.class);
    private static Properties props;

    static {
        try {
            InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("application.yml");
            props = new Properties();
            props.load(in);
            in.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConn() throws Exception{
//        String url = props.getProperty("spring.datasource.url");
//        String username = props.getProperty("spring.datasource.username");
//        String password = props.getProperty("spring.datasource.password");
//        String driver = props.getProperty("spring.datasource.driver-class-name");

        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");
        String driver = props.getProperty("driver-class-name");
        Class.forName(driver);
        return DriverManager.getConnection(url,username, password);
    }
}

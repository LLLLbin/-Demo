package cn.lbin.miaosha.dao;

import cn.lbin.miaosha.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {

    @Select("select id,name from user where id =#{id}")
    public User getUserById(@Param("id") int id);

    @Insert("insert into user(id,name) value(#{id},#{name})")
    public Boolean insertUser(User user);
}

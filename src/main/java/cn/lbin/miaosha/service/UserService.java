package cn.lbin.miaosha.service;

import cn.lbin.miaosha.dao.UserDao;
import cn.lbin.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getById(int id){
        return userDao.getUserById(id);
    }

//    @Transactional
    public Boolean tx() {
        User user = new User();
        user.setId(2);
        user.setName("LL");
        userDao.insertUser(user);

        User user1 = new User();
        user1.setId(1);
        user1.setName("BB");
        userDao.insertUser(user1);
        return true;
    }
}

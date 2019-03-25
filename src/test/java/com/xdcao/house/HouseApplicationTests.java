package com.xdcao.house;

import com.xdcao.house.dao.UserMapper;
import com.xdcao.house.entity.User;
import com.xdcao.house.entity.UserExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class HouseApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testUser() {
        List<User> users = userMapper.selectByExample(new UserExample());
        System.out.println(users.size());
        for (User user:users) {
            System.out.println(user.getName());
        }
    }

}

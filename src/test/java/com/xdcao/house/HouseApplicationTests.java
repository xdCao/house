package com.xdcao.house;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.util.StringMap;
import com.xdcao.house.dao.UserMapper;
import com.xdcao.house.entity.User;
import com.xdcao.house.entity.UserExample;
import com.xdcao.house.service.house.QiniuService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class HouseApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QiniuService qiniuService;

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


    @Test
    public void testQiniuUpload() {
        String fileName = "/Users/caohao/IdeaProjects/house/tmp/21553655502_.pic.jpg";
        File file = new File(fileName);
        Assert.assertTrue(file.exists());
        try {
            Response response = qiniuService.uploadFile(file);
            Assert.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQiniuDelete() throws QiniuException {
        String key = "FvOuLjhUINpxPd9BcTyHOHDs_WHs";
        Response delete = qiniuService.delete(key);
        assert delete.isOK();
    }

}

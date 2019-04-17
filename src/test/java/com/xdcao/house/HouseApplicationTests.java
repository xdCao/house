package com.xdcao.house;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.xdcao.house.dao.UserMapper;
import com.xdcao.house.entity.User;
import com.xdcao.house.entity.UserExample;
import com.xdcao.house.service.ServiceMultiRet;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.service.house.IAddressService;
import com.xdcao.house.service.house.impl.QiniuService;
import com.xdcao.house.service.search.BaiduMapLocation;
import com.xdcao.house.service.search.ISearchService;
import com.xdcao.house.web.form.RentSearch;
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
@ActiveProfiles("te")
public class HouseApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private ISearchService searchService;

    @Autowired
    private IAddressService addressService;

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

    @Test
    public void testIndex() {
        boolean success = searchService.index(15,false);
        assert success;
    }


    @Test
    public void testEsQuery() {

        RentSearch search = new RentSearch();
        search.setCityEnName("bj");
        search.setStart(0);
        search.setSize(10);
        ServiceMultiRet<Integer> query = searchService.query(search);
        assert query.getResult() != null;
        assert query.getTotal() == 4;

    }

    @Test
    public void testBaiduMap() {
        String city = "西安";
        String address = "西安市雁塔区太白南路2号西安电子科技大学";
        ServiceResult<BaiduMapLocation> baiduMapLocation = addressService.getBaiduMapLocation(city, address);
        if (baiduMapLocation.isSuccess()) {
            System.out.println("成功");
        }
        System.out.println(baiduMapLocation.getMessage());
    }

}

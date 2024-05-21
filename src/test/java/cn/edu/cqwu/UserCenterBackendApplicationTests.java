package cn.edu.cqwu;
import cn.edu.cqwu.mapper.UserMapper;
import cn.edu.cqwu.model.domain.Upload;
import cn.edu.cqwu.model.domain.User;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserCenterBackendApplicationTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    void contextLoads() {
        int[] array = new int[]{3, 4, 5, 6};
        Gson gson = new Gson();
        String s = gson.toJson(array);
        System.out.println(s);
        String json = "[3, 4, 5, 6]";
        List<Integer> idList = gson.fromJson(json, new TypeToken<List<Integer>>() {
        }.getType());
        for (Integer integer : idList) {
            System.out.println(integer);
        }
    }

    @Test
    public void test() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        Page<User> page = new Page<>(1, 2);
        Page<User> page1 = userMapper.selectPage(page, queryWrapper);
        System.out.println(page1.getCurrent());
        System.out.println(page1.getSize());
        System.out.println(page1.getRecords());
    }

    @Test
    public void test02() {
        for (int i = 0; i < 1000; i++) {
            User user = new User();
            user.setUserAccount("coder" + i);
            user.setUserPassword(SecureUtil.md5("111111"));
            if (i % 2 == 0) {
                user.setGender(1);
            } else {
                user.setGender(0);
            }
            user.setPhone("17823257046");
            user.setEmail("1556517393@qq.com");
            userMapper.insert(user);
        }
    }

    @Test
    public void test03() {
        Upload upload = new Upload();
        System.out.println(upload);
    }

}

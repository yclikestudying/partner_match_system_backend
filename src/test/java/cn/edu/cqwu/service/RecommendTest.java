package cn.edu.cqwu.service;

import cn.edu.cqwu.mapper.TeamMapper;
import cn.edu.cqwu.model.domain.Team;
import cn.edu.cqwu.model.domain.Upload;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author 杨闯
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecommendTest {
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private Upload upload;
    @Test
    public void test() {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        Long count = teamMapper.selectCount(queryWrapper);
        int[] randoms = new int[5];
        int index = 0;
        int flag = 0;
        while (true) {
            int random = (int) (Math.random() * count);
            for (int i : randoms) {
                if (i == random) {
                    flag = 1;
                }
            }
            if (flag == 0) {
                randoms[index++] = random;
            }
            if (index >= 5) {
                break;
            }
            flag = 0;
        }
        for (int random : randoms) {
            System.out.println(random);
        }
    }
    @Test
    public void test02() {
        for (int i = 0; i < 100; i++) {
            int random = (int) (Math.random() * 11);
            System.out.println(random);
        }

    }

    @Test
    public void test03() {
        System.out.println(upload);
    }
}

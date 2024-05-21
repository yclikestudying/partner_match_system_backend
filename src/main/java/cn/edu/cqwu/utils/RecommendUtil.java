package cn.edu.cqwu.utils;

import java.util.List;
import java.util.Objects;

/**
 * @author 杨闯
 */
//相似度推荐用户
public class RecommendUtil {
    public static int minDistance(List<String> list1, List<String> list2){
        int n = list1.size();
        int m = list2.size();

        if(n * m == 0) {
            return n + m;
        }

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++){
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++){
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++){
            for (int j = 1; j < m + 1; j++){
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int left_down = d[i - 1][j - 1];
                if (!Objects.equals(list1.get(i - 1), list2.get(j - 1)))
                    left_down += 1;
                d[i][j] = Math.min(left, Math.min(down, left_down));
            }
        }
        return d[n][m];
    }
}

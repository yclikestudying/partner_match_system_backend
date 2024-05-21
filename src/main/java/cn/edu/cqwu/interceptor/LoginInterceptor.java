package cn.edu.cqwu.interceptor;

import cn.edu.cqwu.common.BaseResponse;
import cn.edu.cqwu.common.CodeMessage;
import cn.edu.cqwu.common.ResultUtils;
import cn.edu.cqwu.constant.UserConstant;
import cn.edu.cqwu.model.vo.user.UserInfoVO;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 杨闯
 * @date 2024-04-03
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoVO userInfoVO = (UserInfoVO) request.getSession().getAttribute(UserConstant.USER_STATUS);
        log.error("sessionid:" + request.getSession().getId());
        if (userInfoVO == null) {
            BaseResponse<Object> baseResponse = ResultUtils.error(CodeMessage.NOT_LOGIN);
            response.setContentType("application/json; charset=UTF-8");
            Gson gson = new Gson();
            String toJson = gson.toJson(baseResponse);
            response.getWriter().write(toJson);
            return false;
        }

        return true;
    }
}
